package com.ria.olita.tech.silingan.service.otp;

public interface RegistrationOtpProofService {

	void markVerifiedForRegistration(String mobileNumber);

	boolean consumeRegistrationProof(String mobileNumber);
}

