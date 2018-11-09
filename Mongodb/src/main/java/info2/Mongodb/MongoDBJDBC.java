package info2.Mongodb;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;

public class MongoDBJDBC{
   public static void main( String args[] ){
      try{   
       // 连接到 mongodb 服务
         MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
       
         // 连接到数据库
         MongoDatabase mongoDatabase = mongoClient.getDatabase("mydb");  
         System.out.println("Connect to database successfully");
         // create collections
     /*    mongoDatabase.createCollection("players");
         System.out.println("集合players创建成功");
         mongoDatabase.createCollection("teams");
         System.out.println("集合teams创建成功");
         mongoDatabase.createCollection("matches");
         System.out.println("集合matches创建成功");
        */ 
         MongoCollection<Document> players = mongoDatabase.getCollection("players");
         MongoCollection<Document> teams = mongoDatabase.getCollection("teams");
         MongoCollection<Document> matches = mongoDatabase.getCollection("matches");
         
         players.createIndex(Indexes.ascending("name"));
         teams.createIndex(Indexes.ascending("name"));
         players.createIndex(Indexes.ascending("name"));
         mongoDatabase.drop();
      }catch(Exception e){
        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
     }
   }
}
