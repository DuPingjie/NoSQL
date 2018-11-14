package info2.Mongodb;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Projections;

public class MongoDBJDBC{
   public static void main( String args[] ){
        try{   
        	
              // Connect to MongoDB service
              MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
            
              // Connect to database
              MongoDatabase mongoDatabase = mongoClient.getDatabase("mydb");  
              System.out.println("Connect to database successfully");
              
              // Create collection
              // Model in MongoDb football plyers:
              mongoDatabase.createCollection("players");
              System.out.println("Collection players is created!");
              MongoCollection<Document> players = mongoDatabase.getCollection("players");
              // Model in MongoDb football teams:
              mongoDatabase.createCollection("teams");
              System.out.println("Collection players is created!");
              MongoCollection<Document> teams = mongoDatabase.getCollection("teams");
              // Model in MongoDb football matches:
              mongoDatabase.createCollection("matches");
              System.out.println("Collection players is created!");
              MongoCollection<Document> matches = mongoDatabase.getCollection("matches");
           
              // Optimization of querie (teams by name, players by name)
              players.createIndex(Indexes.ascending("last name"));
              teams.createIndex(Indexes.ascending("team name"));
              players.createIndex(Indexes.ascending("first name"));
              
             
              // Queries of insertion for players, teams and matches 
              // Insert 110 players
              Random rand = new Random();      
              List<Document> documents_players = new ArrayList<Document>();               
              List<String> posts = Arrays.asList("Goalkeeper", "Centre-back-1","Centre-back-2", "Lfet-back","Right-back","Centre-forward","Right-winger","Left-winger","Third-striker","Second-striker","First-striker");            
        	     int post_index=0;
              for(int i=0;i<110;i++){
            	   Document document = new Document("last name", "nom_"+String.valueOf(i+1)).  
               	       append("first name", "prenom_"+String.valueOf(i+1)).  
               	       append("birthday", String.valueOf(rand.nextInt(30)+1973)+"-"+String.format("%02d",rand.nextInt(12)+1)+"-"+String.format("%02d",rand.nextInt(30)+1)).  
                 	   append("size", String.valueOf(rand.nextInt(70)+150)).
                 	   append("weight", String.valueOf(rand.nextInt(60)+40)).
                 	   append("post", posts.get(post_index));  
            	   if(post_index==10)
            	  	    post_index=0;
            	   else
            		    post_index++;
            	   documents_players.add(document);
               }            	   
                  players.insertMany(documents_players);  
                  System.out.println("Players are inserted successfully!");  
                  
              // Print players
              FindIterable<Document> findIterable = players.find();  
     	        MongoCursor<Document> mongoCursor = findIterable.iterator();  
              System.out.println("==========================================");
              while(mongoCursor.hasNext()){  
           	           System.out.println(mongoCursor.next());  
              }   
           	      
       	     // Insert 10 teams
              List<Document> documents_teams = new ArrayList<Document>();   
              List<String> colors=Arrays.asList("red","blue","white","green","yellow","black","white-black","white-blue","yellow-black","red-black");
        	 
              for(int i=0;i<10;i++){
            	   BasicDBList conList=new BasicDBList();
            	   for(int j=0;j<11;j++){
            		    conList.add(new BasicDBObject("last name","nom_"+String.valueOf(i*11+j)));
            	   }
            		    Document document = new Document("team name", "team_"+String.valueOf(i+1)).           	  
               	    append("color", colors.get(i)).  
                      append("team players", players.find(new BasicDBObject("$or",conList)).projection(Projections.include("_id")));  
            	   documents_teams.add(document);
               }      
              teams.insertMany(documents_teams);  
              System.out.println("Teams are inserted successfully!");  
              
              // Print teams
              FindIterable<Document> findIterable1 = teams.find();
     	        MongoCursor<Document> mongoCursor1 = findIterable1.iterator();  
              System.out.println("==========================================");
       	     while(mongoCursor1.hasNext()){  
                  System.out.println(mongoCursor1.next());  
              }   
       	      
       	     // Insert 5 matches
              List<Document> documents_matches = new ArrayList<Document>();   
              for(int i=0;i<5;i++){
            	  Document homeplayersscore=new Document();
            	  Document extplayersscore=new Document();
            	  List<Document> homeplayers=(List<Document>) teams.find(new BasicDBObject("team name","team_"+String.valueOf(2*i+1))).projection(Projections.include("team players")).first().get("team players");
            	  List<Document> extplayers=(List<Document>)  teams.find(new BasicDBObject("team name","team_"+String.valueOf(2*i+2))).projection(Projections.include("team players")).first().get("team players");
            	  for(int j=0;j<homeplayers.size()-1;j++){
            		  homeplayersscore.append(homeplayers.get(j).toString(), "0");
            		  extplayersscore.append(extplayers.get(j).toString(), "0");
            	  }
            	  homeplayersscore.append(homeplayers.get(homeplayers.size()-1).toString(), "2");
            	  extplayersscore.append(extplayers.get(homeplayers.size()-1).toString(), "3");
            	  
            	  Document document = new Document("hometeam", teams.find(new BasicDBObject("team name","team_"+String.valueOf(2*i+1))).projection(Projections.include("_id")).first()).  
               	       append("extteam", teams.find(new BasicDBObject("team name","team_"+String.valueOf(2*i+2))).projection(Projections.include("_id")).first()).  
               	       append("competition", "World Cup").  
                 	   append("homescore", "2").
                 	   append("extscore", "3").
                 	   append("homeplayersscore:", homeplayersscore).
                 	   append("extplayersscore", extplayersscore);  
            	  documents_matches.add(document);
                 }            	   
               	matches.insertMany(documents_matches);  
                  System.out.println("Mathes are inserted successfully!");  
                  
                  // Print Matches
                  FindIterable<Document> findIterable2 = matches.find();  
           	      MongoCursor<Document> mongoCursor2 = findIterable2.iterator();  
                  System.out.println("==========================================");
           	      while(mongoCursor2.hasNext()){  
           	           System.out.println(mongoCursor2.next());  
                  }   
           
              // Queries of selecting the players for a post (Right-back) and a maximum age (25 years old) 
           	  int age_max=25;
              String post="Right-back";
              Calendar now = Calendar.getInstance();             
       	     String birthday_earliest=(now.get(Calendar.YEAR)-age_max)+"-"+(now.get(Calendar.MONTH)+1)+"-"+now.get(Calendar.DAY_OF_MONTH);
 
              // Print the results
              FindIterable<Document> result1=players.find(Filters.and(Filters.eq("post", post),Filters.gte("birthday",birthday_earliest)));
              MongoCursor<Document> result1C = result1.iterator();  
              System.out.println("==========================================");
              while(result1C.hasNext()){  
       	             System.out.println(result1C.next());  
              } 
           
           // Delete the database
           mongoDatabase.drop(); 
           }catch(Exception e){
             System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          }
   }
}
