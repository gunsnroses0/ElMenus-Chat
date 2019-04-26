package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

import Commands.Command;

public class Chat {
	private static final String COLLECTION_NAME = "chats";

	private static MongoCollection<Document> collection = null;
	private static int DbPoolCount = 4;
	static String host = System.getenv("MONGO_URI");
	public static int getDbPoolCount() {
		return DbPoolCount;
	}
	public static void setDbPoolCount(int dbPoolCount) {
		DbPoolCount = dbPoolCount;
	}
	
	public static HashMap<String, Object> create(HashMap<String, Object> atrributes) throws ParseException {
		MongoClientOptions.Builder options = MongoClientOptions.builder()
	            .connectionsPerHost(DbPoolCount);
		MongoClientURI uri = new MongoClientURI(
				host,options);
		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase database = mongoClient.getDatabase("El-Menus");
//    	Method method =   Class.forName("PlatesService").getMethod("getDB", null);
//    	MongoDatabase database = (MongoDatabase) method.invoke(null, null);
		
		// Retrieving a collection
		MongoCollection<Document> collection = database.getCollection("chats");
		Document newChat = new Document();

		for (String key : atrributes.keySet()) {
			newChat.append(key, atrributes.get(key));
		}
		collection.insertOne(newChat);

		JSONParser parser = new JSONParser();
		HashMap<String, Object> returnValue = Command.jsonToMap((JSONObject) parser.parse(newChat.toJson()));
		return returnValue;

	}
	
	public static HashMap<String, Object> update(String id, HashMap<String, Object> atrributes) {
		MongoClientOptions.Builder options = MongoClientOptions.builder()
	            .connectionsPerHost(DbPoolCount);
		MongoClientURI uri = new MongoClientURI(
				host,options);
		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase database = mongoClient.getDatabase("El-Menus");
//    	Method method =   Class.forName("PlatesService").getMethod("getDB", null);
//    	MongoDatabase database = (MongoDatabase) method.invoke(null, null);

		// Retrieving a collection
		MongoCollection<Document> collection = database.getCollection("chats");
		Document updatedChat = new Document();
		Bson filter = new Document("_id", new ObjectId(id));
		System.out.println(filter.toString());
		for (String key : atrributes.keySet()) {
			updatedChat.append(key, atrributes.get(key));
		}

		Bson updateOperationDocument = new Document("$set", updatedChat);
		collection.updateMany(filter, updateOperationDocument);

		return atrributes;
	}
	
	
	
	public static HashMap<String, Object> get(String messageId) {
		MongoClientOptions.Builder options = MongoClientOptions.builder()
	            .connectionsPerHost(DbPoolCount);
		MongoClientURI uri = new MongoClientURI(
				host,options);
		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase database = mongoClient.getDatabase("El-Menus");
//    	Method method =   Class.forName("PlatesService").getMethod("getDB", null);
//    	MongoDatabase database = (MongoDatabase) method.invoke(null, null);

		// Retrieving a collection
		MongoCollection<Document> collection = database.getCollection("chats");
		System.out.println("Inside Get");
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(messageId));
		System.out.println(query.toString());
		HashMap<String, Object> message = null;
		Document doc = collection.find(query).first();
		JSONParser parser = new JSONParser(); 
		try {
			JSONObject json = (JSONObject) parser.parse(doc.toJson());
		
			message = Command.jsonToMap(json);
			
			System.out.println(message.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return message;
	}
	
	public static HashMap<String, Object> getAll(String roomId) {
		MongoClientOptions.Builder options = MongoClientOptions.builder()
	            .connectionsPerHost(DbPoolCount);
		MongoClientURI uri = new MongoClientURI(
				host,options);
		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase database = mongoClient.getDatabase("El-Menus");
//    	Method method =   Class.forName("PlatesService").getMethod("getDB", null);
//    	MongoDatabase database = (MongoDatabase) method.invoke(null, null);

		// Retrieving a collection
		MongoCollection<Document> collection = database.getCollection("chats");
		System.out.println("Inside Get");
		BasicDBObject query = new BasicDBObject();
		query.put("targetID", roomId);
		System.out.println(query.toString());
		HashMap<String, Object> message = null;
		JSONArray chats = new JSONArray();
		FindIterable<Document> result = collection.find(query);
		
		result.forEach(new Block<Document>() {
	        @Override
	        public void apply(final Document document) {
	            JSONObject jsonChat = new JSONObject();
	            jsonChat.put("id", document.getObjectId("_id").toString());
	            jsonChat.put("text", document.get("text"));
	            jsonChat.put("isAnon", document.get("isAnon"));
	            jsonChat.put("attachments", document.get("attachments"));
	            
	            HashMap<String, Object> jsonChatHash = Command.jsonToMap(jsonChat);
//	            jsonReport.put("comment", document.getString("comment"));
//	            jsonReport.put("city_item_id", document.getString("city_item_id"));
//	            jsonReport.put("priority", document.getInteger("priority"));
//	            jsonReport.put("resolved", document.getBoolean("resolved"));
//	            jsonReport.put("report_date", document.getLong("report_date"));
	            chats.add(jsonChatHash);
	        }
	    });
	    
	    JSONObject jsonResults = new JSONObject();
	    jsonResults.put("chats", chats);
	    
	    message = Command.jsonToMap(jsonResults);
	    
	    return message;
	}

}
