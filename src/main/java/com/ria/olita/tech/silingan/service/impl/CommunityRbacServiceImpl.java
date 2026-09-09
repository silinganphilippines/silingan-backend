package com.ria.olita.tech.silingan.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ria.olita.tech.silingan.dto.req.AssignStaffRoleRequest;
import com.ria.olita.tech.silingan.dto.req.UpdateCommunityRolePermissionsRequest;
import com.ria.olita.tech.silingan.dto.res.AvailablePermissionsResponse;
import com.ria.olita.tech.silingan.dto.res.AvailablePermissionsResponse.PermissionInfo;
import com.ria.olita.tech.silingan.dto.res.CurrentUserCapabilitiesResponse;
import com.ria.olita.tech.silingan.dto.res.EffectivePermissionsResponse;
import com.ria.olita.tech.silingan.dto.res.StaffRoleAssignmentResponse;
import com.ria.olita.tech.silingan.dto.res.StaffRoleTemplateResponse;
import com.ria.olita.tech.silingan.entity.Community;
import com.ria.olita.tech.silingan.entity.CommunityRolePermissionOverride;
import com.ria.olita.tech.silingan.entity.SilinganRealmRole;
import com.ria.olita.tech.silingan.entity.StaffRole;
import com.ria.olita.tech.silingan.entity.StaffRolePermission;
import com.ria.olita.tech.silingan.entity.User;
import com.ria.olita.tech.silingan.entity.UserCommunity;
import com.ria.olita.tech.silingan.entity.UserCommunityStaffRole;
import com.ria.olita.tech.silingan.entity.rbac.PermissionEnum;
import com.ria.olita.tech.silingan.entity.rbac.PermissionOverrideEffect;
import com.ria.olita.tech.silingan.entity.rbac.StaffRoleCode;
import com.ria.olita.tech.silingan.exception.ForbiddenException;
import com.ria.olita.tech.silingan.exception.NotFoundException;
import com.ria.olita.tech.silingan.repository.CommunityRepository;
import com.ria.olita.tech.silingan.repository.CommunityRolePermissionOverrideRepository;
import com.ria.olita.tech.silingan.repository.StaffRolePermissionRepository;
import com.ria.olita.tech.silingan.repository.StaffRoleRepository;
import com.ria.olita.tech.silingan.repository.UserCommunityPermissionRepository;
import com.ria.olita.tech.silingan.repository.UserCommunityRepository;
import com.ria.olita.tech.silingan.repository.UserCommunityStaffRoleRepository;
import com.ria.olita.tech.silingan.repository.UserRepository;
import com.ria.olita.tech.silingan.security.context.UserContext;
import com.ria.olita.tech.silingan.security.context.UserContextHolder;
import com.ria.olita.tech.silingan.security.scope.StaffGrantGuard;
import com.ria.olita.tech.silingan.service.CommunityRbacService;

import lombok.RequiredArgsConstructor;

/**
 * Central implementation of community-scoped RBAC.
 *
 * <p><b>Permission model.</b> A staff member's authority in a community is resolved from three
 * layers, in order:
 *
 * <ol>
 *   <li>{@code staff_role_permissions} — the global default permission set for a
 *       {@link StaffRoleCode}, seeded at startup from {@link #DEFAULT_ROLE_PERMISSIONS}.</li>
 *   <li>{@code community_role_permission_overrides} — per-community ALLOW/DENY deltas that let one
 *       community tailor a shared role template without forking it.</li>
 *   <li>{@code user_community_permissions} — additive per-user grants for one-off exceptions.</li>
 * </ol>
 *
 * <p>Only steps 1 and 2 can subtract a permission; direct user grants are strictly additive, so
 * there is no user-level DENY.
 *
 * <p><b>Why templates plus overrides</b> rather than per-community role rows: role codes stay
 * comparable across the platform (a "PMO Staff" means the same thing everywhere and reporting can
 * aggregate on it), while each community can still diverge. Storing only the delta also means a
 * change to a platform default automatically propagates to every community that has not customised
 * that particular permission.
 *
 * <p><b>Callers.</b> {@code CommunityRbacController} and {@code StaffRoleAssignmentController} for
 * administration, and {@code UserContextFilter} which calls
 * {@link #resolveEffectivePermissions(UUID, UUID)} on every authenticated request to build the
 * request-scoped {@code CommunityAccess}.
 *
 * <p><b>Security.</b> Tenant isolation for the HTTP entry points is enforced upstream by
 * {@code CommunityScopeAspect}; this class additionally applies {@link StaffGrantGuard} so a caller
 * cannot grant authority they do not hold. See {@code docs/SECURITY_GUARDS.md}.
 */
@Service
@RequiredArgsConstructor
public class CommunityRbacServiceImpl implements CommunityRbacService {
	private final StaffRoleRepository staffRoleRepository;
	private final StaffRolePermissionRepository staffRolePermissionRepository;
	private final CommunityRolePermissionOverrideRepository communityRolePermissionOverrideRepository;
	private final UserCommunityStaffRoleRepository userCommunityStaffRoleRepository;
	private final UserCommunityPermissionRepository userCommunityPermissionRepository;
	private final UserCommunityRepository userCommunityRepository;
	private final UserRepository userRepository;
	private final CommunityRepository communityRepository;
	private final StaffGrantGuard staffGrantGuard;

	private static final Map<StaffRoleCode, Set<PermissionEnum>> DEFAULT_ROLE_PERMISSIONS = buildDefaultRolePermissions();

	/**
	 * Verifies at startup that the persisted role catalogue matches the code-side definitions.
	 *
	 * <p>Roles are declared in code ({@link StaffRoleCode} / {@code DEFAULT_ROLE_PERMISSIONS}) but read
	 * from the database at runtime. Those two representations must not drift, or a role will silently
	 * carry different privileges than the code implies — a security problem, not just a bug.
	 *
	 * <p>This method used to <em>write</em> the catalogue on every boot, hiding a privileged reference-data
	 * write inside ordinary service code. That job now belongs to {@code db/data/reference-data.sql},
	 * applied declaratively by {@code spring.sql.init} once Flyway has migrated the schema. Keeping it
	 * as visible SQL makes privilege changes reviewable in a diff and identical across environments.
	 * What remains here is a read-only assertion that fails the boot loudly rather than letting an
	 * environment with a half-applied catalogue serve traffic.
	 *
	 * <p>Not intended to be called directly — invoked by the container via {@link PostConstruct}.
	 *
	 * @throws IllegalStateException if any role or default permission is missing, extra, or mislabelled;
	 *                               the message points at the seed script that must be corrected
	 */
	@PostConstruct
	@Transactional(readOnly = true)
	public void validateRoleCatalog() {
		List<String> problems = new ArrayList<>();

		for (StaffRoleCode roleCode : StaffRoleCode.values()) {
			Optional<StaffRole> persisted = staffRoleRepository.findByCode(roleCode);
			if (persisted.isEmpty()) {
				problems.add("staff_roles is missing role '%s'".formatted(roleCode));
				continue;
			}

			StaffRole role = persisted.get();
			if (!roleCode.displayName().equals(role.getName())) {
				problems.add("role '%s' name is '%s' but code expects '%s'"
					.formatted(roleCode, role.getName(), roleCode.displayName()));
			}
			if (!roleCode.description().equals(role.getDescription())) {
				problems.add("role '%s' description differs from StaffRoleCode".formatted(roleCode));
			}

			Set<PermissionEnum> expected = DEFAULT_ROLE_PERMISSIONS.getOrDefault(roleCode, EnumSet.noneOf(PermissionEnum.class));
			EnumSet<PermissionEnum> actual = staffRolePermissionRepository.findByStaffRoleId(role.getId()).stream()
				.map(StaffRolePermission::getPermission)
				.collect(Collectors.toCollection(() -> EnumSet.noneOf(PermissionEnum.class)));

			EnumSet<PermissionEnum> missing = EnumSet.noneOf(PermissionEnum.class);
			missing.addAll(expected);
			missing.removeAll(actual);
			if (!missing.isEmpty()) {
				problems.add("role '%s' is missing default permissions %s".formatted(roleCode, missing));
			}

			EnumSet<PermissionEnum> unexpected = EnumSet.copyOf(actual);
			unexpected.removeAll(expected);
			if (!unexpected.isEmpty()) {
				problems.add("role '%s' has unexpected default permissions %s".formatted(roleCode, unexpected));
			}
		}

		if (!problems.isEmpty()) {
			throw new IllegalStateException(
				"Staff role catalogue in the database does not match the code definitions:\n  - "
					+ String.join("\n  - ", problems)
					+ "\nThe catalogue is seeded from db/data/reference-data.sql on every start. Update that "
					+ "script so it matches DEFAULT_ROLE_PERMISSIONS, then restart.");
		}
	}

	/**
	 * Returns every permission the platform understands, split into its {@code domain} and
	 * {@code action} parts with a human-readable label.
	 *
	 * <p>Exists so the admin UI can render permission pickers without hardcoding a copy of
	 * {@link PermissionEnum} in the frontend — the catalogue stays in one place and new permissions
	 * appear in the UI automatically.
	 *
	 * <p>Usage: {@code GET /api/v1/rbac/permissions}.
	 *
	 * @return the full permission catalogue; never empty.
	 */
	@Override
	@Transactional(readOnly = true)
	public AvailablePermissionsResponse getPermissionCatalog() {
		List<PermissionInfo> permissions = Arrays.stream(PermissionEnum.values())
			.map(permission -> {
				String[] parts = permission.getValue().split(":");
				String domain = parts[0];
				String action = parts[1];
				return new PermissionInfo(
					permission,
					domain,
					action,
					buildPermissionDescription(domain, action)
				);
			})
			.toList();
		return new AvailablePermissionsResponse(permissions);
	}

	/**
	 * Returns the platform-wide role templates with their default permissions, ignoring any
	 * community customisation.
	 *
	 * <p>This is the "factory settings" view. It is what a community is compared against to decide
	 * whether its own configuration has drifted, and what a new community starts from.
	 *
	 * <p>Usage: {@code GET /api/v1/rbac/staff-roles}.
	 *
	 * @return one template per role, ordered by name; {@code customized} is always {@code false}.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<StaffRoleTemplateResponse> getDefaultRoleTemplates() {
		return staffRoleRepository.findAllByOrderByNameAsc()
			.stream()
			.map(role -> new StaffRoleTemplateResponse(
				role.getCode(),
				role.getName(),
				role.getDescription(),
				getDefaultPermissions(role.getId()),
				false
			))
			.toList();
	}

	/**
	 * Returns the role templates as they actually apply inside one community, with that community's
	 * ALLOW/DENY overrides already folded in.
	 *
	 * <p>Each entry carries a {@code customized} flag so the UI can show an administrator which
	 * roles have been tailored locally versus which still track the platform defaults.
	 *
	 * <p>Usage: {@code GET /api/v1/communities/{communityId}/rbac/roles}.
	 *
	 * @param communityId the community to resolve against.
	 * @return one effective template per role, ordered by name.
	 * @throws NotFoundException if the community does not exist.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<StaffRoleTemplateResponse> getCommunityRoleTemplates(UUID communityId) {
		ensureCommunityExists(communityId);

		return staffRoleRepository.findAllByOrderByNameAsc()
			.stream()
			.map(role -> buildCommunityRoleTemplate(communityId, role))
			.toList();
	}

	/**
	 * Replaces a community's customisation of one role template with the supplied permission set.
	 *
	 * <p>The request states the <em>desired end state</em>, not a delta. This method diffs it against
	 * the platform defaults and persists only the differences as ALLOW/DENY override rows. Storing
	 * the delta rather than a full copy keeps the community tracking future changes to the platform
	 * defaults for every permission it has not explicitly opinionated on.
	 *
	 * <p>Existing overrides for the role are deleted first, so the operation is a full replace and is
	 * safe to retry.
	 *
	 * <p>Applies to every current and future holder of the role in this community — permissions are
	 * resolved per request, so changes take effect on the affected users' next call.
	 *
	 * <p>Usage: {@code PUT /api/v1/communities/{communityId}/rbac/roles/{roleCode}/permissions}.
	 *
	 * @param communityId the community whose customisation is being written.
	 * @param roleCode    the role template to customise.
	 * @param request     the complete set of permissions the role should end up with.
	 * @return the resulting template; {@code customized} is {@code true} when it now differs from the
	 *         platform defaults.
	 * @throws NotFoundException  if the community or role does not exist.
	 * @throws ForbiddenException if the change would lock the community out of its own RBAC
	 *                            configuration, or grants permissions the caller does not hold.
	 */
	@Override
	@Transactional
	public StaffRoleTemplateResponse updateCommunityRolePermissions(
		UUID communityId,
		StaffRoleCode roleCode,
		UpdateCommunityRolePermissionsRequest request
	) {
		ensureCommunityExists(communityId);
		StaffRole role = findRoleByCode(roleCode);

		EnumSet<PermissionEnum> defaultPermissions = getDefaultPermissions(role.getId());
		EnumSet<PermissionEnum> requestedPermissions = toEnumSet(request.permissions());

		assertNoAdministrativeLockout(roleCode, requestedPermissions);

		staffGrantGuard.assertCanGrant(
			communityId,
			requestedPermissions,
			() -> resolveEffectivePermissions(staffGrantGuard.requireCurrentUserId(), communityId)
		);

		communityRolePermissionOverrideRepository.deleteByCommunityIdAndStaffRoleId(communityId, role.getId());

		List<CommunityRolePermissionOverride> overrides = new ArrayList<>();
		for (PermissionEnum permission : PermissionEnum.values()) {
			boolean inDefaults = defaultPermissions.contains(permission);
			boolean inRequested = requestedPermissions.contains(permission);

			if (inDefaults == inRequested) {
				continue;
			}

			overrides.add(CommunityRolePermissionOverride.builder()
				.community(Community.builder().id(communityId).build())
				.staffRole(role)
				.permission(permission)
				.effect(inRequested ? PermissionOverrideEffect.ALLOW : PermissionOverrideEffect.DENY)
				.build());
		}

		if (!overrides.isEmpty()) {
			communityRolePermissionOverrideRepository.saveAll(overrides);
		}

		return new StaffRoleTemplateResponse(
			role.getCode(),
			role.getName(),
			role.getDescription(),
			requestedPermissions,
			!overrides.isEmpty()
		);
	}

	/**
	 * Assigns or replaces a member's staff role within a community.
	 *
	 * <p>A user holds at most one staff role per community (enforced by a unique constraint on
	 * {@code user_community_staff_roles}), so this upserts rather than appends — calling it again
	 * moves the user to the new role instead of accumulating roles. That single-role rule keeps
	 * effective-permission resolution cheap and unambiguous.
	 *
	 * <p>Guards applied, in order: the caller may not target themselves; the target must already be a
	 * member of the community; only {@code STAFF} and {@code COMMUNITY_ADMIN} members are eligible
	 * (a resident must be promoted first); and the caller may not assign a role carrying permissions
	 * they do not themselves hold.
	 *
	 * <p>Usage: {@code PUT /api/v1/communities/{communityId}/staff/{userId}/role}.
	 *
	 * @param communityId the community the assignment applies to.
	 * @param userId      the member receiving the role.
	 * @param request     the role code, and whether the assignment is active.
	 * @return the persisted assignment.
	 * @throws NotFoundException  if the community, user, role, or membership does not exist.
	 * @throws ForbiddenException if the member is ineligible, or the caller is escalating privileges
	 *                            or modifying their own account.
	 */
	@Override
	@Transactional
	public StaffRoleAssignmentResponse assignStaffRole(UUID communityId, UUID userId, AssignStaffRoleRequest request) {
		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new NotFoundException("Community not found"));
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException("User not found"));

		staffGrantGuard.assertNotSelf(userId, "change the staff role");

		UserCommunity membership = userCommunityRepository.findByUserIdAndCommunityId(userId, communityId)
			.orElseThrow(() -> new NotFoundException("User is not a member of the community"));

		if (membership.getRole() != SilinganRealmRole.STAFF && membership.getRole() != SilinganRealmRole.COMMUNITY_ADMIN) {
			throw new ForbiddenException("Staff role can only be assigned to STAFF or COMMUNITY_ADMIN members");
		}

		StaffRole staffRole = findRoleByCode(request.roleCode());

		staffGrantGuard.assertCanAssignRole(
			communityId,
			request.roleCode(),
			getEffectiveRolePermissions(communityId, staffRole),
			() -> resolveEffectivePermissions(staffGrantGuard.requireCurrentUserId(), communityId)
		);

		UserCommunityStaffRole assignment = userCommunityStaffRoleRepository
			.findByUserIdAndCommunityId(userId, communityId)
			.orElseGet(() -> UserCommunityStaffRole.builder()
				.user(user)
				.community(community)
				.build());

		assignment.setStaffRole(staffRole);
		assignment.setActive(request.active());
		assignment.setAssignedAt(LocalDateTime.now());
		assignment.setAssignedBy(resolveCurrentUserEntity().orElse(null));

		UserCommunityStaffRole savedAssignment = userCommunityStaffRoleRepository.save(assignment);
		return toAssignmentResponse(savedAssignment);
	}

	/**
	 * Reads a member's staff role assignment, including inactive ones.
	 *
	 * <p>Returns an empty assignment (null role, {@code active = false}) rather than throwing when
	 * the user has none, so the admin UI can render "no role assigned" without special-casing a 404.
	 * Inactive assignments are included deliberately: an administrator needs to see a suspended role
	 * in order to reactivate it.
	 *
	 * <p>Usage: {@code GET /api/v1/communities/{communityId}/staff/{userId}/role}.
	 *
	 * @param communityId the community to look in.
	 * @param userId      the member to inspect.
	 * @return the assignment, or an empty placeholder if none exists.
	 * @throws NotFoundException if the community or user does not exist.
	 */
	@Override
	@Transactional(readOnly = true)
	public StaffRoleAssignmentResponse getStaffRoleAssignment(UUID communityId, UUID userId) {
		ensureCommunityExists(communityId);
		ensureUserExists(userId);

		return userCommunityStaffRoleRepository.findByUserIdAndCommunityId(userId, communityId)
			.map(this::toAssignmentResponse)
			.orElse(new StaffRoleAssignmentResponse(userId, communityId, null, false, null, null));
	}

	/**
	 * Explains a member's access by returning each permission layer separately as well as the union.
	 *
	 * <p>Where {@link #resolveEffectivePermissions(UUID, UUID)} answers "what can they do?", this
	 * answers "why?" — role-derived and directly-granted permissions are kept apart so an
	 * administrator auditing access can tell which came from the role template and which were
	 * granted as a one-off exception. Use this for the admin UI; use the leaner
	 * {@code resolveEffectivePermissions} on hot paths.
	 *
	 * <p>Usage: {@code GET /api/v1/communities/{communityId}/staff/{userId}/effective-permissions}.
	 *
	 * @param communityId the community to resolve against.
	 * @param userId      the member to inspect.
	 * @return the assigned role plus the role, direct, and combined permission sets.
	 * @throws NotFoundException if the community or user does not exist.
	 */
	@Override
	@Transactional(readOnly = true)
	public EffectivePermissionsResponse getEffectivePermissions(UUID communityId, UUID userId) {
		ensureCommunityExists(communityId);
		ensureUserExists(userId);

		EnumSet<PermissionEnum> directPermissions = toEnumSet(
			userCommunityPermissionRepository.findPermissionsByUserIdAndCommunityId(userId, communityId)
		);

		Optional<UserCommunityStaffRole> roleAssignment = userCommunityStaffRoleRepository
			.findByUserIdAndCommunityIdAndActiveTrue(userId, communityId);

		EnumSet<PermissionEnum> rolePermissions = roleAssignment
			.map(assignment -> getEffectiveRolePermissions(communityId, assignment.getStaffRole()))
			.orElse(EnumSet.noneOf(PermissionEnum.class));

		EnumSet<PermissionEnum> effectivePermissions = rolePermissions.isEmpty()
			? EnumSet.noneOf(PermissionEnum.class)
			: EnumSet.copyOf(rolePermissions);
		effectivePermissions.addAll(directPermissions);

		StaffRoleCode roleCode = roleAssignment
			.map(assignment -> assignment.getStaffRole().getCode())
			.orElse(null);

		return new EffectivePermissionsResponse(
			userId,
			communityId,
			roleCode,
			rolePermissions,
			directPermissions,
			effectivePermissions
		);
	}

	/**
	 * Returns the authenticated caller's own role and permissions for a community.
	 *
	 * <p>Lets the frontend decide which menus, buttons, and routes to render without probing
	 * endpoints and handling 403s. This is a convenience for the UI only — it is not an authorization
	 * decision, which is always re-made server-side by {@code PermissionAspect}.
	 *
	 * <p>Platform and community administrators are reported as holding every permission, matching the
	 * bypass those realm roles receive during enforcement, so the UI does not hide controls they can
	 * in fact use.
	 *
	 * <p>Usage: {@code GET /api/v1/me/communities/{communityId}/capabilities}.
	 *
	 * @param communityId the community to report capabilities for.
	 * @return the caller's effective role and permissions.
	 * @throws NotFoundException  if the community does not exist.
	 * @throws ForbiddenException if there is no valid authenticated user context.
	 */
	@Override
	@Transactional(readOnly = true)
	public CurrentUserCapabilitiesResponse getCurrentUserCapabilities(UUID communityId) {
		ensureCommunityExists(communityId);

		UserContext userContext = UserContextHolder.get();
		if (userContext == null || userContext.userId() == null) {
			throw new ForbiddenException("No authenticated user context");
		}

		UUID userId;
		try {
			userId = UUID.fromString(userContext.userId());
		} catch (IllegalArgumentException ex) {
			throw new ForbiddenException("Invalid authenticated user context");
		}

		if (UserContextHolder.isPlatformAdmin() || UserContextHolder.isCommunityAdmin()) {
			return new CurrentUserCapabilitiesResponse(
				userId,
				communityId,
				StaffRoleCode.COMMUNITY_ADMIN,
				EnumSet.allOf(PermissionEnum.class)
			);
		}

		return new CurrentUserCapabilitiesResponse(
			userId,
			communityId,
			resolveAssignedRole(userId, communityId).orElse(null),
			resolveEffectivePermissions(userId, communityId)
		);
	}

	/**
	 * Resolves the flat set of permissions a user holds in a community.
	 *
	 * <p>This is the authoritative resolution used for enforcement: role defaults, then the
	 * community's ALLOW/DENY overrides, then additive direct grants. Only an <em>active</em> role
	 * assignment contributes, which is what makes deactivating an assignment an effective
	 * suspension without deleting the record. A user with no active role still keeps any direct
	 * grants.
	 *
	 * <p><b>Hot path.</b> {@code UserContextFilter} calls this once per authenticated request to
	 * build {@code CommunityAccess}, so it is deliberately lean — no existence checks and no layer
	 * breakdown. It also means a revoked permission takes effect on the very next request rather
	 * than waiting for a token refresh. See backlog item 11 in {@code docs/SECURITY_GUARDS.md}
	 * regarding caching.
	 *
	 * @param userId      the user to resolve.
	 * @param communityId the community to resolve within.
	 * @return the combined permission set; empty if the user has no role and no direct grants.
	 */
	@Override
	@Transactional(readOnly = true)
	public Set<PermissionEnum> resolveEffectivePermissions(UUID userId, UUID communityId) {
		EnumSet<PermissionEnum> directPermissions = toEnumSet(
			userCommunityPermissionRepository.findPermissionsByUserIdAndCommunityId(userId, communityId)
		);

		Optional<UserCommunityStaffRole> roleAssignment = userCommunityStaffRoleRepository
			.findByUserIdAndCommunityIdAndActiveTrue(userId, communityId);

		if (roleAssignment.isEmpty()) {
			return directPermissions;
		}

		EnumSet<PermissionEnum> effectivePermissions = getEffectiveRolePermissions(communityId, roleAssignment.get().getStaffRole());
		effectivePermissions.addAll(directPermissions);
		return effectivePermissions;
	}

	/**
	 * Returns the code of the user's <em>active</em> staff role in a community, if any.
	 *
	 * <p>A lightweight lookup for callers that need the role label but not the permission set — for
	 * example rendering a role badge in the staff directory. Unlike
	 * {@link #getStaffRoleAssignment(UUID, UUID)} this ignores inactive assignments, because a
	 * suspended role should not be presented as the user's current one.
	 *
	 * @param userId      the user to inspect.
	 * @param communityId the community to inspect within.
	 * @return the active role code, or {@link Optional#empty()} if none is assigned or it is inactive.
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<StaffRoleCode> resolveAssignedRole(UUID userId, UUID communityId) {
		return userCommunityStaffRoleRepository.findByUserIdAndCommunityIdAndActiveTrue(userId, communityId)
			.map(assignment -> assignment.getStaffRole().getCode());
	}

	/**
	 * Folds a community's ALLOW/DENY overrides onto a role's platform default permissions.
	 *
	 * <p>The single place where layers 1 and 2 of the permission model are combined; both the
	 * enforcement path and the admin template views route through it so they can never disagree.
	 * Overrides are applied last, so DENY reliably wins over a default.
	 *
	 * @return a mutable set, safe for the caller to add direct grants to.
	 */
	private EnumSet<PermissionEnum> getEffectiveRolePermissions(UUID communityId, StaffRole staffRole) {
		EnumSet<PermissionEnum> effective = getDefaultPermissions(staffRole.getId());
		List<CommunityRolePermissionOverride> overrides = communityRolePermissionOverrideRepository
			.findByCommunityIdAndStaffRoleId(communityId, staffRole.getId());

		for (CommunityRolePermissionOverride override : overrides) {
			if (override.getEffect() == PermissionOverrideEffect.ALLOW) {
				effective.add(override.getPermission());
			} else {
				effective.remove(override.getPermission());
			}
		}

		return effective;
	}

	/**
	 * Builds the community-facing view of a role template, resolving its effective permissions and
	 * flagging whether the community has customised it.
	 *
	 * <p>The {@code customized} flag is derived from the mere existence of override rows rather than
	 * by comparing permission sets, so a community that has explicitly re-affirmed a default is not
	 * misreported as untouched.
	 */
	private StaffRoleTemplateResponse buildCommunityRoleTemplate(UUID communityId, StaffRole role) {
		EnumSet<PermissionEnum> permissions = getEffectiveRolePermissions(communityId, role);
		boolean customized = !communityRolePermissionOverrideRepository
			.findByCommunityIdAndStaffRoleId(communityId, role.getId())
			.isEmpty();

		return new StaffRoleTemplateResponse(
			role.getCode(),
			role.getName(),
			role.getDescription(),
			permissions,
			customized
		);
	}

	/**
	 * Looks up the persisted {@link StaffRole} row for a role code.
	 *
	 * <p>Role codes arrive as enums but permissions hang off the database row, so this is the bridge
	 * between the two. A missing row means {@code reference-data.sql} has not seeded the newly added
	 * code yet, hence the explicit failure rather than a silent skip.
	 *
	 * @throws NotFoundException if no row exists for the code.
	 */
	private StaffRole findRoleByCode(StaffRoleCode roleCode) {
		return staffRoleRepository.findByCode(roleCode)
			.orElseThrow(() -> new NotFoundException("Staff role not found: " + roleCode));
	}

	/**
	 * Reads a role's platform default permissions — layer 1 of the model, before any community
	 * override is applied.
	 */
	private EnumSet<PermissionEnum> getDefaultPermissions(UUID staffRoleId) {
		return toEnumSet(staffRolePermissionRepository.findPermissionsByStaffRoleId(staffRoleId));
	}

	/**
	 * Copies permissions into a mutable {@link EnumSet}, treating null and empty alike.
	 *
	 * <p>{@link EnumSet#copyOf(java.util.Collection)} throws on an empty collection, and callers
	 * routinely receive empty results from the repositories, so this wrapper keeps that guard in one
	 * place. Returning a fresh mutable set also lets callers merge layers without mutating a
	 * repository result.
	 */
	private EnumSet<PermissionEnum> toEnumSet(Set<PermissionEnum> permissions) {
		if (permissions == null || permissions.isEmpty()) {
			return EnumSet.noneOf(PermissionEnum.class);
		}
		return EnumSet.copyOf(permissions);
	}

	/**
	 * {@link List} overload of {@link #toEnumSet(Set)}, for repository queries that return a list.
	 * Duplicates collapse naturally in the resulting set.
	 */
	private EnumSet<PermissionEnum> toEnumSet(List<PermissionEnum> permissions) {
		if (permissions == null || permissions.isEmpty()) {
			return EnumSet.noneOf(PermissionEnum.class);
		}
		return EnumSet.copyOf(permissions);
	}

	/**
	 * Maps a persisted assignment to its API response.
	 *
	 * <p>The user and community identifiers are read from the shadow {@code @Column} fields where
	 * available, falling back to the associations. This avoids triggering a lazy load — and a
	 * {@code LazyInitializationException} outside a session — just to obtain an ID that is already
	 * present on the row.
	 */
	private StaffRoleAssignmentResponse toAssignmentResponse(UserCommunityStaffRole assignment) {
		UUID userId = assignment.getUserId() != null
			? assignment.getUserId()
			: (assignment.getUser() != null ? assignment.getUser().getId() : null);
		UUID communityId = assignment.getCommunityId() != null
			? assignment.getCommunityId()
			: (assignment.getCommunity() != null ? assignment.getCommunity().getId() : null);

		return new StaffRoleAssignmentResponse(
			userId,
			communityId,
			assignment.getStaffRole().getCode(),
			assignment.getActive(),
			assignment.getAssignedAt(),
			assignment.getAssignedBy() != null ? assignment.getAssignedBy().getId() : null
		);
	}

	/**
	 * Derives a display label such as "Manage Announcement" from a permission's domain and action.
	 *
	 * <p>Generated rather than stored so that adding a {@link PermissionEnum} constant needs no
	 * accompanying label table or translation row. Anything that is not {@code view} is treated as
	 * "Manage", matching the two-action model in {@code Action}.
	 */
	private String buildPermissionDescription(String domain, String action) {
		String actionDescription = "view".equals(action) ? "View" : "Manage";
		return actionDescription + " " + capitalize(domain);
	}

	/**
	 * Upper-cases the first character, null- and empty-safe. Used only to build permission display
	 * labels.
	 */
	private String capitalize(String value) {
		if (value == null || value.isEmpty()) {
			return value;
		}
		return value.substring(0, 1).toUpperCase() + value.substring(1);
	}

	/**
	 * Fails fast when the community does not exist.
	 *
	 * <p>Used by read paths that would otherwise return an empty result for a bogus identifier,
	 * which would be indistinguishable from a real community that has no configuration yet.
	 *
	 * @throws NotFoundException if no such community exists.
	 */
	private void ensureCommunityExists(UUID communityId) {
		if (!communityRepository.existsById(communityId)) {
			throw new NotFoundException("Community not found");
		}
	}

	/**
	 * Fails fast when the user does not exist, for the same reason as
	 * {@link #ensureCommunityExists(UUID)}.
	 *
	 * @throws NotFoundException if no such user exists.
	 */
	private void ensureUserExists(UUID userId) {
		if (!userRepository.existsById(userId)) {
			throw new NotFoundException("User not found");
		}
	}

	/**
	 * Prevents a community from removing its own ability to administer RBAC.
	 *
	 * <p>The COMMUNITY_ADMIN template is the only role that can restore permissions for a community.
	 * Allowing it to be stripped of role/staff management would permanently lock the community out
	 * of its own RBAC configuration, with recovery only via a platform administrator or direct
	 * database edit.
	 *
	 * @param roleCode             the template being edited; ignored unless it is COMMUNITY_ADMIN.
	 * @param requestedPermissions the desired end state.
	 * @throws ForbiddenException if the four role/staff administration permissions are not all
	 *                            retained.
	 */
	private void assertNoAdministrativeLockout(StaffRoleCode roleCode, Set<PermissionEnum> requestedPermissions) {
		if (roleCode != StaffRoleCode.COMMUNITY_ADMIN) {
			return;
		}

		EnumSet<PermissionEnum> required = EnumSet.of(
			PermissionEnum.ROLE_MANAGE,
			PermissionEnum.ROLE_VIEW,
			PermissionEnum.STAFF_MANAGE,
			PermissionEnum.STAFF_VIEW
		);

		if (!requestedPermissions.containsAll(required)) {
			throw new ForbiddenException(
				"The COMMUNITY_ADMIN role must retain role and staff management permissions"
			);
		}
	}

	/**
	 * Loads the authenticated caller's {@link User} entity for audit stamping.
	 *
	 * <p>Returns {@link Optional#empty()} rather than throwing on a missing or malformed context,
	 * because it is only used to populate {@code assignedBy}. An unattributable assignment is
	 * preferable to failing an otherwise valid, already-authorized operation — the authorization
	 * decision itself is made earlier by {@link StaffGrantGuard}.
	 */
	private Optional<User> resolveCurrentUserEntity() {
		UserContext context = UserContextHolder.get();
		if (context == null || context.userId() == null) {
			return Optional.empty();
		}

		try {
			UUID currentUserId = UUID.fromString(context.userId());
			return userRepository.findById(currentUserId);
		} catch (IllegalArgumentException ex) {
			return Optional.empty();
		}
	}

	/**
	 * Declares the platform default permission set for each staff role.
	 *
	 * <p>This map is the expected state; the rows are actually installed by
	 * {@code db/data/reference-data.sql} on startup and {@link #validateRoleCatalog()} asserts the two
	 * agree. Changing a platform default therefore means editing this map <em>and</em> that script in
	 * the same commit; per-community deviations belong in override rows via
	 * {@link #updateCommunityRolePermissions}.
	 *
	 * <p>COMMUNITY_ADMIN intentionally receives every permission, so a new permission is
	 * administrable from the day it is introduced.
	 *
	 * @return an immutable map, evaluated once into {@link #DEFAULT_ROLE_PERMISSIONS}.
	 */
	private static Map<StaffRoleCode, Set<PermissionEnum>> buildDefaultRolePermissions() {
		EnumMap<StaffRoleCode, Set<PermissionEnum>> map = new EnumMap<>(StaffRoleCode.class);
		map.put(StaffRoleCode.COMMUNITY_ADMIN, EnumSet.allOf(PermissionEnum.class));
		map.put(StaffRoleCode.PMO_STAFF, EnumSet.of(
			PermissionEnum.DASHBOARD_VIEW,
			PermissionEnum.ANNOUNCEMENT_VIEW,
			PermissionEnum.ANNOUNCEMENT_MANAGE,
			PermissionEnum.REPORT_VIEW,
			PermissionEnum.REPORT_MANAGE,
			PermissionEnum.DIRECTORY_VIEW,
			PermissionEnum.RESIDENT_VIEW,
			PermissionEnum.STAFF_VIEW,
			PermissionEnum.DOCUMENT_VIEW
		));
		map.put(StaffRoleCode.SECURITY_ADMIN, EnumSet.of(
			PermissionEnum.DASHBOARD_VIEW,
			PermissionEnum.REPORT_VIEW,
			PermissionEnum.DIRECTORY_VIEW,
			PermissionEnum.RESIDENT_VIEW,
			PermissionEnum.STAFF_VIEW
		));
		map.put(StaffRoleCode.MAINTENANCE_ADMIN, EnumSet.of(
			PermissionEnum.DASHBOARD_VIEW,
			PermissionEnum.REPORT_VIEW,
			PermissionEnum.REPORT_MANAGE,
			PermissionEnum.DOCUMENT_VIEW,
			PermissionEnum.DOCUMENT_MANAGE,
			PermissionEnum.ANNOUNCEMENT_VIEW,
			PermissionEnum.DIRECTORY_VIEW
		));
		map.put(StaffRoleCode.READ_ONLY_STAFF, EnumSet.of(
			PermissionEnum.COMMUNITY_VIEW,
			PermissionEnum.RESIDENT_VIEW,
			PermissionEnum.STAFF_VIEW,
			PermissionEnum.ROLE_VIEW,
			PermissionEnum.ANNOUNCEMENT_VIEW,
			PermissionEnum.REPORT_VIEW,
			PermissionEnum.DIRECTORY_VIEW,
			PermissionEnum.DASHBOARD_VIEW,
			PermissionEnum.DOCUMENT_VIEW
		));
		return Collections.unmodifiableMap(map);
	}
}
