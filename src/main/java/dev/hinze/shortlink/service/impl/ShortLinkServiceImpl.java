package dev.hinze.shortlink.service.impl;

import dev.hinze.shortlink.exception.ShortLinkCreateException;
import dev.hinze.shortlink.exception.ShortLinkNotFoundException;
import dev.hinze.shortlink.model.ShortLink;
import dev.hinze.shortlink.repository.ShortLinkRepository;
import dev.hinze.shortlink.service.ShortLinkService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;

import javax.persistence.LockModeType;
import java.time.OffsetDateTime;

@Service
@Slf4j
public class ShortLinkServiceImpl implements ShortLinkService {

    private static final char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    private final ShortLinkRepository shortLinkRepository;

    public ShortLinkServiceImpl(ShortLinkRepository shortLinkRepository) {
        this.shortLinkRepository = shortLinkRepository;
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    public ShortLink create(String to, OffsetDateTime expiration) {
        var shortLink = new ShortLink(getUniqueRandomSixDigitBase62(), to, OffsetDateTime.now(), expiration);
        log.info("Creating short link {}", shortLink.getId());
        return shortLinkRepository.save(shortLink);
    }

    @Cacheable(value = "shortLink", key = "#shortLinkId")
    @Override
    public ShortLink get(String shortLinkId) {
        log.info("Finding short link {}", shortLinkId);
        return shortLinkRepository.findById(shortLinkId)
                .orElseThrow(() -> new ShortLinkNotFoundException(shortLinkId + " not found"));
    }

    @CacheEvict(value = "shortLink", key = "#shortLink.id")
    @Override
    public void delete(ShortLink shortLink) {
        log.info("Deleting short link {}", shortLink.getId());
        shortLinkRepository.delete(shortLink);
    }

    private String getUniqueRandomSixDigitBase62() {
        int attempts = 10;
        while(attempts-- > 0) {
            var string = RandomStringUtils.random(6, chars);
            if(!shortLinkRepository.existsById(string))
                return string;
        }
        throw new ShortLinkCreateException("error generating unique string");
    }

}
