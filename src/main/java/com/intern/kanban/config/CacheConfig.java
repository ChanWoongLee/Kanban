package com.intern.kanban.config;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableCaching
public class CacheConfig implements WebMvcConfigurer {
	@Autowired
	private CacheManager cacheManager;

	@Bean
	public CacheManager cacheManager() {
		return new EhCacheCacheManager(Objects.requireNonNull(ehCacheCacheManager().getObject()));
	}

	@Bean
	public EhCacheManagerFactoryBean ehCacheCacheManager() {
		EhCacheManagerFactoryBean factory = new EhCacheManagerFactoryBean();
		factory.setConfigLocation(new ClassPathResource("ehcache/ehcache.xml"));
		factory.setShared(true);
		return factory;
	}
}
