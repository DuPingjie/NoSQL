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
         
         //Insert players
              List<Document> documents_players = new ArrayList<Document>();   
              for(int i=0;i<110;i++){
            	  Document document = new Document("name", "nom_"+String.valueOf(i+1)).  
               	       append("first name", "prenom_"+String.valueOf(i)).  
               	       append("birthday", "2005-10-18").  
                 	   append("size", "19"+String.valueOf(i/10.0)).
                 	   append("weight", "8"+String.valueOf(i/10.0)).
                 	   append("post", "right");  
            	  documents_players.add(document);
              }            	   
               	  players.insertMany(documents_players);  
                  System.out.println("文档插入成功");  
               
               //print documents
       	      FindIterable<Document> findIterable = players.find();  
       	      MongoCursor<Document> mongoCursor = findIterable.iterator();  
       	      while(mongoCursor.hasNext()){  
       	           System.out.println(mongoCursor.next());  
              }    
         
         mongoDatabase.drop();
         
      }catch(Exception e){
        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
     }
   }
}
