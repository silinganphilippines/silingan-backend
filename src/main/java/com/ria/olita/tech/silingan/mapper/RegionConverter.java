package com.ria.olita.tech.silingan.mapper;

import org.springframework.stereotype.Component;

import com.ria.olita.tech.silingan.entity.Region;

/**
 * Converter for mapping region from int to Region enum name (String)
 */
@Component
public class RegionConverter {

	/**
	 * Converts an integer region code to Region enum name string
	 */
	public String intToString(int region) {
		Region regionEnum = Region.fromRegionalNumber(region);
		return regionEnum.name();
	}

	/**
	 * Converts a Region enum name string back to integer
	 */
	public int stringToInt(String region) {
		if (region == null || region.isEmpty()) {
			throw new IllegalArgumentException("Region cannot be null or empty");
		}
		try {
			Region regionEnum = Region.valueOf(region);
			// Return the first number for that region
			return switch (regionEnum) {
				case LUZ -> 1;
				case VIS -> 6;
				case MIN -> 9;
			};
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid region value: " + region, e);
		}
	}
}

