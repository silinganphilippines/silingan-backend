package com.ria.olita.tech.silingan.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.ria.olita.tech.silingan.entity.CommunityRole;
import com.ria.olita.tech.silingan.security.context.UserContext;
import com.ria.olita.tech.silingan.security.context.UserContextHolder;


class CommunityStatementInspectorTest {

	private final CommunityStatementInspector communityStatementInspector = new CommunityStatementInspector();

	@AfterEach
	void cleanup() {
		UserContextHolder.clear();
	}

	@Test
	void shouldAllowSelectWithCommunityFilter() {

		String sql = "SELECT * FROM announcements WHERE community_id = '123'";
		String result = communityStatementInspector.inspect(sql);

		assertThat(result).isEqualTo(sql);
	}


	@Test
	void shouldBlockSelectWithoutCommunityFilter() {
		String sql = "SELECT * FROM announcements";

		assertThrows(
			IllegalStateException.class,
			() -> communityStatementInspector.inspect(sql)
		);
	}

	@ParameterizedTest
	@CsvSource({
		"PLATFORM_ADMIN, true",
		"RESIDENT, false",
		"COMMUNITY_ADMIN, false"
	})
	void shouldHandleQueriesBasedOnRole(String role, boolean shouldPass) {

		UserContext context = UserContext.builder()
			.roles(List.of(CommunityRole.valueOf(role)))
			.build();

		UserContextHolder.set(context);

		String sql = "SELECT * FROM announcements"; // no community_id → unsafe

		if (shouldPass) {
			String result = communityStatementInspector.inspect(sql);
			assertThat(result).isEqualTo(sql);
		} else {
			assertThatThrownBy(() -> communityStatementInspector.inspect(sql))
				.isInstanceOf(IllegalStateException.class);
		}
	}
}
