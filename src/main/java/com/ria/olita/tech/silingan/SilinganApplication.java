package com.ria.olita.tech.silingan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.ria.olita.tech.silingan.config.OtpProperties;
import com.ria.olita.tech.silingan.config.SmsProperties;
import com.ria.olita.tech.silingan.config.JwtProperties;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties({OtpProperties.class, SmsProperties.class, JwtProperties.class})
public class SilinganApplication {


	public static void main(String[] args) {
		SpringApplication.run(SilinganApplication.class, args);
	}

}
