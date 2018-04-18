package com.blockshine.common.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.blockshine.common.constant.CodeConstant;
import com.blockshine.common.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableCaching
@Slf4j(topic = "RedisConfig")
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.pool.max-wait}")
    private long maxWaitMillis;

    @Value("${spring.redis.password}")
    private String password;
    @Bean
    public JedisPool redisPoolFactory() {
    	
    	log.info("redis init --- host:"+host+" port:"+port+" timeout:"+timeout+" maxIdle:"+maxIdle+" maxWaitMillis:"+maxWaitMillis);
    	
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, StringUtils.isEmpty(password)?null:password);
        
        if(jedisPool.getResource()==null) {
        	throw new BusinessException("can not get jedis", CodeConstant.JEDIS_ERROR);
        }
        
        return jedisPool;
    }

}
