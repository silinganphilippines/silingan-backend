package com.ria.olita.tech.silingan.entity.rbac;

import java.util.UUID;

import lombok.Builder;

@Builder
public record CommunityRole(UUID id, String roleName) {
}
