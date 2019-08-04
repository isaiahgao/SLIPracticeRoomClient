package jhunions.isaiahgao.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import jhunions.isaiahgao.common.ScanResultPacket;
import jhunions.isaiahgao.common.ScanResultPacket.ScanResult;
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
		PUT,
		DELETE
	}
	
	public static Response sendRequest(RequestType type, String urlpath) throws Exception {
		return sendRequest(type, urlpath, null);
	}
	
	public static Response sendRequest(RequestType type, String urlpath, String body) throws Exception {
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
		System.out.println("Body: " + body);
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
		return new Response(responseCode, result);
	}
	
	public static Future<ScanResultPacket> scanId(String id, String room) {
		return EXE.submit(() -> {
			try {
				String result = sendRequest(RequestType.POST, "/rooms/" + room, "{\"type\":\"scan\",\"cardnumber\":\"" + id + "\"," + Main.getAuthHandler().getAuthJson() + "}").result;
				return new ScanResultPacket(result);
			} catch (Exception e) {
				return new ScanResultPacket(ScanResult.ERROR, null);
			}
		});
	}
	
	public static Future<Boolean> enableRoom(int room) {
		return EXE.submit(() -> {
			try {
				int code = sendRequest(RequestType.POST, "/rooms/" + room, "{\"type\":\"enable\"," + Main.getAuthHandler().getAuthJson() + "}").code;
				return code >= 200 && code < 300;
			} catch (Exception e) {
				return false;
			}
		});
	}

	public static Future<Boolean> disableRoom(int room, String reason) {
		return EXE.submit(() -> {
			try {
				int code = sendRequest(RequestType.POST, "/rooms/" + room, "{\"type\":\"disable\"," + (reason == null ? "" : "\"reason\":\"" + reason + "\",") + Main.getAuthHandler().getAuthJson() + "}").code;
				return code >= 200 && code < 300;
			} catch (Exception e) {
				return false;
			}
		});
	}

	public static Future<Boolean> push(User usd) {
		return EXE.submit(() -> {
			try {
				String usds = usd.toString();
				sendRequest(RequestType.POST, "/users", usds.substring(0, usds.length() - 1) + "," + Main.getAuthHandler().getAuthJson() + "}");
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		});
	}

	public static @Nullable Future<User> getUserData(String userId) {
		return EXE.submit(() -> {
			try {
				String result = sendRequest(RequestType.PUT, "/users/" + userId, "{ "+ Main.getAuthHandler().getAuthJson() + "}").result;
				return new User(result);
			} catch (Exception e) {
				return null;
			}
		});
	}

	public static Future<Boolean> removeUserData(String currentId) {
		return EXE.submit(() -> {
			try {
				Response response = sendRequest(RequestType.DELETE, "/users/" + currentId, "{ "+ Main.getAuthHandler().getAuthJson() + "}");
				if (response.code != 200)
					return false;
				return true;
			} catch (Exception e) {
				return false;
			}
		});
	}
	
	public static class Response {
		public Response(int code, String result) {
			this.code = code;
			this.result = result;
		}
		
		public int code;
		public String result;
	}

}
