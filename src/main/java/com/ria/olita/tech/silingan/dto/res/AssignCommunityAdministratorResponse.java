package com.ria.olita.tech.silingan.dto.res;

public record AssignCommunityAdministratorResponse(
	Administrator administrator
)
{
	public static AssignCommunityAdministratorResponse invitationSent(String email) {
		return new AssignCommunityAdministratorResponse(new Administrator(email, "INVITATION_SENT"));
	}

	public record Administrator(
		String email,
		String status
	) {
	}
}

