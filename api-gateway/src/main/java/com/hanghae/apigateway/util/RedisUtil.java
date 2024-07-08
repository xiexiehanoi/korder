package com.hanghae.apigateway.util;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class RedisUtil {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public RedisUtil(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Boolean> setValue(String key, String value, Duration duration) {
        return redisTemplate.opsForValue().set(key, value, duration);
    }

    public Mono<String> getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Mono<Boolean> deleteValue(String key) {
        return redisTemplate.opsForValue().delete(key);
    }
}
