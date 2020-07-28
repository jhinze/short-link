package dev.hinze.shortlink.service;

import dev.hinze.shortlink.model.ShortLink;

import java.time.OffsetDateTime;

public interface ShortLinkService {

    ShortLink create(String to, OffsetDateTime expiration);

    ShortLink get(String shortLink);

    void delete(ShortLink shortLink);

}
