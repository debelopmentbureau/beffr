package com.fedex.beffr.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ConfigurationProperties(prefix = "service.backend-services")
public class BackendServicesProperties {
    private String url;
    private String shipmentsPath;
    private String pricingPath;
    private String trackPath;
}
