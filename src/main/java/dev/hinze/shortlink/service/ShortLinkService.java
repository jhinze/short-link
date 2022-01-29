package dev.hinze.shortlink.service;

import dev.hinze.shortlink.exception.ShortLinkCreateException;
import dev.hinze.shortlink.exception.ShortLinkNotFoundException;
import dev.hinze.shortlink.model.ShortLink;
import dev.hinze.shortlink.repository.ShortLinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@Slf4j
public class ShortLinkService {

    private static final int SAVE_ATTEMPTS = 10;
    private final ShortLinkRepository shortLinkRepository;

    public ShortLinkService(ShortLinkRepository shortLinkRepository) {
        this.shortLinkRepository = shortLinkRepository;
    }

    public ShortLink create(String to, OffsetDateTime expiration) {
        for(int i = 0; i < SAVE_ATTEMPTS; i++) {
            try {
                var shortLink = new ShortLink()
                        .setToUrl(to)
                        .setExpiresOn(expiration);
                shortLink = shortLinkRepository.save(shortLink);
                log.info("Created short link {}", shortLink.getId());
                return shortLink;
            } catch (DataIntegrityViolationException e) {
                log.warn("Unable to save short link", e);
            }
        }
        throw new ShortLinkCreateException("Error creating short link");
    }

    @Cacheable(value = "shortLink", key = "#shortLinkId")
    public ShortLink get(String shortLinkId) {
        log.info("Finding short link {}", shortLinkId);
        return shortLinkRepository.findById(shortLinkId)
                .orElseThrow(() -> new ShortLinkNotFoundException(shortLinkId + " not found"));
    }

    @CacheEvict(value = "shortLink", key = "#shortLink.id")
    public void delete(ShortLink shortLink) {
        log.info("Deleting short link {}", shortLink.getId());
        shortLinkRepository.delete(shortLink);
    }

}
