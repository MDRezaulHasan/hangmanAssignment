package se.hangman.client;

import java.io.IOException;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientLogin {

	private class ClientCredentials {
		String username;
		String password;
	}

	
	private ObjectMapper Obj = null;
	public String jwtKey = "";
	public String PlayerName = "";

	public ClientLogin() {
		Obj = new ObjectMapper();
		Obj.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

	}

	public String validateLogin(String username, String pswd) {

		ClientCredentials cred = new ClientCredentials();
		cred.username = username;
		cred.password = pswd;
		return objToJSONString(cred);
	}

	public void successLogin(String key) {
		this.jwtKey = key;
		
	}
	

	public ClientCredentials JSONStringToObject(String jsonString) {

		try {
			return Obj.readValue(jsonString, ClientCredentials.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String objToJSONString(ClientCredentials msg) {
		try {
			return Obj.writeValueAsString(msg);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
