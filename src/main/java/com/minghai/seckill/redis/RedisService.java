package com.minghai.seckill.redis;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author minghai
 * @description
 * @date 2019/12/4
 */
@Service
@Slf4j
public class RedisService {

    @Autowired
    private JedisPool jedisPool;
    
    /**
     * @Author minghai
     * @Description 获取单个对象
     * @Date 2019/12/12 11:41 
     * @Param [prefix, key, clazz]
     * @return T
     */
    public <T> T get(KeyPrefix prefix,String key, Class<T> clazz){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            // 生成正真的key
            String realKey = prefix.getPrefix() + key;
            String str =  jedis.get(realKey);

            T t = stringToBean(str,clazz);
           return t;
        }finally {
            returnToPool(jedis);
        }
    }
    /**
     * @Author minghai
     * @Description 设置对象
     * @Date 2019/12/12 11:42 
     * @Param [prefix, key, value]
     * @return boolean
     */
    public <T> boolean set(KeyPrefix prefix, String key, T value){
        Jedis jedis = null;
        try{
           jedis = jedisPool.getResource();
           String str = beanToString(value);
           if(str == null || str.length() <= 0){
               return false;
           }
            // 生成正真的key
            String realKey = prefix.getPrefix() + key;

           int seconds = prefix.expireSeconds();
           if(seconds <= 0){
               jedis.set(realKey,str);
           }else{
               jedis.setex(realKey,seconds,str);
           }


           return true;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * @Author minghai
     * @Description 判断是否存在
     * @Date 2019/12/12 11:43
     * @Param [prefix, key]
     * @return boolean
     */
    public <T> boolean exists(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try{
           jedis = jedisPool.getResource();
            // 生成正真的key
            String realKey = prefix.getPrefix() + key;
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * @Author minghai
     * @Description 增加值
     * @Date 2019/12/12 11:44
     * @Param [prefix, key]
     * @return java.lang.Long
     */
    public <T> Long incr(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try{
           jedis = jedisPool.getResource();
            // 生成正真的key
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * @Author minghai
     * @Description 减少值
     * @Date 2019/12/12 11:44
     * @Param [prefix, key]
     * @return java.lang.Long
     */
    public <T> Long decr(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try{
           jedis = jedisPool.getResource();
            // 生成正真的key
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    private <T> String beanToString(T value) {
        if(value == null){
            return null;
        }
        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz == Integer.class){
            return ""+value;
        }else if(clazz == String.class){
            return (String)value;
        }else if(clazz == long.class || clazz == Long.class){
            return ""+value;
        }else{
            return JSON.toJSONString(value);
        }
    }

    private <T> T stringToBean(String str, Class<T> clazz) {
        if(str == null || str.length() <= 0 || clazz == null){
            return null;
        }
        if(clazz == int.class || clazz == Integer.class){
            return (T)Integer.valueOf(str);
        }else if(clazz == String.class){
            return (T)str;
        }else if(clazz == long.class || clazz == Long.class){
            return (T)Long.valueOf(str);
        }else{
            return JSON.toJavaObject(JSON.parseObject(str),clazz);
        }

    }

    private void returnToPool(Jedis jedis) {
        if(jedis != null){
            jedis.close();
        }
    }
}
