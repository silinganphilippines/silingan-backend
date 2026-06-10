package com.ria.olita.tech.silingan.entity;

public enum Region {
	LUZ, VIS, MIN;

	public static Region fromRegionalNumber(int region) {
		if (region >= 1 && region <= 5) return LUZ;
		if (region >= 6 && region <= 8) return VIS;
		if (region >= 9 && region <= 13) return MIN;
		throw new IllegalArgumentException("Unsupported region: " + region);
	}
}
