package info2.Mongodb;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.mongodb.client.model.MapReduceAction;
import com.mongodb.client.model.Projections;

public class MongoDBJDBC {
	public static void main(String args[]) {
		try {

			// Connect to MongoDB
			MongoClient mongoClient = new MongoClient("localhost", 27017);

			// Connect to database
			MongoDatabase mongoDatabase = mongoClient.getDatabase("mydb");
			System.out.println("Connect to database successfully");

			// Create collections
			mongoDatabase.createCollection("players");
			System.out.println("Collection players is created!");
			mongoDatabase.createCollection("teams");
			System.out.println("Collection players is created!");
			mongoDatabase.createCollection("matches");
			System.out.println("Collection players is created!");

			MongoCollection<Document> players = mongoDatabase.getCollection("players");
			MongoCollection<Document> teams = mongoDatabase.getCollection("teams");
			MongoCollection<Document> matches = mongoDatabase.getCollection("matches");

			players.createIndex(Indexes.ascending("last name"));
			teams.createIndex(Indexes.ascending("team name"));
			players.createIndex(Indexes.ascending("name"));
			matches.createIndex(Indexes.ascending("homeplayersscore"));

			matches.createIndex(Indexes.ascending("extplayersscore"));

			// Insert 110 players
			List<Document> documents_players = new ArrayList<Document>();
			for (int i = 0; i < 110; i++) {
				Document document = new Document("last name", "nom_" + String.valueOf(i + 1))
						.append("first name", "prenom_" + String.valueOf(i)).append("birthday", "2005-10-18")
						.append("size", String.valueOf(170 + i / 10.0)).append("weight", String.valueOf(70 + i / 10.0))
						.append("post", "right");
				documents_players.add(document);
			}
			players.insertMany(documents_players);
			System.out.println("Players are inserted successfully!");

			// Print players
			FindIterable<Document> findIterable = players.find();
			MongoCursor<Document> mongoCursor = findIterable.iterator();
			while (mongoCursor.hasNext()) {
				System.out.println(mongoCursor.next());
			}

			// Insert 10 teams
			List<Document> documents_teams = new ArrayList<Document>();
			List<String> colors = new ArrayList<String>();
			colors.add("red");
			colors.add("blue");
			colors.add("yellow");
			colors.add("green");
			colors.add("white");

			for (int i = 0; i < 10; i++) {
				BasicDBList conList = new BasicDBList();
				for (int j = 0; j < 11; j++) {
					conList.add(new BasicDBObject("last name", "nom_" + String.valueOf(i * 11 + j)));
				}
				Document document = new Document("team name", "team_" + String.valueOf(i + 1))
						.append("color", colors.get(i / 2)).append("team players",
								players.find(new BasicDBObject("$or", conList)).projection(Projections.include("_id")));
				documents_teams.add(document);
			}
			teams.insertMany(documents_teams);
			System.out.println("Teams are inserted successfully!");

			// Print documents
			FindIterable<Document> findIterable1 = teams.find();
			MongoCursor<Document> mongoCursor1 = findIterable1.iterator();
			while (mongoCursor1.hasNext()) {
				System.out.println(mongoCursor1.next());
			}

			// Insert 5 matches
			List<Document> documents_matches = new ArrayList<Document>();
			for (int i = 0; i < 5; i++) {
				List<Document> homeplayersscore = new ArrayList<Document>();
				List<Document> extplayersscore = new ArrayList<Document>();
				List<Document> homeplayers = (List<Document>) teams
						.find(new BasicDBObject("team name", "team_" + String.valueOf(2 * i + 1)))
						.projection(Projections.include("team players")).first().get("team players");
				List<Document> extplayers = (List<Document>) teams
						.find(new BasicDBObject("team name", "team_" + String.valueOf(2 * i + 2)))
						.projection(Projections.include("team players")).first().get("team players");

				for (int j = 0; j < homeplayers.size() - 1; j++) {
					homeplayersscore.add(new Document("player_id", homeplayers.get(j).get("_id")).append("note", 0));
					extplayersscore.add(new Document("player_id", extplayers.get(j).get("_id")).append("note", 0));
				}

				homeplayersscore.add(new Document("player_id", homeplayers.get(homeplayers.size() - 1).get("_id"))
						.append("note", 2));
				extplayersscore.add(
						new Document("player_id", extplayers.get(extplayers.size() - 1).get("_id")).append("note", 3));
				System.out.println(extplayers.get(homeplayers.size() - 1).toString());

				Document document = new Document("hometeam",
						teams.find(new BasicDBObject("team name", "team_" + String.valueOf(2 * i + 1)))
								.projection(Projections.include("_id")).first()).append(
										"extteam",
										teams.find(new BasicDBObject("team name", "team_" + String.valueOf(2 * i + 2)))
												.projection(Projections.include("_id")).first())
										.append("competition", "World Cup").append("homescore", 2).append("extscore", 3)
										.append("homeplayersscore", homeplayersscore)
										.append("extplayersscore", extplayersscore);
				documents_matches.add(document);
			}
			matches.insertMany(documents_matches);
			System.out.println("Mathes are inserted successfully!");

			// Print Matches
			FindIterable<Document> findIterable2 = matches.find();
			MongoCursor<Document> mongoCursor2 = findIterable2.iterator();
			while (mongoCursor2.hasNext()) {
				System.out.println(mongoCursor2.next());
			}

			// IV. Queries of selecting the players for a post (Right-back) and a maximum
			// age (25 years old)
			int age_max = 25;
			String post = "Right-back";
			Calendar now = Calendar.getInstance();
			String birthday_earliest = (now.get(Calendar.YEAR) - age_max) + "-" + (now.get(Calendar.MONTH) + 1) + "-"
					+ now.get(Calendar.DAY_OF_MONTH);

			// Print the results
			FindIterable<Document> result1 = players
					.find(Filters.and(Filters.eq("post", post), Filters.gte("birthday", birthday_earliest)));
			MongoCursor<Document> result1C = result1.iterator();
			System.out.println("==========================================");
			while (result1C.hasNext()) {
				System.out.println(result1C.next());
			}

			// request new collections
			FindIterable<Document> homeplayersIdNote = matches.find()
					.projection(new Document("homeplayersscore", true).append("_id", false));
			FindIterable<Document> extplayersIdNote = matches.find()
					.projection(new Document("extplayersscore", true).append("_id", false));
			mongoDatabase.createCollection("newPlayers");//a temporary collection to keep "player_id" with "note"
			MongoCollection<Document> newPlayers = mongoDatabase.getCollection("newPlayers");

			//get the matches of (player_id,note) in the home game
			MongoCursor<Document> playersCursor = homeplayersIdNote.iterator();
			while (playersCursor.hasNext()) {
				ArrayList<Document> versi = (ArrayList<Document>) playersCursor.next().get("homeplayersscore");
				for (Document embedded : versi) {
					newPlayers.insertOne(new Document("player_id", embedded.get("player_id")).append("numMatches", 1)
							.append("note", embedded.get("note")));
				}
			}
			//get the matches of (player_id,note) in the away game
			playersCursor = extplayersIdNote.iterator();
			while (playersCursor.hasNext()) {
				ArrayList<Document> versi = (ArrayList<Document>) playersCursor.next().get("extplayersscore");
				for (Document embedded : versi) {
					newPlayers.insertOne(new Document("player_id", embedded.get("player_id")).append("numMatches", 1)
							.append("note", embedded.get("note")));
				}
			}
			//use the mapReduce to get the collection of (key:player_id,value:meanNote)
			//result=-1 for the elements to be removed
			String mapfun = "function(){emit(this.player_id,{note:this.note,numMatches:this.numMatches});}";
			String reducefun = "function(key, values){" + "var sumN=0;" + "var elemDeleted=-1;"
					+ "for(var i = 0;i<values.length;i++){" + "sumN+=values[i].note;" + "}" + "var sumM=0;"
					+ "for(var i = 0;i<values.length;i++){" + "sumM+=values[i].numMatches" + "}"
					+ "var meanNote=sumN/sumM;" + "values.note=sumN;" + "values.numMatches=sumM;" + "return values"
					+ "}";
			String finalizefun = "function(key, reduceValues) {" + "if(reduceValues.note >= 2){"
					+ "reduceValues.result=reduceValues.note/reduceValues.numMatches;" + "}else{"
					+ "reduceValues.result = -1;}" + "return reduceValues.result;" + "}";

			newPlayers.mapReduce(mapfun, reducefun).finalizeFunction(finalizefun).collectionName("resultPlayers")
					.action(MapReduceAction.REPLACE).iterator().close();
			MongoCollection<Document> resultPlayers = mongoDatabase.getCollection("resultPlayers");
			//remove all the wrong elements
			resultPlayers.deleteMany(Filters.eq("value", -1));

			 mongoDatabase.drop();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}

	}
}
