package com.nicole.jdisdemo;

import com.nicole.jdisdemo.pojo.User;
import com.nicole.jdisdemo.utils.SerializeUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)   //解决不能 @Autowired 自动注入的问题
public class JdisDemoApplicationTests {

    @Autowired
    private JedisPool jedisPool;
    private Jedis jedis;

    @Before
    public void init() {
        jedis = jedisPool.getResource();
    }

    @After
    public void close() {
        if (null != jedis) {
            jedis.close();
        }
    }

    @Test
    public void init01() {
        //初始化Jedis 客户端实例
        Jedis jedis = new Jedis("d9city.net", 8379);
        //连接密码
        jedis.auth("jimy3k0519");

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
            jedis.lpush("userlist", "jimy", "jimy3k", "zhangsan", "lisi", "wangwu");
        }

        //取值
        List<String> userlist = jedis.lrange("userlist", 0, -1);
        System.out.println("用户列表：" + userlist);
        if (null != jedis) {
            jedis.close();
        }
    }

    /**
     * 使用配置Jedis类 单元测试
     **/
    @Test
    public void init03() {
        //选择操作的库
        jedis.select(1);

        // 测试Redis 心跳连接
        int i = 0;
        String pong = "";
        while (i <= 3) {
            pong += jedis.ping() + "! ";
            i++;
        }

        System.out.println("测试Redis服务器心跳中..... " + pong);

        //设置键，值；单条
        jedis.set("name", "jimy3k");
        jedis.set("age", "33");

        //取值；单条
        String name = jedis.get("name");
        int age = Integer.parseInt(jedis.get("age"));
        System.out.println("姓名：" + name);
        System.out.println("年龄：" + age);

        //设置键，值；多条
        jedis.mset("name", "jimy3k", "age", "30", "sex", "男");

        //删值；
        jedis.del("sex");

        //取值；多条
        List<String> list = jedis.mget("name", "age", "sex");
        System.out.println("多条取值：");
        for (String s : list) {
            System.out.println(s);
        }


        //设置List 键，值
        Long len = jedis.llen("userlist");
        if (len <= 0) {
            jedis.lpush("userlist", "jimy", "jimy3k", "zhangsan", "lisi", "wangwu");
        }

        //List 取值
        List<String> userlist = jedis.lrange("userlist", 0, -1);
        System.out.println("用户列表：" + userlist);

        //设置Hash 键，值
        Map<String, String> map = new HashMap<>();
        map.put("name", "jimy3k");
        map.put("age", "32");
        map.put("addr", "深圳");
        map.put("sex", "男");
        map.put("score", "98");
        jedis.hmset("user3", map);

        //Hash 取值
        userlist = jedis.hmget("user3", "name", "age", "addr", "sex", "score");
        System.out.println("user3：" + userlist);
    }

    /**
     * 使用Jedis配置类 键值失效时间单元测试
     **/
    @Test
    public void testExpired() {
        //设置键值时，设置失效时间
        jedis.setex("code", 600, "验证码");

        try {
            TimeUnit.SECONDS.sleep(10);   //让程序等10秒钟
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //查询键值失效时间
        long expiredTime = jedis.ttl("code");
        System.out.println(expiredTime);

        //设置已存在键值的失效时间
        jedis.expire("code", 60);

        try {
            TimeUnit.SECONDS.sleep(10);   //让程序等10秒钟
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //查询键值失效时间
        expiredTime = jedis.ttl("code");
        System.out.println(expiredTime);
    }

    /**
     * 使用Jedis配置类 操作java对象
     **/
    @Test
    public void testPojo() {
        jedis.select(1);

        User user = new User();
        user.setId(2);
        user.setUsername("张三");
        user.setPassword("123456");
        byte[] byteUser = SerializeUtil.serialize(user);

        //设值
        jedis.set(SerializeUtil.serialize("user"), byteUser);

        //取值
        byte[] user1 = jedis.get(SerializeUtil.serialize("user"));
        User user2 = (User) SerializeUtil.unserialize(user1);
        System.out.println(user2);
    }

    /**
     * 使用Jedis配置类 使用事务
     **/
    @Test
    public void testTx() {
        Transaction tx = jedis.multi();
        tx.set("code", "1235rt3");

        //执行事务
        tx.exec();

        //回滚事务
        //tx.discard();
    }
}
