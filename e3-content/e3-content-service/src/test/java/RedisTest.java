import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.e3.redisUtils.JedisClient;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class RedisTest {
	
	/**
	 * 连接单机版
	 */
	@Test
	public void testJedis(){
		//1. 创建一个Jedis对象，需要指定服务端的ip和端口
		//2. 使用Jedis对象操作数据库，每个redis命令对应一个方法
		//3. 打印结果
		//4. 关闭jedis
		Jedis jedis = new Jedis("192.168.25.128", 6379);
		String result = jedis.get("hello");
		System.out.println(result);
		jedis.close();
		
	}
	
	/**
	 * 连接单机版使用连接池
	 */
	@Test
	public void testJedisPool(){
		//1. 创建JedisPool对象，需要指定服务器的ip和端口
		JedisPool pool = new JedisPool("192.168.25.128", 6379);
		//2. 从JedisPool中获得Jedis对象
		Jedis jedis = pool.getResource();
		//3. 使用Jedis操作redis服务器
		jedis.set("jedis", "set");
		String result = jedis.get("jedis");
		System.out.println(result);
		//4. 操作完毕后关闭Jedis对象，连接池回收资源
		jedis.close();
		//5. 关闭JedisPool对象
		pool.close();
	}
	
	/**
	 * 连接集群版
	 */
	@Test
	public void testJedisCluster(){
		/*第一步：使用JedisCluster对象。需要一个Set<HostAndPort>参数。Redis节点的列表。
		第二步：直接使用JedisCluster对象操作redis。在系统中单例存在。
		第三步：打印结果
		第四步：系统关闭前，关闭JedisCluster对象。*/
		Set<HostAndPort> nodes = new HashSet<>();
		nodes.add(new HostAndPort("192.168.25.128", 7001));
		nodes.add(new HostAndPort("192.168.25.128", 7002));
		nodes.add(new HostAndPort("192.168.25.128", 7003));
		nodes.add(new HostAndPort("192.168.25.128", 7004));
		nodes.add(new HostAndPort("192.168.25.128", 7005));
		nodes.add(new HostAndPort("192.168.25.128", 7006));
		JedisCluster cluster = new JedisCluster(nodes);
		cluster.set("cluster", "100");
		String result = cluster.get("cluster");
		System.out.println(result);
		cluster.close();
		
		
	}
	
	@Test
	public void teseJedisClient(){
		//1.初始化容器，
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		//2.从容器中获取jedisclient对象
		JedisClient jedisClient =applicationContext.getBean(JedisClient.class);
		jedisClient.set("first", "cluster");
		String result =jedisClient.get("first");
		System.out.println(result);
		
	}
}
