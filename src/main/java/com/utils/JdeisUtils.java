package com.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JdeisUtils {
    private static JedisPool pool = null;
    static{
        InputStream in = JdeisUtils.class.getClassLoader().getResourceAsStream("redis.properties");
        Properties pro = new Properties();
        if(in!=null){
            try {
                pro.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxIdle(Integer.parseInt(pro.get("maxIdle").toString()));
            poolConfig.setMinIdle(Integer.parseInt(pro.get("minIdle").toString()));
            poolConfig.setMaxTotal(Integer.parseInt(pro.get("maxTotal").toString()));
            pool =new JedisPool(poolConfig,pro.get("url").toString(),Integer.parseInt(pro.get("port").toString()));
        }else{
            System.out.println("error");
        }
        //Jedis jedis = pool.getResource();
        //System.out.println(jedis.get("username"));
    }

    public static Jedis getJedis()
    {
        return pool.getResource();
    }
//
//    public static void main(String[] args)
//    {
//        Jedis jedis = getJedis();
//        System.out.println(jedis.get("username"));
//    }
}
