package com.nicole.jdisdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

@SpringBootTest
public class JdisDemoApplicationTests {

    @Test
    public void init01() {
        //初始化Jedis 客户端实例
        Jedis jedis = new Jedis("d9city.net",8379);
        //连接密码
        jedis.auth("jimy3k0519");

        //选择操作的库
        jedis.select(1);

        // 测试Redis 心跳连接
        String pong = "";
        int i=0;
        while ( i<=3 ){
            pong += jedis.ping() + "! ";
            i++;
        }

        System.out.println("测试Redis服务器心跳中..... " + pong);

        //设置键，值
        jedis.set("name","jimy3k");
        jedis.set("age","33");

        //取值
        String name = jedis.get("name");
        int age = Integer.parseInt(jedis.get("age"));
        System.out.println("姓名：" + name);
        System.out.println("年龄：" + age);

        //设置List 键，值
        Long len = jedis.llen("userlist");
        if (len <= 0) {
            jedis.lpush("userlist","jimy","jimy3k","zhangsan","lisi","wangwu");
        }

        //取值
        List<String> userlist = jedis.lrange("userlist",0,-1);
        System.out.println("用户列表：" + userlist);
        if (null != jedis){
            jedis.close();
        }
    }

    @Test
    public void init02(){
        //初始化JedisPool 连接池
        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(),"d9city.net",8379,10000,"jimy3k0519");
        //从连接池获取连接实例
        Jedis jedis = jedisPool.getResource();

        //选择操作的库
        jedis.select(1);

        // 测试Redis 心跳连接
        int i = 0;
        String pong = "";
        while ( i<=3 ){
            pong += jedis.ping() + "! ";
            i++;
        }

        System.out.println("测试Redis服务器心跳中..... " + pong);

        //设置键，值
        jedis.set("name","jimy3k");
        jedis.set("age","33");

        //取值
        String name = jedis.get("name");
        int age = Integer.parseInt(jedis.get("age"));
        System.out.println("姓名：" + name);
        System.out.println("年龄：" + age);

        //设置List 键，值
        Long len = jedis.llen("userlist");
        if (len <= 0) {
            jedis.lpush("userlist","jimy","jimy3k","zhangsan","lisi","wangwu");
        }

        //取值
        List<String> userlist = jedis.lrange("userlist",0,-1);
        System.out.println("用户列表：" + userlist);
        if (null != jedis){
            jedis.close();
        }

    }
}
