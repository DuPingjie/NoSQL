package test.Redis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.SortingParams;

public class RedisClient {
	private Jedis jedis;
    private JedisPool jedisPool;
    private ShardedJedis shardedJedis;
    private ShardedJedisPool shardedJedisPool;
    
    public RedisClient() 
    { 
        initialPool(); 
        initialShardedPool(); 
        shardedJedis = shardedJedisPool.getResource(); 
        jedis = jedisPool.getResource(); 
        
        
    } 
 
   
    private void initialPool() 
    { 
        // Pool Configuration
        JedisPoolConfig config = new JedisPoolConfig(); 
        config.setMaxTotal(20); 
        config.setMaxIdle(5); 
        config.setMaxWaitMillis(1000l); 
        config.setTestOnBorrow(false); 
        
        jedisPool = new JedisPool(config,"127.0.0.1",6379);
    }
    

    private void initialShardedPool() 
    { 
       
        JedisPoolConfig config = new JedisPoolConfig(); 
        config.setMaxTotal(20); 
        config.setMaxIdle(5); 
        config.setMaxWaitMillis(1000l); 
        config.setTestOnBorrow(false); 
       
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(); 
        shards.add(new JedisShardInfo("127.0.0.1", 6379, "master")); 

        
        shardedJedisPool = new ShardedJedisPool(config, shards); 
    } 

    void callCenterModel() {
    	System.out.println("======================Calls==========================");
        // Clear all date
        jedis.flushDB(); 
        // Add calls
        shardedJedis.hset("call1", "id", "1"); 
        shardedJedis.hset("call1", "hour", "15:15"); 
        shardedJedis.hset("call1", "number", "0666666668"); 
        shardedJedis.hset("call1", "status", "finished"); 
        shardedJedis.hset("call1", "duration", "100s"); 
        shardedJedis.hset("call1", "text", "i want eating."); 
        shardedJedis.hset("call1", "id_op", "2"); 
        
        shardedJedis.hset("call2", "id", "2"); 
        shardedJedis.hset("call2", "hour", "18:55"); 
        shardedJedis.hset("call2", "number", "0666556668"); 
        shardedJedis.hset("call2", "status", "treating"); 
        shardedJedis.hset("call2", "duration", "250s"); 
        shardedJedis.hset("call2", "text", "i'd like to meet Mr.Wang."); 
        shardedJedis.hset("call2", "id_op", "2"); 
        
        shardedJedis.hset("call3", "id", "3"); 
        shardedJedis.hset("call3", "hour", "21:01"); 
        shardedJedis.hset("call3", "number", "0666559968"); 
        shardedJedis.hset("call3", "status", "no affected"); 
        shardedJedis.hset("call3", "duration", "110s"); 
        shardedJedis.hset("call3", "text", "How are you?"); 
        
        //Add operators
        shardedJedis.hset("op1", "id", "1"); 
        shardedJedis.hset("op1", "name", "Jack"); 
        
        shardedJedis.hset("op2", "id", "2"); 
        shardedJedis.hset("op2", "name", "Rose");
        
        shardedJedis.hset("op3", "id", "3"); 
        shardedJedis.hset("op3", "name", "Jane"); 
        
        System.out.println("call1："+shardedJedis.hvals("call1"));
        System.out.println("call2："+shardedJedis.hvals("call2"));
        System.out.println("call3："+shardedJedis.hvals("call3"));
        
        System.out.println("======================Operators==========================");
  
        System.out.println("op1："+shardedJedis.hvals("op1"));
        System.out.println("op2："+shardedJedis.hvals("op2"));
        System.out.println("op3："+shardedJedis.hvals("op3"));
        System.out.println();
        
        //Affect a new call
        System.out.println("======================Affecte a call==========================");
        shardedJedis.hset("call3", "status", "treating");
        shardedJedis.hset("call3", "id_op", "1");
        System.out.println("call3："+shardedJedis.hvals("call3"));
        
        // Print all the calls not affected
        System.out.println("======================Calls not affected==========================");
        for (int i=1;i<4;i++) {
        	String call="call"+String.valueOf(i);
        	String status=shardedJedis.hget(call,"status");

        	if (status.equals("no affected")) {
        		System.out.println(call+shardedJedis.hvals(call));
        	}
        }
        
      
              
    
     }

}
