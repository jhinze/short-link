package dev.hinze.shortlink.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecaptchaV3Response {

    private boolean success;
    private BigDecimal score;
    private String action;
    @JsonProperty("challenge_ts")
    private OffsetDateTime timestamp;
    private String hostname;
    @JsonProperty("error-codes")
    private String[] errorCodes;

}
