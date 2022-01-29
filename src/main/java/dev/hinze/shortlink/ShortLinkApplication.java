package dev.hinze.shortlink;

import dev.hinze.shortlink.generator.ShortLinkIdGenerator;
import org.hibernate.tuple.CreationTimestampGeneration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.nativex.hint.*;

@SpringBootApplication
@EnableCaching
@AotProxyHints({
		@AotProxyHint(targetClass=dev.hinze.shortlink.service.ShortLinkService.class, proxyFeatures = ProxyBits.IS_STATIC),
		@AotProxyHint(targetClass=dev.hinze.shortlink.service.RecaptchaV3Service.class, proxyFeatures = ProxyBits.IS_STATIC)
})
@TypeHints({
		@TypeHint(types = CreationTimestampGeneration.class),
		@TypeHint(types = ShortLinkIdGenerator.class)
})
public class ShortLinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShortLinkApplication.class, args);
	}

}
