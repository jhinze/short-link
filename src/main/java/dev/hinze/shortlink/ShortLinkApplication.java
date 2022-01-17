package dev.hinze.shortlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.nativex.hint.AotProxyHint;
import org.springframework.nativex.hint.AotProxyHints;
import org.springframework.nativex.hint.ProxyBits;

@SpringBootApplication
@EnableCaching
@AotProxyHints({
		@AotProxyHint(targetClass=dev.hinze.shortlink.service.ShortLinkService.class, proxyFeatures = ProxyBits.IS_STATIC),
		@AotProxyHint(targetClass=dev.hinze.shortlink.service.RecaptchaV3Service.class, proxyFeatures = ProxyBits.IS_STATIC)
})
public class ShortLinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShortLinkApplication.class, args);
	}

}
