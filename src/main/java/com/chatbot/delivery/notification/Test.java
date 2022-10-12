package com.chatbot.delivery.notification;

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONObject;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String response = "[{\"id\":\"a453c8e6-73e6-41a5-9ed6-978e7a7985ad\",\"language\":\"en\",\"dialogId\":\"welcome\",\"dialogName\":\"Welcome\",\"dialogTexts\":[{\"dialogSeqNumber\":1,\"dialogText\":\"Hi {1}, Your package with Tracking Number: {2} is on its way. Need to make changes to your delivery ? Estimated Delivery: \"},{\"dialogSeqNumber\":2,\"dialogText\":\"Manage Delivery\"}]}]";
		
		WelcomeMessage welcomeMessage = new WelcomeMessage();
		JSONArray arr = new JSONArray(response);  
		try {
			JSONObject dialog = arr.getJSONObject(0);
			JSONArray dialogTexts = (JSONArray)dialog.getJSONArray("dialogTexts");
			String message = (String) dialogTexts.getJSONObject(0).get("dialogText");
			String buttonText = (String) dialogTexts.getJSONObject(1).get("dialogText");
			welcomeMessage.setWelcomeMessage(message);
			welcomeMessage.setButtonText(buttonText);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		String text1 = welcomeMessage.getWelcomeMessage().replace("{1}", "Saroj");
		text1 = text1.replace("{2}", "1141414");
		
		System.out.println(text1);

	}

}
