package com.service;

import com.github.jedis.lock.JedisLock;
import com.utils.JdeisUtils;
import redis.clients.jedis.Jedis;

import java.util.List;

public class newUser {

    public String create(String username, String password) {
        Jedis jedis = JdeisUtils.getJedis();
        String res="";
        long re = 0;
        JedisLock jedisLock = new JedisLock(jedis,"key");
        try {
            jedisLock.acquire();
            re=jedis.hsetnx("user",username,password);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (jedisLock!=null){
                jedisLock.release();
            }
        }
        if(re==0){
            res="账号已存在！";
        }else{
            res="注册成功！";
        }
        return res;
    }

    public String login(String username, String password) {
        Jedis jedis = JdeisUtils.getJedis();
        String res="";
        String lspass="";
        long re = 0;
        JedisLock jedisLock = new JedisLock(jedis,"key");
        try {
            jedisLock.acquire();
            if(jedis.hexists("user",username)==false){
                return "账号错误！";
            }
            lspass=jedis.hget("user",username);
            if(lspass.equals("zhanghaoyifeng")){
                res="账号已封禁！";
                return res;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (jedisLock!=null){
                jedisLock.release();
            }
        }
        if(lspass.equals(password)){
            res="登录成功！";
        }else{
            res="密码错误！";
        }
        return res;
    }

    public String mone(String username) {
        Jedis jedis = JdeisUtils.getJedis();
        String res="";
        String lspass="";
        String lsmone="";
        String lstime="";
        long lstime1,lstime2;
        double lsmo=0.0;
        long re = 0;
        JedisLock jedisLock = new JedisLock(jedis,"key");
        try {
            jedisLock.acquire();
            if(jedis.hexists("user",username)==false){
                return "账号错误！";
            }
            lspass=jedis.hget("user",username);
            if(lspass.equals("zhanghaoyifeng")){
                res="账号已封禁！";
                return res;
            }
            //判断账号上次得到金币时间
            if(jedis.hexists("time",username)==false){
                //return "账号错误！";
                jedis.hset("time",username, String.valueOf(System.currentTimeMillis()/1000));
                jedis.hset("mone",username, "0.01");
            }else{
                //判断时间
                lstime=jedis.hget("time",username);
                lstime1=Long.parseLong(lstime);
                lstime2=System.currentTimeMillis()/1000;
                //System.out.println(lstime1+"----"+lstime2);
                if(lstime2-lstime1>=420){//获取金币间隔
                    jedis.hset("time",username, String.valueOf(lstime2));
                    //增加金币
                    if(jedis.hexists("mone",username)==false){
                        jedis.hset("mone",username, "0.01");
                    }else{
                        lsmo=Double.parseDouble(jedis.hget("mone",username));
                        lsmo=lsmo+0.01;
                        jedis.hset("mone",username,String.format("%.2f",lsmo));
                    }
                    res="成功!";
                    //增加完毕
                }else{
                    //封号
                    jedis.hset("user",username,"zhanghaoyifeng");
                    res="账号封禁!";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (jedisLock!=null){
                jedisLock.release();
            }
        }
        /*if(lspass.equals(lsmone)){
            res="登录成功！";
        }else{
            res="密码错误！";
        }*/
        return res;
    }

    public String look(String username) {
        Jedis jedis = JdeisUtils.getJedis();
        String res="";
        String lspass="";
        long re = 0;
        JedisLock jedisLock = new JedisLock(jedis,"key");
        try {
            jedisLock.acquire();
            if(jedis.hexists("user",username)==false){
                return "账号错误！";
            }
            lspass=jedis.hget("user",username);
            if(lspass.equals("zhanghaoyifeng")){
                res="账号已封禁！";
                return res;
            }
            res=jedis.hget("mone",username);
            //System.out.println(res);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (jedisLock!=null){
                jedisLock.release();
            }
        }
        return res;
    }

    public String cash(String username, String password, String name, String account, String money) {
        String res = login(username,password);
        if(res.equals("登录成功！")==false){
            return "提现失败！";
        }else{
            Jedis jedis = JdeisUtils.getJedis();
            JedisLock jedisLock = new JedisLock(jedis,"key");
            try {
                //判断余额是否足够
                String lsmone=look(username);
                if(lsmone.indexOf('!')!=-1){
                    return "提现失败！";
                }
                if(Double.parseDouble(lsmone)<Double.parseDouble(money)){
                    return "余额不足！";
                }
                jedisLock.acquire();
                double lsmo=Double.parseDouble(lsmone)-Double.parseDouble(money);
                jedis.hset("mone",username,String.format("%.2f",lsmo));
                jedis.lpush("cash",username+"----"+name+"----"+account+"----"+money);
                List<String> lscash = jedis.lrange("cash",0,-1);
                for (String s : lscash) {
                    System.out.println(s);
                }
                res="提现成功！";
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (jedisLock!=null){
                    jedisLock.release();
                }
            }
            return res;
        }
    }
}
