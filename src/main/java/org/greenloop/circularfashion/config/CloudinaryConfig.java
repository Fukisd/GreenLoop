package org.greenloop.circularfashion.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name:dmpjc496u}")
    private String cloudName;

    @Value("${cloudinary.api-key:867162548936863}")
    private String apiKey;

    @Value("${cloudinary.api-secret:t_Wp6_Yoc8xLv0nXXfqO-gIVF8I}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }
}
