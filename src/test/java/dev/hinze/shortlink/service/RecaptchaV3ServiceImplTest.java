package dev.hinze.shortlink.service;

import dev.hinze.shortlink.exception.RecaptchaV3Exception;
import dev.hinze.shortlink.model.RecaptchaV3Response;
import dev.hinze.shortlink.service.impl.RecaptchaV3ServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = RecaptchaV3ServiceImpl.class,
        properties = {
        "google.recaptcha.v3.secret=xxxx",
        "google.recaptcha.v3.verify=true",
        "google.recaptcha.v3.min=0.7",
        "google.recaptcha.v3.action=action"
})
public class RecaptchaV3ServiceImplTest {

    @MockBean
    private RestTemplate restTemplate;
    @Autowired
    private RecaptchaV3Service recaptchaV3ServiceImpl;

    private RecaptchaV3Response recaptchaV3Response;

    @BeforeEach
    public void beforeEach() {
        recaptchaV3Response =
                new RecaptchaV3Response(true, BigDecimal.valueOf(0.7), "action", OffsetDateTime.now(), "localhost", new String[]{});
        when(restTemplate.postForObject(contains("response"), any(), eq(RecaptchaV3Response.class)))
                .thenReturn(recaptchaV3Response);
    }

    @Test
    public void scoreIsMinimum() {
        recaptchaV3ServiceImpl.verify("response");
    }

    @Test
    public void scoreAboveMinimum() {
        recaptchaV3Response.setScore(BigDecimal.valueOf(0.71));
        recaptchaV3ServiceImpl.verify("response");
    }

    @Test
    public void scoreBelowMinimum() {
        recaptchaV3Response.setScore(BigDecimal.valueOf(0.69));
        Assertions.assertThrows(RecaptchaV3Exception.class, () ->
                recaptchaV3ServiceImpl.verify("response")
        );
    }

    @Test
    public void actionNotWhatExpected() {
        recaptchaV3Response.setAction("something else");
        Assertions.assertThrows(RecaptchaV3Exception.class, () ->
                recaptchaV3ServiceImpl.verify("response")
        );    }

    @Test
    public void responseIsNull() {
        when(restTemplate.postForObject(contains("null"), any(), eq(RecaptchaV3Response.class)))
                .thenReturn(null);
        Assertions.assertThrows(RecaptchaV3Exception.class, () ->
                recaptchaV3ServiceImpl.verify("null")
        );
    }

}


