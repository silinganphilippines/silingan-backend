package com.ria.olita.tech.silingan.service.sms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.ria.olita.tech.silingan.config.SmsProperties;

class SmsServiceTest {

    @Test
    void shouldBypassOtpSendWhenBypassEnabled() {
        SmsProperties properties = new SmsProperties();
        properties.setProvider("semaphore");
        properties.setBypassSending(true);

        SmsProvider provider = Mockito.mock(SmsProvider.class);
        when(provider.getProviderName()).thenReturn("semaphore");

        SmsService smsService = new SmsService(properties, List.of(provider));
        smsService.init();

        boolean sent = smsService.sendOtp("+639171234567", "123456");

        assertThat(sent).isTrue();
        verify(provider, never()).sendOtp(anyString(), anyString());
    }

    @Test
    void shouldDelegateOtpSendWhenBypassDisabled() {
        SmsProperties properties = new SmsProperties();
        properties.setProvider("semaphore");
        properties.setBypassSending(false);

        SmsProvider provider = Mockito.mock(SmsProvider.class);
        when(provider.getProviderName()).thenReturn("semaphore");
        when(provider.sendOtp("+639171234567", "123456")).thenReturn(true);

        SmsService smsService = new SmsService(properties, List.of(provider));
        smsService.init();

        boolean sent = smsService.sendOtp("+639171234567", "123456");

        assertThat(sent).isTrue();
        verify(provider).sendOtp("+639171234567", "123456");
    }
}

