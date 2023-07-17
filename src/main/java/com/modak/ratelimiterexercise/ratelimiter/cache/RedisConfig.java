package com.modak.ratelimiterexercise.ratelimiter.cache;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.grid.jcache.JCacheProxyManager;
import org.redisson.config.Config;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

@Configuration
public class RedisConfig {

    @Bean
    public Config config()
    {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        return config;
    }

    @Bean
    public CacheManager cacheManager(Config config) {
        CachingProvider cachingProvider = Caching.getCachingProvider("org.redisson.jcache.JCachingProvider");
        CacheManager manager = cachingProvider.getCacheManager();
        Cache<Object, Object> cache = manager.getCache("cache");
        if (cache == null) {
            manager.createCache("cache", RedissonConfiguration.fromConfig(config));
        }
        return manager;
    }

    @Bean
    public ProxyManager<String> proxyManager(CacheManager cacheManager) {
        return new JCacheProxyManager<>(cacheManager.getCache("cache"));
    }

}
