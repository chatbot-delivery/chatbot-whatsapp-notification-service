package com.chatbot.delivery.notification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

/**
 * Azure Functions with HTTP Trigger.
 */
/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

public class Function {
	/**
	 * This function listens at endpoint "/api/HttpExample". Two ways to invoke it
	 * using "curl" command in bash: 1. curl -d "HTTP Body" {your
	 * host}/api/HttpExample 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
	 * 
	 * @throws Exception
	 */
	private final String CLIENT_SECRET = System.getenv("WHATSAPP_TOKEN");
	private final String PHONE_ID = System.getenv("PHONE_ID");
	private final String WA_GATEWAY_URL = "https://graph.facebook.com/v14.0/" + PHONE_ID + "/messages";
	private final static String encoding = Charset.defaultCharset().name();

	@FunctionName("sendMessage")
	public HttpResponseMessage run(@HttpTrigger(name = "req", methods = { HttpMethod.GET,
			HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
			final ExecutionContext context) throws Exception {

		context.getLogger().info("Java HTTP trigger processed a request.");

		context.getLogger().info(System.getenv("WHATSAPP_TOKEN"));
		// Parse query parameter
		final String whtsappno = request.getQueryParameters().get("whtsappno");
		final String name = request.getQueryParameters().get("name");
		final String trackingId = request.getQueryParameters().get("trackingId");
		final String lang = request.getQueryParameters().get("lang");
		String template_name = "fdmi_delivery_notification_template";

		context.getLogger().info("whtsappno: " + whtsappno);
		context.getLogger().info("name: " + name);
		context.getLogger().info("trackingId: " + trackingId);
		context.getLogger().info("lang: " + lang);
		context.getLogger().info("template_name: " + template_name);

		int statusCode = sendMessage(buildMessage(whtsappno, template_name, lang, name, trackingId, context),
				WA_GATEWAY_URL, CLIENT_SECRET, context);
		context.getLogger().info("Message sent successfully");
		if (statusCode == 200) {
			return request.createResponseBuilder(HttpStatus.OK)
					.body("Hello " + name + " ,your message sent successfully").build();
		} else {
			return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
					.body("Hello " + name + " ,your message could not be sent").build();
		}

	}

	public static String buildMessage(String number, String template, String lang, String name, String trackingId,
			final ExecutionContext context) throws Exception {
		// TODO: Should have used a 3rd party library to make a JSON string from an
		// object
		initialize();

		String jsonPayload = readFile("message_template.json", Charset.defaultCharset(), context);

		jsonPayload = jsonPayload.replace("$RECEPIENT_NUMBER", number);
		jsonPayload = jsonPayload.replace("$TEMPLATE", template);
		jsonPayload = jsonPayload.replace("$NAME", name);
		jsonPayload = jsonPayload.replace("$TRCK_NBR", trackingId);
		jsonPayload = jsonPayload.replace("$LANG", lang.replace("-", "_"));
		jsonPayload = jsonPayload.replace("$LOCALE", lang);
		
		context.getLogger().info("jsonPayload {} " + jsonPayload);

		return jsonPayload;
	}

	static String readFile(String fileName, Charset encoding, final ExecutionContext context) throws IOException, URISyntaxException {
		
		FileResourcesUtils app = new FileResourcesUtils();
        return app.readFile(fileName);
	}

	private static void initialize() {
		try {

			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };

			// Install the all-trusting trust manager
			SSLContext sc;
			sc = SSLContext.getInstance("SSL");

			sc.init(null, trustAllCerts, new java.security.SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int sendMessage(String jsonPayload, String urlstring, String accessToken, final ExecutionContext context)
			throws Exception {

		context.getLogger().info("start sendMessage:");
		context.getLogger().info(urlstring);
		context.getLogger().info(accessToken);
		try {
			URL url = new URL(urlstring);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Bearer " + accessToken);
			conn.setRequestProperty("Content-Type", "application/json");

//			conn.setRequestProperty("Accept", "application/json");
//			conn.setRequestProperty("Accept-Charset", this.encoding);

			OutputStream os = conn.getOutputStream();
			os.write(jsonPayload.getBytes());
			os.flush();
			os.close();

			int statusCode = conn.getResponseCode();
			context.getLogger().info("Response from WA Gateway: \n");
			context.getLogger().info("Status Code: " + statusCode);
			BufferedReader br = new BufferedReader(
					new InputStreamReader((statusCode == 200) ? conn.getInputStream() : conn.getErrorStream()));
			String output;
			while ((output = br.readLine()) != null) {
				context.getLogger().info(output);
			}
			conn.disconnect();
			return statusCode;
		} catch (Exception e) {
			e.printStackTrace();
			context.getLogger().log(Level.SEVERE, "Error occured", e.getStackTrace());
		}
		context.getLogger().info("end sendMessage");

		return 0;

	}

	

	

}
