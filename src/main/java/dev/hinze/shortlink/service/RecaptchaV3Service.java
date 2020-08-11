package dev.hinze.shortlink.service;


public interface RecaptchaV3Service {

    void verify(String response);

}
