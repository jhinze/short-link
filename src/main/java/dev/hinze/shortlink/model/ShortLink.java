package dev.hinze.shortlink.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.OffsetDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortLink {

    @Id
    private String id;
    private String toUrl;
    private OffsetDateTime createdOn;
    private OffsetDateTime expiresOn;

}
