package dev.hinze.shortlink.controller;

import dev.hinze.shortlink.model.RecaptchaV3Response;
import dev.hinze.shortlink.model.ShortLink;
import dev.hinze.shortlink.repository.ShortLinkRepository;
import dev.hinze.shortlink.service.impl.RecaptchaV3ServiceImpl;
import dev.hinze.shortlink.service.impl.ShortLinkServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = ShortLinkController.class,
        properties = {
                "google.recaptcha.v3.secret=xxxx",
                "google.recaptcha.v3.verify=true",
                "google.recaptcha.v3.min=0.7",
                "google.recaptcha.v3.action=action"
        })
@ContextConfiguration(
        classes = {
                ShortLinkController.class,
                ShortLinkServiceImpl.class,
                RecaptchaV3ServiceImpl.class
        })
public class ShortLinkControllerTest {

    @MockBean
    private ShortLinkRepository shortLinkRepository;
    @MockBean
    private RestTemplate restTemplate;
    @Captor
    private ArgumentCaptor<ShortLink> shortLinkArgumentCaptor;
    @Autowired
    private MockMvc mockMvc;

    private final ShortLink shortLink =
            new ShortLink("123456", "https://1.b/123456", "https://some.url", OffsetDateTime.now(), null);
    private final ShortLink expiredShortLink =
            new ShortLink("abcdef", "https://1.b/abcdef", "https://some.url", OffsetDateTime.now(), OffsetDateTime.now().minusHours(1));
    private final RecaptchaV3Response recaptchaV3Response =
            new RecaptchaV3Response(true, BigDecimal.valueOf(0.7), "action", OffsetDateTime.now(), "localhost", new String[]{});
    private final RecaptchaV3Response lowScoreRecaptchaV3Response =
            new RecaptchaV3Response(true, BigDecimal.valueOf(0.69), "action", OffsetDateTime.now(), "localhost", new String[]{});

    @BeforeEach
    public void before() {
        when(shortLinkRepository.findById(eq("123456"))).thenReturn(Optional.of(shortLink));
        when(shortLinkRepository.findById(eq("abcdef"))).thenReturn(Optional.of(expiredShortLink));
        when(restTemplate.postForObject(contains("good"), any(), eq(RecaptchaV3Response.class)))
                .thenReturn(recaptchaV3Response);
        when(restTemplate.postForObject(contains("bad"), any(), eq(RecaptchaV3Response.class)))
                .thenReturn(lowScoreRecaptchaV3Response);
    }

    @Test
    public void shouldThrowRecaptchaException() throws Exception {
        var to = "http://shorten.me";
        mockMvc.perform(post("/link?to=" + to + "&recaptchaResponse=bad"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldCreateShortLinkWithExpiration() throws Exception {
        var to = "http://shorten.me";
        var exp = "2020-07-01T01:30:00-04:00";
        mockMvc.perform(post("/link?to=" + to + "&expiration=" + exp + "&recaptchaResponse=good"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(shortLinkRepository, times(1)).save(shortLinkArgumentCaptor.capture());
        var shortLink = shortLinkArgumentCaptor.getValue();
        assertThat(shortLink.getToUrl())
                .isEqualTo(to);
        assertThat(shortLink.getExpiresOn())
                .usingDefaultComparator()
                .isEqualTo(OffsetDateTime.parse(exp));
        assertThat(shortLink.getId())
                .isNotBlank();
        assertThat(shortLink.getShortLink())
                .isEqualTo("http://localhost/" + shortLink.getId());
    }

    @Test
    public void shouldCreateShortLinkWithoutExpiration() throws Exception {
        var to = "http://shorten.me";
        mockMvc.perform(post("/link?to=" + to + "&recaptchaResponse=good"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(shortLinkRepository, times(1)).save(shortLinkArgumentCaptor.capture());
        var shortLink = shortLinkArgumentCaptor.getValue();
        assertThat(shortLink.getToUrl())
                .isEqualTo(to);
        assertThat(shortLink.getExpiresOn())
                .isNull();
        assertThat(shortLink.getId())
                .isNotBlank();
    }

    @Test
    public void shouldRedirectTo() throws Exception {
        mockMvc.perform(get("/123456"))
                .andDo(print())
                .andExpect(redirectedUrl(shortLink.getToUrl()))
                .andExpect(status().isFound());
        verify(shortLinkRepository, times(0))
                .delete(any());
    }

    @Test
    public void shouldErrorIfNotFound() throws Exception {
        mockMvc.perform(get("/654321"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldDeleteIfExpired() throws Exception {
        mockMvc.perform(get("/abcdef"))
                .andDo(print())
                .andExpect(status().isNotFound());
        verify(shortLinkRepository, times(1))
                .delete(eq(expiredShortLink));
    }

}
