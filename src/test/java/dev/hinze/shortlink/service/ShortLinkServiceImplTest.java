package dev.hinze.shortlink.service;

import dev.hinze.shortlink.exception.ShortLinkCreateException;
import dev.hinze.shortlink.model.ShortLink;
import dev.hinze.shortlink.repository.ShortLinkRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ShortLinkService.class)
public class ShortLinkServiceImplTest {

    @MockBean
    private ShortLinkRepository shortLinkRepository;
    @Autowired
    private ShortLinkService shortLinkServiceImpl;

    private final ShortLink shortLink =
            new ShortLink()
                    .setId("123456")
                    .setToUrl("https://some.url")
                    .setCreatedOn(OffsetDateTime.now());

    @Test
    public void shouldGetShortLink() {
        when(shortLinkRepository.findById(anyString())).thenReturn(Optional.of(shortLink));
        var shortLink = shortLinkServiceImpl.get("123456");
        assertThat(shortLink)
                .isEqualTo(shortLink);
    }

    @Test
    public void shouldDeleteShortLink() {
        shortLinkServiceImpl.delete(shortLink);
        verify(shortLinkRepository, times(1))
                .delete(eq(shortLink));
    }

    @Test
    public void shouldCreateShortLink() {
        when(shortLinkRepository.save(any(ShortLink.class))).thenReturn(shortLink);
        shortLinkServiceImpl.create("http://some.place", null);
        verify(shortLinkRepository, times(1))
                .save(any(ShortLink.class));
    }

    @Test
    public void shouldThrowExceptionIfUnableToCreateUniqueId() {
        when(shortLinkRepository.save(any(ShortLink.class))).thenThrow(DataIntegrityViolationException.class);
        Assertions.assertThrows(ShortLinkCreateException.class, () ->
                shortLinkServiceImpl.create("http://some.place", null));
    }

}
