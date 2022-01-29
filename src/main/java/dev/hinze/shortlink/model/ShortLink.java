package dev.hinze.shortlink.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.*;
import javax.persistence.Entity;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ShortLink {

    @Id
    @GenericGenerator(name = "short_link_id", strategy = "dev.hinze.shortlink.generator.ShortLinkIdGenerator")
    @GeneratedValue(generator = "short_link_id")
    private String id;
    @Transient
    private String shortLink;
    private String toUrl;
    private OffsetDateTime createdOn;
    private OffsetDateTime expiresOn;

    @PostPersist
    @PostLoad
    private void postPersistOrLoad() {
        this.shortLink = ServletUriComponentsBuilder.fromCurrentContextPath()
                                .replacePath(this.id)
                                .build()
                                .toUriString();
    }

}
