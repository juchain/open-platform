package com.blockshine.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.blockshine.common.constant.CodeConstant;
import com.blockshine.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

@Component()
@Slf4j(topic = "JedisService")
public class JedisService {

	@Autowired
	private JedisPool jedisPool;

	/**
	 * 获取Jedis对象
	 * 
	 * @return
	 */
	public synchronized Jedis getJedis() {
		Jedis jedis = null;
		if (jedisPool != null) {
			try {
				jedis = jedisPool.getResource();
			} catch (Exception e) {
				log.error("getJedis error" + e);
				throw new BusinessException("can not get jedis", CodeConstant.JEDIS_ERROR);
			}
		}
		return jedis;
	}

	/**
	 * 根据key查看是否存在
	 * 
	 * @param key
	 * @return
	 */
	public boolean hasKey(String key) {
		Jedis jedis = getJedis();
		try {
			return jedis.exists(key);
		} finally {
			returnJedis(jedis);
		}

	}

	/**
	 * 设置key -value 形式数据
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public String set(String key, String value) {
		Jedis jedis = getJedis();
		try {
			return jedis.set(key, value);
		} finally {
			returnJedis(jedis);
		}

	}

	/**
	 * 设置 一个过期时间
	 * 
	 * @param key
	 * @param value
	 * @param timeOut
	 *            单位秒
	 * @return
	 */
	public String set(String key, String value, int timeOut) {
		Jedis jedis = getJedis();
		try {
			return jedis.setex(key, timeOut, value);
		} finally {
			returnJedis(jedis);
		}

	}

	/**
	 * 对象hash 设置 一个过期时间
	 * 
	 * @param key
	 * @param value
	 * @param timeOut
	 *            单位秒
	 * @return
	 */
	public String setex(String key, String value, int timeOut) {
		Jedis jedis = getJedis();

		try {
			return jedis.setex(key.getBytes(), timeOut, value.getBytes());
		} finally {
			returnJedis(jedis);
		}

	}

	/**
	 * 设置 一个过期时间
	 * 
	 * @param key
	 * @return
	 */
	public byte[] getex(String key) {
		Jedis jedis = getJedis();
		try {
			return jedis.get(key.getBytes());
		} finally {
			returnJedis(jedis);
		}

	}

	/**
	 * 根据key获取value
	 * 
	 * @param key
	 * @return
	 */
	public String getByKey(String key) {
		Jedis jedis = getJedis();

		try {
			return jedis.get(key);
		} finally {
			returnJedis(jedis);
		}

	}

	/**
	 * 根据通配符获取所有匹配的key
	 * 
	 * @param pattern
	 * @return
	 */
	public Set<String> getKesByPattern(String pattern) {
		Jedis jedis = getJedis();
		try {
			return jedis.keys(pattern);
		} finally {
			returnJedis(jedis);
		}

	}

	/**
	 * 根据key删除
	 * 
	 * @param key
	 */
	public void delByKey(String key) {
		Jedis jedis = getJedis();
		try {
			jedis.del(key);
		} finally {
			returnJedis(jedis);
		}

	}

	/**
	 * 根据key获取过期时间
	 * 
	 * @param key
	 * @return
	 */
	public long getTimeOutByKey(String key) {
		Jedis jedis = getJedis();
		try {
			return jedis.ttl(key);
		} finally {
			returnJedis(jedis);
		}

	}

	/**
	 * 清空数据 【慎用啊！】
	 */
	public void flushDB() {
		Jedis jedis = getJedis();
		try {
			jedis.flushDB();
		} finally {
			returnJedis(jedis);
		}
	}

	/**
	 * 刷新过期时间
	 * 
	 * @param key
	 * @param timeOut
	 * @return
	 */
	public long refreshLiveTime(String key, int timeOut) {
		Jedis jedis = getJedis();
		try {
			return jedis.expire(key, timeOut);
		} finally {
			returnJedis(jedis);
		}
	}

	/**
	 * 回收jedis(放到finally中)
	 * 
	 * @param jedis
	 */
	public void returnJedis(Jedis jedis) {
//		if (jedis.isConnected()) {
//		} else {
//			log.info(jedis + " --- ");
//		}
		jedis.close();
	}

//	public static void main(String[] args) {
//		JedisPool jap = new JedisPool("47.100.174.228", 6379);
//		for (int i = 0; i < 10; i++) {
//			// JedisPool jap = new JedisPool("47.100.174.228",6379);
//			System.out.println(jap);
//			Jedis jedis = jap.getResource();
//			System.out.println(i + "---" + jedis);
//			jedis.close();
//			if (i == 9) {
//				System.out.println("adf");
//			}
//		}
//
//	}

}
