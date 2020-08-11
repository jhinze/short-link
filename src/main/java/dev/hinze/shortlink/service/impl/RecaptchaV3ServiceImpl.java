package dev.hinze.shortlink.service.impl;

import dev.hinze.shortlink.exception.RecaptchaV3Exception;
import dev.hinze.shortlink.model.RecaptchaV3Response;
import dev.hinze.shortlink.service.RecaptchaV3Service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;


@Service
public class RecaptchaV3ServiceImpl implements RecaptchaV3Service {

    @Value("${google.recaptcha.v3.secret}")
    private String recaptchaSecret;
    @Value("${google.recaptcha.v3.min}")
    private BigDecimal minScore;
    @Value("${google.recaptcha.v3.action}")
    private String action;

    private final RestTemplate restTemplate;

    public RecaptchaV3ServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void verify(String response) {
        var ip = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest()
                        .getRemoteAddr();
        var uri = UriComponentsBuilder.fromHttpUrl("https://www.google.com/recaptcha/api/siteverify")
                        .queryParam("secret", recaptchaSecret)
                        .queryParam("response", response)
                        .queryParam("remoteip", ip)
                        .toUriString();
        var recaptchaV3Response = restTemplate.postForObject(uri, null, RecaptchaV3Response.class);
        if(recaptchaV3Response == null
                || !StringUtils.equals(action, recaptchaV3Response.getAction())
                || recaptchaV3Response.getScore().compareTo(minScore) < 0)
            throw new RecaptchaV3Exception("Verification failed");
    }

}
