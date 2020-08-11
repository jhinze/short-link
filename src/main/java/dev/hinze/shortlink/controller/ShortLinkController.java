package dev.hinze.shortlink.controller;

import dev.hinze.shortlink.exception.ShortLinkExpiredException;
import dev.hinze.shortlink.model.ShortLink;
import dev.hinze.shortlink.service.RecaptchaV3Service;
import dev.hinze.shortlink.service.ShortLinkService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.OffsetDateTime;

@RestController
@RequestMapping(value = "/")
public class ShortLinkController {

    @Value("${google.recaptcha.v3.verify:false}")
    private boolean recaptchaVerify;

    private final ShortLinkService shortLinkService;
    private final RecaptchaV3Service recaptchaV3Service;

    public ShortLinkController(ShortLinkService shortLinkService, RecaptchaV3Service recaptchaV3Service) {
        this.shortLinkService = shortLinkService;
        this.recaptchaV3Service = recaptchaV3Service;
    }

    @PostMapping("/link")
    public ShortLink createLink(
            @RequestParam
                    String to,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    OffsetDateTime expiration,
            @RequestParam(required = false)
                    String recaptchaResponse) {
        if(recaptchaVerify)
            recaptchaV3Service.verify(recaptchaResponse);
        return shortLinkService.create(to, expiration);
    }

    @GetMapping("/{shortLinkPathVariable}")
    public ModelAndView toLink(@PathVariable String shortLinkPathVariable) {
        var shortLink = shortLinkService.get(shortLinkPathVariable);
        if(shortLink.getExpiresOn() != null && shortLink.getExpiresOn().isBefore(OffsetDateTime.now())) {
            shortLinkService.delete(shortLink);
            throw new ShortLinkExpiredException(shortLinkPathVariable + " is expired");
        }
        return new ModelAndView("redirect:" + shortLink.getToUrl());
    }

}
