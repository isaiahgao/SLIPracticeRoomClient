package jhunions.isaiahgao.client;

import java.io.File;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class AuthHandler {
	
	public AuthHandler() {
		if (!test("auth")) {
			auth = "";
		}
	}
	
	private boolean test(String filename) {
		try {
			File file = new File(filename);
			if (file.exists()) {
				Scanner sc = new Scanner(file);
				String str = sc.nextLine();
				auth = str;
				sc.close();
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	private String auth;
	
	public String getAuthJson() {
		JsonNode node = Main.getJson().createObjectNode().set("key", JsonNodeFactory.instance.textNode(auth));
		String str = node.toString();
		return str.substring(1, str.length() - 1);
	}

}
