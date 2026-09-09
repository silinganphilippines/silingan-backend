-- =====================================================================
-- Reference data - staff role catalog and default permissions
-- ---------------------------------------------------------------------
-- Seeds the five system staff roles and their platform-wide default
-- permissions (permission layer 1).
--
-- WHY THIS IS NOT A FLYWAY MIGRATION
-- Flyway owns the *schema* only. Reference data is not a one-way schema
-- change: it is desired state that should be re-asserted on every boot, so
-- an operator who edits a permission row by hand has it corrected on the
-- next restart rather than silently drifting forever. A versioned migration
-- runs exactly once and cannot do that.
--
-- WHY THIS IS NOT A @PostConstruct HOOK EITHER
-- It used to be one, inside CommunityRbacServiceImpl. That hid a privileged
-- write behind ordinary service code and made the catalog invisible to
-- anyone reading SQL. It now runs as declarative SQL through
-- spring.sql.init, after Flyway has created the schema.
--
-- REQUIREMENTS FOR THIS FILE
--  * Idempotent. It is executed on every application start.
--  * Convergent. It must remove rows it no longer grants, not just add.
--  * In sync with DEFAULT_ROLE_PERMISSIONS in CommunityRbacServiceImpl -
--    validateRoleCatalog() fails the boot if the two disagree.
--
-- Role IDs are fixed constants so the same row identity exists in every
-- environment, making later data fixes reproducible.
-- =====================================================================

INSERT INTO staff_roles (id, code, name, description, is_system, created_at, updated_at) VALUES
    ('00000000-0000-4000-a000-000000000001', 'COMMUNITY_ADMIN',   'Community Admin',   'Full access to all community-scoped features',        TRUE, NOW(), NOW()),
    ('00000000-0000-4000-a000-000000000002', 'PMO_STAFF',         'PMO Staff',         'Operations-focused access for community management',  TRUE, NOW(), NOW()),
    ('00000000-0000-4000-a000-000000000003', 'SECURITY_ADMIN',    'Security Admin',    'Security and monitoring access',                      TRUE, NOW(), NOW()),
    ('00000000-0000-4000-a000-000000000004', 'MAINTENANCE_ADMIN', 'Maintenance Admin', 'Maintenance and facilities management access',        TRUE, NOW(), NOW()),
    ('00000000-0000-4000-a000-000000000005', 'READ_ONLY_STAFF',   'Read-Only Staff',   'View-only access to assigned community modules',      TRUE, NOW(), NOW())
ON CONFLICT (code) DO UPDATE
    SET name        = EXCLUDED.name,
        description = EXCLUDED.description,
        is_system   = TRUE,
        updated_at  = NOW();

-- COMMUNITY_ADMIN: every permission that exists.
INSERT INTO staff_role_permissions (id, staff_role_id, permission)
SELECT gen_random_uuid(), r.id, p.permission
FROM staff_roles r
CROSS JOIN (VALUES
    ('COMMUNITY_VIEW'), ('COMMUNITY_MANAGE'),
    ('RESIDENT_VIEW'), ('RESIDENT_MANAGE'),
    ('STAFF_VIEW'), ('STAFF_MANAGE'),
    ('ROLE_VIEW'), ('ROLE_MANAGE'),
    ('ANNOUNCEMENT_VIEW'), ('ANNOUNCEMENT_MANAGE'),
    ('REPORT_VIEW'), ('REPORT_MANAGE'),
    ('DIRECTORY_VIEW'), ('DIRECTORY_MANAGE'),
    ('DASHBOARD_VIEW'), ('NOTIFICATION_MANAGE'),
    ('DOCUMENT_VIEW'), ('DOCUMENT_MANAGE')
) AS p(permission)
WHERE r.code = 'COMMUNITY_ADMIN'
ON CONFLICT (staff_role_id, permission) DO NOTHING;

-- PMO_STAFF: operations - can publish announcements and work reports.
INSERT INTO staff_role_permissions (id, staff_role_id, permission)
SELECT gen_random_uuid(), r.id, p.permission
FROM staff_roles r
CROSS JOIN (VALUES
    ('DASHBOARD_VIEW'),
    ('ANNOUNCEMENT_VIEW'), ('ANNOUNCEMENT_MANAGE'),
    ('REPORT_VIEW'), ('REPORT_MANAGE'),
    ('DIRECTORY_VIEW'),
    ('RESIDENT_VIEW'),
    ('STAFF_VIEW'),
    ('DOCUMENT_VIEW')
) AS p(permission)
WHERE r.code = 'PMO_STAFF'
ON CONFLICT (staff_role_id, permission) DO NOTHING;

-- SECURITY_ADMIN: monitoring only - deliberately holds no MANAGE permission.
INSERT INTO staff_role_permissions (id, staff_role_id, permission)
SELECT gen_random_uuid(), r.id, p.permission
FROM staff_roles r
CROSS JOIN (VALUES
    ('DASHBOARD_VIEW'),
    ('REPORT_VIEW'),
    ('DIRECTORY_VIEW'),
    ('RESIDENT_VIEW'),
    ('STAFF_VIEW')
) AS p(permission)
WHERE r.code = 'SECURITY_ADMIN'
ON CONFLICT (staff_role_id, permission) DO NOTHING;

-- MAINTENANCE_ADMIN: facilities - owns reports and documents.
INSERT INTO staff_role_permissions (id, staff_role_id, permission)
SELECT gen_random_uuid(), r.id, p.permission
FROM staff_roles r
CROSS JOIN (VALUES
    ('DASHBOARD_VIEW'),
    ('REPORT_VIEW'), ('REPORT_MANAGE'),
    ('DOCUMENT_VIEW'), ('DOCUMENT_MANAGE'),
    ('ANNOUNCEMENT_VIEW'),
    ('DIRECTORY_VIEW')
) AS p(permission)
WHERE r.code = 'MAINTENANCE_ADMIN'
ON CONFLICT (staff_role_id, permission) DO NOTHING;

-- READ_ONLY_STAFF: every VIEW permission, no MANAGE permission.
INSERT INTO staff_role_permissions (id, staff_role_id, permission)
SELECT gen_random_uuid(), r.id, p.permission
FROM staff_roles r
CROSS JOIN (VALUES
    ('COMMUNITY_VIEW'),
    ('RESIDENT_VIEW'),
    ('STAFF_VIEW'),
    ('ROLE_VIEW'),
    ('ANNOUNCEMENT_VIEW'),
    ('REPORT_VIEW'),
    ('DIRECTORY_VIEW'),
    ('DASHBOARD_VIEW'),
    ('DOCUMENT_VIEW')
) AS p(permission)
WHERE r.code = 'READ_ONLY_STAFF'
ON CONFLICT (staff_role_id, permission) DO NOTHING;

-- Drop any default permission that is no longer declared for these roles.
-- Needed because environments created before Flyway had their catalog written
-- at runtime and may carry rows this migration does not grant.
DELETE FROM staff_role_permissions srp
USING staff_roles r
WHERE srp.staff_role_id = r.id
  AND r.code IN ('COMMUNITY_ADMIN', 'PMO_STAFF', 'SECURITY_ADMIN', 'MAINTENANCE_ADMIN', 'READ_ONLY_STAFF')
  AND NOT EXISTS (
      SELECT 1
      FROM (VALUES
          ('COMMUNITY_ADMIN',   'COMMUNITY_VIEW'), ('COMMUNITY_ADMIN',   'COMMUNITY_MANAGE'),
          ('COMMUNITY_ADMIN',   'RESIDENT_VIEW'),  ('COMMUNITY_ADMIN',   'RESIDENT_MANAGE'),
          ('COMMUNITY_ADMIN',   'STAFF_VIEW'),     ('COMMUNITY_ADMIN',   'STAFF_MANAGE'),
          ('COMMUNITY_ADMIN',   'ROLE_VIEW'),      ('COMMUNITY_ADMIN',   'ROLE_MANAGE'),
          ('COMMUNITY_ADMIN',   'ANNOUNCEMENT_VIEW'), ('COMMUNITY_ADMIN', 'ANNOUNCEMENT_MANAGE'),
          ('COMMUNITY_ADMIN',   'REPORT_VIEW'),    ('COMMUNITY_ADMIN',   'REPORT_MANAGE'),
          ('COMMUNITY_ADMIN',   'DIRECTORY_VIEW'), ('COMMUNITY_ADMIN',   'DIRECTORY_MANAGE'),
          ('COMMUNITY_ADMIN',   'DASHBOARD_VIEW'), ('COMMUNITY_ADMIN',   'NOTIFICATION_MANAGE'),
          ('COMMUNITY_ADMIN',   'DOCUMENT_VIEW'),  ('COMMUNITY_ADMIN',   'DOCUMENT_MANAGE'),

          ('PMO_STAFF', 'DASHBOARD_VIEW'), ('PMO_STAFF', 'ANNOUNCEMENT_VIEW'), ('PMO_STAFF', 'ANNOUNCEMENT_MANAGE'),
          ('PMO_STAFF', 'REPORT_VIEW'), ('PMO_STAFF', 'REPORT_MANAGE'), ('PMO_STAFF', 'DIRECTORY_VIEW'),
          ('PMO_STAFF', 'RESIDENT_VIEW'), ('PMO_STAFF', 'STAFF_VIEW'), ('PMO_STAFF', 'DOCUMENT_VIEW'),

          ('SECURITY_ADMIN', 'DASHBOARD_VIEW'), ('SECURITY_ADMIN', 'REPORT_VIEW'),
          ('SECURITY_ADMIN', 'DIRECTORY_VIEW'), ('SECURITY_ADMIN', 'RESIDENT_VIEW'),
          ('SECURITY_ADMIN', 'STAFF_VIEW'),

          ('MAINTENANCE_ADMIN', 'DASHBOARD_VIEW'), ('MAINTENANCE_ADMIN', 'REPORT_VIEW'),
          ('MAINTENANCE_ADMIN', 'REPORT_MANAGE'), ('MAINTENANCE_ADMIN', 'DOCUMENT_VIEW'),
          ('MAINTENANCE_ADMIN', 'DOCUMENT_MANAGE'), ('MAINTENANCE_ADMIN', 'ANNOUNCEMENT_VIEW'),
          ('MAINTENANCE_ADMIN', 'DIRECTORY_VIEW'),

          ('READ_ONLY_STAFF', 'COMMUNITY_VIEW'), ('READ_ONLY_STAFF', 'RESIDENT_VIEW'),
          ('READ_ONLY_STAFF', 'STAFF_VIEW'), ('READ_ONLY_STAFF', 'ROLE_VIEW'),
          ('READ_ONLY_STAFF', 'ANNOUNCEMENT_VIEW'), ('READ_ONLY_STAFF', 'REPORT_VIEW'),
          ('READ_ONLY_STAFF', 'DIRECTORY_VIEW'), ('READ_ONLY_STAFF', 'DASHBOARD_VIEW'),
          ('READ_ONLY_STAFF', 'DOCUMENT_VIEW')
      ) AS expected(role_code, permission)
      WHERE expected.role_code = r.code
        AND expected.permission = srp.permission
  );
