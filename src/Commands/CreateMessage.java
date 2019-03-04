package Commands;

import Model.Chat;
import Model.Participant;
import Model.Room;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;

public class CreateMessage extends Command{
	@Override
	protected void execute() {
		HashMap<String, Object> props = parameters;
		Channel channel = (Channel) props.get("channel");
		JSONParser parser = new JSONParser();
		System.out.println("rest_id");
		//System.out.println(props.get("id"));
		
		try {
//			System.out.println("rest_id");
//			System.out.println(props.get("id"));
//			JSONObject messageBody = (JSONObject) parser.parse((String) props.get("body"));
//			HashMap<String, Object> requestBodyHash = jsonToMap((JSONObject) messageBody.get("body"));
//			AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
//			AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");
//			Envelope envelope = (Envelope) props.get("envelope");
//			HashMap<String, Object> createdMessage = Question.create(requestBodyHash);
//			JSONObject response = jsonFromMap(createdMessage);
			JSONObject body = (JSONObject) parser.parse((String) props.get("body"));
			String url = body.get("uri").toString();
			url = url.substring(1);
			String[] parametersArray = url.split("/");
			
			
			
			if(parametersArray.length == 1) {
				
				HashMap<String, Object> requestBodyHash = jsonToMap((JSONObject) body.get("body"));
				Object roomType = requestBodyHash.get("type");
				
				HashMap<String, Object> roomHash = new HashMap();
				roomHash.put("type", roomType);
				AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
				AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");
				Envelope envelope = (Envelope) props.get("envelope");
				HashMap<String, Object> createdRoom = Room.create(roomHash);
				
//				System.out.print(createdRoom.get("roomID"));
				
				
				
				
//				Object obj = requestBodyHash.get("users");
//				System.out.println(obj.indexOf(0));
				
				JSONObject obj = (JSONObject) body.get("body");
				JSONArray slideContent = (JSONArray) obj.get("users");
		        Iterator i = slideContent.iterator();
		        
		        while (i.hasNext()) {
		        	
		        	
		        	HashMap<String, Object> participantHash = new HashMap();
		        	participantHash.put("user_id", i.next());
		        	participantHash.put("room_id", createdRoom.get("roomID"));
		        	HashMap<String, Object> createdParticipant = Participant.create(participantHash);
		            
		        }
				
		        createdRoom.remove("roomID");
		        JSONObject response = jsonFromMap(createdRoom);
		        channel.basicPublish("", properties.getReplyTo(), replyProps, response.toString().getBytes("UTF-8"));
				
				
				
			}else {
				
				HashMap<String, Object> requestBodyHash = jsonToMap((JSONObject) body.get("body"));
				requestBodyHash.put("targetID", parametersArray[1]);
				AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
				AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");
				Envelope envelope = (Envelope) props.get("envelope");
				HashMap<String, Object> createdMessage = Chat.create(requestBodyHash);
				JSONObject response = jsonFromMap(createdMessage);
				channel.basicPublish("", properties.getReplyTo(), replyProps, response.toString().getBytes("UTF-8"));
				
				
		}
			
			
			
			
			
//			HashMap<String, Object> requestBodyHash = jsonToMap((JSONObject) body.get("body"));
//			requestBodyHash.put("targetID", targetID);
//			AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
//			AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");
//			Envelope envelope = (Envelope) props.get("envelope");
//			HashMap<String, Object> createdMessage = Chat.create(requestBodyHash);
//			JSONObject response = jsonFromMap(createdMessage);
			

			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
