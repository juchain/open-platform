package com.blockshine.common.config;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

@Component
public class JedisService implements ApplicationContextAware {

      ApplicationContext applicationContext = null;

     public static JedisPool jedisPool=  null;
     public static Jedis jedis = null;

     private static Logger logger = Logger.getLogger(JedisService.class);



    public JedisService(){

    }

    public Jedis getJedis(){
        if (jedis ==null){
            synchronized (Jedis.class){
                if (jedis ==null){
                    jedis = getJedisPool().getResource();
                }else {
                    logger.info("jedis is not null");
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
        return getJedis().exists(key);
    }

    /**
     * 设置key -value 形式数据
     * @param key
     * @param value
     * @return
     */
    public  String set(String key,String value){
        String result =  getJedis().set(key,value);
        return result;
    }

    /**
     * 设置 一个过期时间
     * @param key
     * @param value
     * @param timeOut 单位秒
     * @return
     */
    public  String set(String key,String value,int timeOut){
        return getJedis().setex(key,timeOut,value);
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
        return getJedis().setex(key.getBytes(),timeOut,value.getBytes());
    }


    /**
     * 设置 一个过期时间
     * @param key
     * @return
     */
    public  byte[] getex(String key){
        return getJedis().get(key.getBytes());
    }

    /**
     * 根据key获取value
     * @param key
     * @return
     */
    public  String getByKey(String key){
        return getJedis().get(key);
    }

    /**
     * 根据通配符获取所有匹配的key
     * @param pattern
     * @return
     */
    public  Set<String> getKesByPattern(String pattern){
        return getJedis().keys(pattern);
    }

    /**
     * 根据key删除
     * @param key
     */
    public  void delByKey(String key){
        getJedis().del(key);
    }

    /**
     * 根据key获取过期时间
     * @param key
     * @return
     */
    public  long getTimeOutByKey(String key){
        return getJedis().ttl(key);
    }

    /**
     * 清空数据 【慎用啊！】
     */
    public  void flushDB(){
        getJedis().flushDB();
    }

    /**
     * 刷新过期时间
     * @param key
     * @param timeOut
     * @return
     */
    public  long refreshLiveTime(String key,int timeOut){
        return getJedis().expire(key,timeOut);
    }
}
