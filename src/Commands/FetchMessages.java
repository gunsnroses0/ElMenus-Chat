package Commands;

import Model.Chat;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;

public class FetchMessages extends Command{
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
			System.out.println("infetch");
			JSONObject body = (JSONObject) parser.parse((String) props.get("body"));
			String url = body.get("uri").toString();
			url = url.substring(1);
			String[] parametersArray = url.split("/");
			String targetID = parametersArray[1];
			
			AMQP.BasicProperties properties = (AMQP.BasicProperties) props.get("properties");
			AMQP.BasicProperties replyProps = (AMQP.BasicProperties) props.get("replyProps");
			Envelope envelope = (Envelope) props.get("envelope");
			HashMap<String, Object> results = Chat.getAll(targetID);
			JSONObject response = jsonFromMap(results);
			
			


			channel.basicPublish("", properties.getReplyTo(), replyProps, response.toString().getBytes("UTF-8"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
