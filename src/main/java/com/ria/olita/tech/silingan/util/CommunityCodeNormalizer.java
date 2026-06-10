package com.ria.olita.tech.silingan.util;

import java.util.List;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommunityCodeNormalizer {

	private static final Pattern CLEAN_PATTERN = Pattern.compile("[^A-Z0-9 ]");

	public static String normalize(String input) {
		if (input == null) return "";
		return CLEAN_PATTERN
			.matcher(input.toUpperCase()
				.trim())
			.replaceAll("");
	}

	public static List<String> splitWords(String normalized) {
		return List.of(normalized.split("\\s+"));
	}

}
