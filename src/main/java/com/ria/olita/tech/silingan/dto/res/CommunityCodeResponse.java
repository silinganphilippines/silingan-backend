package com.ria.olita.tech.silingan.dto.res;

import lombok.Builder;

/**
 * Response object holding both display code and system code for a community.
 *
 * <ul>
 *   <li><b>displayCode</b> – Human-friendly, user-facing code used as an invite
 *       identifier (e.g., {@code ANAO}, {@code SANLORVIL}).</li>
 *   <li><b>systemCode</b> – Internal, immutable, globally-unique identifier
 *       used by the system (e.g., {@code VIS-CEB-ANAO-0001}).</li>
 * </ul>
 */
@Builder
public record CommunityCodeResponse(
	String displayCode,
	String systemCode
) {
}