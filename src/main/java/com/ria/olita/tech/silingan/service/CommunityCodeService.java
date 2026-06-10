package com.ria.olita.tech.silingan.service;

import com.ria.olita.tech.silingan.dto.req.AddressRequest;
import com.ria.olita.tech.silingan.dto.res.CommunityCodeResponse;
import com.ria.olita.tech.silingan.entity.CommunityType;

public interface CommunityCodeService {

	CommunityCodeResponse generateCodes(CommunityType type, AddressRequest address);

}