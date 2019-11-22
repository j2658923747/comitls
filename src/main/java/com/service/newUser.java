package com.service;

import com.github.jedis.lock.JedisLock;
import com.utils.JdeisUtils;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Random;

public class newUser {

    public String create(String username, String password) {
        System.out.println("开始新建！");
        Jedis jedis = JdeisUtils.getJedis();
        String res="";
        long re = 0;
        //JedisLock jedisLock = new JedisLock(jedis,"key",100,1000);
        try {
          //  jedisLock.acquire();
            re=jedis.hsetnx("user",username,password);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
//            if (jedisLock!=null){
//                jedisLock.release();
//            }
            if (jedis!=null){
                jedis.close();
            }
        }
        if(re==0){
            res="账号已存在！";
        }else{
            res="注册成功！";
        }
        System.out.println("新建结束！");
        return res;
    }

    public String login(String username, String password) {
        System.out.println("开始登录！");
        Jedis jedis = JdeisUtils.getJedis();
        String res="";
        String lspass="";
        long re = 0;
        //JedisLock jedisLock = new JedisLock(jedis,"key",100,1000);
        try {
            //jedisLock.acquire();
            if(jedis.hexists("user",username)==false){
                res="账号错误！";
            }else{
                lspass=jedis.hget("user",username);
                if(lspass.equals("zhanghaoyifeng")){
                    res="账号已封禁！";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
//            if (jedisLock!=null){
//                jedisLock.release();
//            }
            if (jedis!=null){
                jedis.close();
            }
        }
        if(res.equals("")==false){
            System.out.println("登录结束！");
            return res;
        }
        if(lspass.equals(password)){
            res="登录成功！";
        }else{
            res="密码错误！";
        }
        System.out.println("登录结束！");
        return res;

    }

    public String mone(String username,String password) {
        System.out.println("开始增加！");
        Jedis jedis = JdeisUtils.getJedis();
        String res="";
        String lspass="";
        String lsmone="";
        String lstime="";
        long lstime1,lstime2;
        double lsmo=0.0;
        long re = 0;
        lspass=login(username,password);

        if(lspass.equals("登录成功！")==false){
            res=lspass;
            System.out.println("增加结束！");
            return res;
        }
        //JedisLock jedisLock = new JedisLock(jedis,"key",100,1000);
        try {

            //jedisLock.acquire();

            Random random = new Random();
            int cas = random.nextInt(3)+10;
            //判断账号上次得到金币时间
            if(jedis.hexists("time",username)==false){
                jedis.hset("time",username, String.valueOf(System.currentTimeMillis()/1000));
                jedis.hset("mone",username, String.valueOf(cas));
                res="金币增加成功!";
            }else{
                //判断时间
                lstime=jedis.hget("time",username);
                lstime1=Long.parseLong(lstime);
                lstime2=System.currentTimeMillis()/1000;
                //System.out.println(lstime1+"----"+lstime2);
                if(lstime2-lstime1>=60){//获取金币间隔
                    jedis.hset("time",username, String.valueOf(lstime2));
                    //增加金币
                    if(jedis.hexists("mone",username)==false){
                        jedis.hset("mone",username, String.valueOf(cas));
                    }else{
                        lsmo=Double.parseDouble(jedis.hget("mone",username));
                        lsmo=lsmo+cas;
                        jedis.hset("mone",username,String.format("%.0f",lsmo));
                    }
                    res="金币增加成功!";
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
//            if (jedisLock!=null){
//                jedisLock.release();
//            }
            if (jedis!=null){
                jedis.close();
            }
        }
        /*if(lspass.equals(lsmone)){
            res="登录成功！";
        }else{
            res="密码错误！";
        }*/
        System.out.println("增加结束！");
        return res;
    }

    public String look(String username) {
        System.out.println("开始查看！");
        Jedis jedis = JdeisUtils.getJedis();
        String res="";
        String lspass="";
        long re = 0;
        //JedisLock jedisLock = new JedisLock(jedis,"key",100,1000);
        try {
            //jedisLock.acquire();
            if(jedis.hexists("user",username)==false){
                res="账号错误！";
            }else{
                lspass=jedis.hget("user",username);
                if(lspass.equals("zhanghaoyifeng")){
                    res="账号已封禁！";
                }
                if(res.equals(""))
                    res=jedis.hget("mone",username);
            }
            //System.out.println(res);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
//            if (jedisLock!=null){
//                jedisLock.release();
//            }
            if (jedis!=null){
                jedis.close();
            }
        }
        System.out.println("查看结束！");
        return res;
    }

    public String cash(String username, String password, String name, String account, String money) {
        System.out.println("开始提现！");
        String res = login(username,password);
        if(res.equals("登录成功！")==false){
            System.out.println("提现结束！");
            return "提现失败！";
        }else{
            String lsmone=look(username);
            if(lsmone.indexOf('!')!=-1){
                return "提现失败！";
            }
            if(Double.parseDouble(lsmone)<Double.parseDouble(money)){
                return "余额不足！";
            }
            Jedis jedis = JdeisUtils.getJedis();
            //JedisLock jedisLock = new JedisLock(jedis,"key",100,1000);
            try {
                //判断余额是否足够
                //jedisLock.acquire();
                double lsmo=Double.parseDouble(lsmone)-Double.parseDouble(money);
                long re=jedis.hsetnx("CASH",username,name+"----"+account+"----"+money);
                if(re==0){
                    res="请到账后提现！";
                }else{
                    jedis.hset("mone",username,String.format("%.2f",lsmo));
                    res="提现成功！";
                }
                //jedis.lpush("cash",username+"----"+name+"----"+account+"----"+money);
                /*List<String> lscash = jedis.lrange("cash",0,-1);
                for (String s : lscash) {
                    System.out.println(s);
                }*/
            }catch (Exception e){
                e.printStackTrace();
            }finally {
//                if (jedisLock!=null){
//                    jedisLock.release();
//                }
                if (jedis!=null){
                    jedis.close();
                }
            }
            System.out.println("提现！");
            return res;
        }
    }
}
