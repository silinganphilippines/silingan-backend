package com.ria.olita.tech.silingan.service.impl;

import java.util.List;
import java.util.StringJoiner;

import org.springframework.stereotype.Service;

import com.ria.olita.tech.silingan.dto.req.AddressRequest;
import com.ria.olita.tech.silingan.dto.res.CommunityCodeResponse;
import com.ria.olita.tech.silingan.entity.CommunityType;
import com.ria.olita.tech.silingan.entity.Region;
import com.ria.olita.tech.silingan.service.CommunityCodeService;
import com.ria.olita.tech.silingan.util.CommunityCodeNormalizer;
import com.ria.olita.tech.silingan.util.InviteCodeSuffixGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityCodeServiceImpl implements CommunityCodeService {

	private static final int DISPLAY_MAX = 16;


	@Override
	public CommunityCodeResponse generateCodes(CommunityType type, AddressRequest address) {

		String displayCode = generateDisplayCode(type, address) + "-" + InviteCodeSuffixGenerator.generate(5);
		String systemCode = generateSystemCode(type, address);

		return CommunityCodeResponse.builder()
			.displayCode(displayCode)
			.systemCode(systemCode)
			.build();
	}


	private String generateDisplayCode(CommunityType type, AddressRequest address) {

		String sourceName = toNameCode(type, address);
		List<String> words = CommunityCodeNormalizer.splitWords(sourceName);

		String combined = String.join("", words);

		// Case A & B: Already <= 16 chars
		if (combined.length() <= DISPLAY_MAX) {
			return combined;
		}

		// Case C: Beautify
		return beautify(words);
	}


	private String generateSystemCode(CommunityType type, AddressRequest address) {

		Region region = Region.fromRegionalNumber(address.region());

		String cityCode = toCityCode(address.city());
		String nameCode = generateDisplayCode(type, address);

		return "%s-%s-%s".formatted(
			region.name(),
			cityCode,
			nameCode
		);
	}


	private String beautify(List<String> words) {
		StringJoiner joiner = new StringJoiner("");

		int wordCount = words.size();

		for (String word : words) {
			int take;
			if (wordCount == 2) take = Math.min(4, word.length());
			else if (wordCount == 3) take = Math.min(3, word.length());
			else take = 1;

			joiner.add(word.substring(0, take));

			if (joiner.length() >= DISPLAY_MAX) break;
		}

		String result = joiner.toString();
		return result.length() > DISPLAY_MAX
			? result.substring(0, DISPLAY_MAX)
			: result;
	}

	private String toCityCode(String city) {
		String normalized = CommunityCodeNormalizer.normalize(city);
		return normalized.substring(0, Math.min(3, normalized.length()));
	}

	private String toNameCode(CommunityType type, AddressRequest address) {
		String name;
		if (type.equals(CommunityType.CITY)) {
			name = address.city();
		} else if (type.equals(CommunityType.CONDO)) {
			name = address.buildingName();
		} else {
			name = address.barangay();
		}

		return CommunityCodeNormalizer.normalize(name);
	}
}
