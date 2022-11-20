package com.fedex.beffr.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ConfigurationProperties(prefix = "config.aggregation")
public class AggregationConfigProperties {
    private Long timerLimitMilliseconds;
    private Long queueLimit;
}
