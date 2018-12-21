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
	
    // Configuration of the connection
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

    // Realize a call center
    void callCenterModel() {
    	
        // Clear all date
        jedis.flushDB(); 
        
        // 1. Add calls
        int n=0;
        
        shardedJedis.hset("call1", "id", "1"); 
        shardedJedis.hset("call1", "hour", "15:15"); 
        shardedJedis.hset("call1", "number", "0666666668"); 
        shardedJedis.hset("call1", "status", "finished"); 
        shardedJedis.hset("call1", "duration", "100s"); 
        shardedJedis.hset("call1", "text", "i want eating."); 
        shardedJedis.hset("call1", "id_op", "2"); 
        n++;
        
        shardedJedis.hset("call2", "id", "2"); 
        shardedJedis.hset("call2", "hour", "18:55"); 
        shardedJedis.hset("call2", "number", "0666556668"); 
        shardedJedis.hset("call2", "status", "treating"); 
        shardedJedis.hset("call2", "duration", "250s"); 
        shardedJedis.hset("call2", "text", "i'd like to meet Mr.Wang."); 
        shardedJedis.hset("call2", "id_op", "2"); 
        n++;
        
        shardedJedis.hset("call3", "id", "3"); 
        shardedJedis.hset("call3", "hour", "21:01"); 
        shardedJedis.hset("call3", "number", "0666559968"); 
        shardedJedis.hset("call3", "status", "no affected"); 
        shardedJedis.hset("call3", "duration", "110s"); 
        shardedJedis.hset("call3", "text", "How are you?"); 
        n++;
        
        shardedJedis.hset("call4", "id", "4"); 
        shardedJedis.hset("call4", "hour", "23:51"); 
        shardedJedis.hset("call4", "number", "0612345968"); 
        shardedJedis.hset("call4", "status", "no affected"); 
        shardedJedis.hset("call4", "duration", "156s"); 
        shardedJedis.hset("call4", "text", "Can we work together?"); 
        n++;
        
        shardedJedis.hset("call5", "id", "5"); 
        shardedJedis.hset("call5", "hour", "13:51"); 
        shardedJedis.hset("call5", "number", "0612345678"); 
        shardedJedis.hset("call5", "status", "no answered"); 
        shardedJedis.hset("call5", "duration", "20s"); 
        n++;
        
        // 2. Add operators
        int nop=0;
        shardedJedis.hset("op1", "id", "1"); 
        shardedJedis.hset("op1", "name", "Jack"); 
        shardedJedis.hset("op1", "last name", "LOUIS"); 
        nop++;
        
        shardedJedis.hset("op2", "id", "2"); 
        shardedJedis.hset("op2", "name", "Rose");
        shardedJedis.hset("op2", "last name", "JEAN"); 
        nop++;
        
        shardedJedis.hset("op3", "id", "3"); 
        shardedJedis.hset("op3", "name", "Micheal");
        shardedJedis.hset("op3", "last name", "OLIVIER "); 
        nop++;
        System.out.println("===========================Calls==============================");
        
        System.out.println("call1："+shardedJedis.hvals("call1"));
        System.out.println("call2："+shardedJedis.hvals("call2"));
        System.out.println("call3："+shardedJedis.hvals("call3"));
        System.out.println("call4："+shardedJedis.hvals("call4"));
        System.out.println("call5："+shardedJedis.hvals("call5"));
        System.out.println();
        
        System.out.println("=========================Operators============================");
  
        System.out.println("op1："+shardedJedis.hvals("op1"));
        System.out.println("op2："+shardedJedis.hvals("op2"));
        System.out.println("op3："+shardedJedis.hvals("op3"));
        System.out.println();
        
        // 3. Print all the calls not affected and the calls being treated
        System.out.println("======================Calls not affected======================");
        for (int i=1;i<=n;i++) {
        	String call="call"+String.valueOf(i);
        	String status=shardedJedis.hget(call,"status");

        	if (status.equals("no affected")) {
        		System.out.println(call+shardedJedis.hvals(call));
        	}
        }
        System.out.println();
        System.out.println("======================Calls being treated=====================");
        for (int i=1;i<=n;i++) {
        	String call="call"+String.valueOf(i);
        	String status=shardedJedis.hget(call,"status");

        	if (status.equals("treating")) {
        		System.out.println(call+shardedJedis.hvals(call));
        	}
        }
        System.out.println();
        
        // 4. Affect a new call
        System.out.println("======================Affecte a call==========================");
        shardedJedis.hset("call3", "status", "treating");
        shardedJedis.hset("call3", "id_op", "1");
        System.out.println("call3："+shardedJedis.hvals("call3"));
        System.out.println();
               
        // 5. Print all the calls being treated by each operator 
        System.out.println("=========Calls being treated and its operator's name==========");
        for (int i=1;i<=n;i++) {
        	String call="call"+String.valueOf(i);
        	String status=shardedJedis.hget(call,"status");

        	if (status.equals("treating")) {
        		String id_op=shardedJedis.hget(call, "id_op");
        		for(int j=1;j<=nop;j++) {
        			String op="op"+String.valueOf(j);
        			String id=shardedJedis.hget(op,"id");
        			if(id_op.equals(id)) {
        				System.out.println(call+shardedJedis.hvals(call)+" operator name : "+shardedJedis.hget(op,"name"));
        			}
        			
        		}
        	}   
        }	
    }

 }
