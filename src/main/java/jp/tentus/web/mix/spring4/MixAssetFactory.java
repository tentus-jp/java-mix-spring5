package jp.tentus.web.mix.spring5;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MixAssetFactory {

    @Bean
    public Asset mix() {
        return new Asset();
    }

}
