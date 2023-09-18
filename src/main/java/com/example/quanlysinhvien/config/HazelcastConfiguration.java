package com.example.quanlysinhvien.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {
    @Bean
    public Config hazelcastConfig() {
        Config config = new Config();
        MapConfig mapConfig = new MapConfig("manage-map");
        mapConfig.setMaxIdleSeconds(60);
        mapConfig.setTimeToLiveSeconds(360);
        config.addMapConfig(mapConfig);

        return config;
    }
}
