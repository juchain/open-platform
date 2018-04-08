package com.blockshine.common.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

@Component()
public class JedisService implements ApplicationContextAware {

    ApplicationContext applicationContext = null;

    public static JedisPool jedisPool=  null;

    public JedisService(){

    }

    public Jedis getJedis(){
         Jedis jedis = null;
        if (jedis ==null){
            synchronized (Jedis.class){
                if (jedis ==null){
                    jedis = getJedisPool().getResource();
                }
            }
        }
        return jedis;
    }

    public JedisPool getJedisPool(){

        if (jedisPool ==null){
            synchronized (JedisPool.class){
                if (jedisPool==null){
                    jedisPool = applicationContext.getBean(JedisPool.class);
                }
            }
        }
        return jedisPool;
    }







    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(this.applicationContext == null){
            this.applicationContext  = applicationContext; //初始化 spring applicationContext
        }
    }

    /**
     * 根据key查看是否存在
     * @param key
     * @return
     */
    public  boolean hasKey(String key){

        Jedis jedis = getJedis();
        try {
            return jedis.exists(key);
        }finally {
            returnJedis(jedis);
        }


    }

    /**
     * 设置key -value 形式数据
     * @param key
     * @param value
     * @return
     */
    public  String set(String key,String value){
        Jedis jedis = getJedis();
        try {
            return  jedis.set(key,value);
        }finally {
            returnJedis(jedis);
        }

    }

    /**
     * 设置 一个过期时间
     * @param key
     * @param value
     * @param timeOut 单位秒
     * @return
     */
    public  String set(String key,String value,int timeOut){
        Jedis jedis = getJedis();
        try {
            return jedis.setex(key,timeOut,value);
        }finally {
            returnJedis(jedis);
        }

    }


    /**
     * 对象hash
     * 设置 一个过期时间
     * @param key
     * @param value
     * @param timeOut 单位秒
     * @return
     */
    public  String setex(String key,String value,int timeOut){
        Jedis jedis = getJedis();

        try {
            return jedis.setex(key.getBytes(),timeOut,value.getBytes());
        }finally {
            returnJedis(jedis);
        }

    }


    /**
     * 设置 一个过期时间
     * @param key
     * @return
     */
    public  byte[] getex(String key){
        Jedis jedis = getJedis();
        try {
            return jedis.get(key.getBytes());
        }finally {
            returnJedis(jedis);
        }


    }

    /**
     * 根据key获取value
     * @param key
     * @return
     */
    public  String getByKey(String key){
        Jedis jedis = getJedis();

        try {
            return jedis.get(key);
        }finally {
            returnJedis(jedis);
        }

    }

    /**
     * 根据通配符获取所有匹配的key
     * @param pattern
     * @return
     */
    public  Set<String> getKesByPattern(String pattern){
        Jedis jedis = getJedis();

        try {
            return jedis.keys(pattern);
        }finally {
            returnJedis(jedis);
        }

    }

    /**
     * 根据key删除
     * @param key
     */
    public  void delByKey(String key){
        Jedis jedis = getJedis();

        try {
            jedis.del(key);
        }finally {
            returnJedis(jedis);
        }

    }

    /**
     * 根据key获取过期时间
     * @param key
     * @return
     */
    public  long getTimeOutByKey(String key){
        Jedis jedis = getJedis();

        try {
            return jedis.ttl(key);
        }finally {
            returnJedis(jedis);
        }

    }

    /**
     * 清空数据 【慎用啊！】
     */
    public  void flushDB(){
        Jedis jedis = getJedis();

        try {
            jedis.flushDB();
        }finally {
            returnJedis(jedis);
        }



    }

    /**
     * 刷新过期时间
     * @param key
     * @param timeOut
     * @return
     */
    public  long refreshLiveTime(String key,int timeOut){
        Jedis jedis = getJedis();

        try {
            return jedis.expire(key,timeOut);
        }finally {
            returnJedis(jedis);
        }


    }



    /**
     * 回收jedis(放到finally中)
     * @param jedis
     */
    public void returnJedis(Jedis jedis) {
        if (null != jedis && null != jedisPool) {
            jedisPool.returnResource(jedis);
        }
    }
}
