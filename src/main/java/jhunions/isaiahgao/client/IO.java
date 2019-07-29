package jhunions.isaiahgao.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jhunions.isaiahgao.common.ScanResult;
import jhunions.isaiahgao.common.User;

public class IO {
	
	private static final String HOST = "http://localhost:7000";
	private static final String USER_AGENT = "Mozilla/5.0";
	private static final ExecutorService EXE = Executors.newSingleThreadExecutor();
	
	public static void main(String[] args) throws Exception {
		File file = new File("admin");
		Scanner sc = new Scanner(file);
		String str = sc.nextLine();
		sc.close();
		
		JsonNode node = Main.getJson().createObjectNode().set("key", JsonNodeFactory.instance.textNode(str));
		sendRequest(RequestType.PUT, "/rooms", node.toString());
	}
	
	public enum RequestType {
		POST,
		PUT
	}
	
	public static String sendRequest(RequestType type, String urlpath) throws Exception {
		return sendRequest(type, urlpath, null);
	}
	
	public static String sendRequest(RequestType type, String urlpath, String body) throws Exception {
		URL obj = new URL(HOST + urlpath);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod(type.toString());

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Content-Type", "text/json");
		
		if (body != null) {
			// Indicate that we want to write to the HTTP request body
			con.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(
					con.getOutputStream());
			out.write(body);
			out.close();
		}
		 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending " + type.toString() + " request to URL : " + HOST);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		String result = response.toString();
		System.out.println(result);
		return result;
	}
	
	public static Future<ScanResult> scanId(String id, String room) {
		return EXE.submit(() -> {
			try {
				String result = sendRequest(RequestType.POST, "/rooms/" + room, "{\"cardnumber\":\"" + id + "\"," + Main.getAuthHandler().getAuthJson() + "}");
				return ScanResult.fromJson(result);
			} catch (Exception e) {
				return ScanResult.ERROR;
			}
		});
	}
	

	public static void synchronize() {
		// TODO Auto-generated method stub
		
	}

	public static void enableRoom(int room) {
		// TODO Auto-generated method stub
		
	}

	public static void disableRoom(int room, String reason) {
		// TODO Auto-generated method stub
		
	}

	public static Future<Boolean> push(User usd) {
		return EXE.submit(() -> {
			try {
				sendRequest(RequestType.POST, "/users", usd.toString());
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		});
	}

	public static User getUserData(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void removeUserData(String currentId) {
		// TODO Auto-generated method stub
		
	}

	public static void scan(User usd, int buttonPressed) {
		// TODO Auto-generated method stub
		
	}

}
