package com.test;

import redis.clients.jedis.Jedis;

public class TestJedis {
    public void test1()
    {
        Jedis jedis = new Jedis("192.168.52.129",6379);
        jedis.set("username","北京");
        String username = jedis.get("username");
        System.out.println(username);
    }
}
