package com.lt.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author gaijf
 * @description
 * @date 2020/12/3
 */
@Configuration
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(factory);
        // 使用 GenericFastJsonRedisSerializer 替换默认序列化
        GenericFastJsonRedisSerializer genericFastJsonRedisSerializer = new GenericFastJsonRedisSerializer();
        // 设置key和value的序列化规则
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(genericFastJsonRedisSerializer);
        // 设置hashKey和hashValue的序列化规则
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(genericFastJsonRedisSerializer);
        // 设置支持事物
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }
}
