package com.ria.olita.tech.silingan.util;

import java.security.SecureRandom;

public class InviteCodeSuffixGenerator {

	// Human-safe Base32 alphabet
	private static final char[] ALPHABET =
		"ABCDEFGHJKMNPQRSTUVWXYZ23456789".toCharArray();

	private static final SecureRandom RANDOM = new SecureRandom();

	private InviteCodeSuffixGenerator() {
		// utility class
	}

	public static String generate(int length) {
		if (length <= 0) {
			throw new IllegalArgumentException("Length must be positive");
		}

		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			int index = RANDOM.nextInt(ALPHABET.length);
			sb.append(ALPHABET[index]);
		}

		return sb.toString();
	}
}

