package com.voidStudios.photoDisplay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class EnergyManager {

	private static final String COOKIES_HEADER="Set-Cookie";
	private CookieManager cManager;
	private String ip;
	private String password;
	private DecimalFormat df;

	public EnergyManager(String ip, String password) {
		this.ip=ip;
		this.password=password;
		df=new DecimalFormat("#0.0");
		cManager=new CookieManager();
		//Grab an initial authentication cookie
		getAuthCookie();
	}

	/**
	 * Fetches the instant_power from the Powerwall.
	 * Potentially retries one additional time on a 403 failure, which tries to fetch a new authentication cookie.
	 * Returns null on any error.
	 * 
	 * @return The current kW being produced by the solar array. 0 if less than .1kW. Null on error.
	 */
	public String getEnergy() {
		boolean authRetry=false;
		while(true) {
			try {
				String ret=getEnergyHelper();
				return ret;
			}catch(Exception e) {
				//Check if server returns 403 unauthorized
				if(e.getMessage().contains("Server returned HTTP response code: 403 for URL")) {
					//Check if this is the second try
					//(i.e. attempted to get a new cookie but server returned 403 still)
					if(authRetry) {
						System.err.println(new Date()+": Warning: Authentication failed: "+e.getMessage());
						return null;
					}
					//Try and get a new authentication cookie
					getAuthCookie();
					authRetry=true;
					continue;
				}
				//Something else went wrong (not an expected 403 return)
				//Return null, log error
				System.err.println(new Date()+": Warning: Failed to get energy with error: "+e.getMessage());
				return null;
			}
		}
	}

	private String getEnergyHelper() throws Exception {
		//Setup connection to Powerwall
		URL url=new URL("https://"+ip+"/api/meters/solar");
		HttpURLConnection httpCon=(HttpURLConnection) url.openConnection();
		httpCon.setRequestMethod("GET");

		//Ignore the self-signed SSL certificate
		setAcceptAllVerifier((HttpsURLConnection) httpCon);

		//Add cookies to request, if any
		//This allows authentication with the Powerwall
		if(cManager.getCookieStore().getCookies().size()>0) {
			StringJoiner joiner=new StringJoiner(";");
			for(HttpCookie c : cManager.getCookieStore().getCookies()) {
				joiner.add(c.toString());
			}
			httpCon.setRequestProperty("Cookie", joiner.toString());
		}

		//Read Powerwall response
		BufferedReader in=new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
		String inputLine;
		StringBuffer content=new StringBuffer();
		while((inputLine=in.readLine())!=null) {
			content.append(inputLine);
		}
		in.close();

		//Should probably JSON parse this instead of string searching
		//Find instant_power, and skip to the actual value (+2)
		int start=content.indexOf("instant_power")+"instant_power".length()+2;
		String s=null;
		//Extract the number, starting at start and ending at the first ',' after that
		if(start!=-1) {
			s=content.substring(start);
			int end=s.indexOf(',');
			s=s.substring(0, end);
		}

		//Convert to double and round
		double kW=Double.parseDouble(s)/1000;
		//If energy is negligible (less than 100 watts) return null
		if(kW<.1)
			return null;
		return df.format(kW)+" kW";
	}

	/**
	 * Fetches an authentication cookie from the Powerwall.
	 * Version 20.49 of Powerwall software necessitated authentication for access to API calls.
	 * Username differentiates between a customer sign in and a contractor sign in.
	 * Email is presently not used for authentication, so a dummy email is presented.
	 */
	private void getAuthCookie() {
		//Remove previous cookies
		cManager.getCookieStore().removeAll();

		try {
			//Setup connection to login page
			URL url=new URL("https://"+ip+"/api/login/Basic");
			HttpURLConnection httpCon=(HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod("POST");
			httpCon.setRequestProperty("Content-Type", "application/json; utf-8");
			httpCon.setDoOutput(true);

			//Ignore the self-signed SSL certificate
			setAcceptAllVerifier((HttpsURLConnection) httpCon);

			//Send the data for authentication
			String jsonInputString="{\"username\":\"customer\",\"password\":\""+password
					+"\",\"email\":\"dummy@email.domain\",\"force_sm_off\":false}";
			try(OutputStream os=httpCon.getOutputStream()) {
				byte[] input=jsonInputString.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			//Parse headers and extract cookies
			Map<String, List<String>> headerFields=httpCon.getHeaderFields();
			List<String> cookiesHeader=headerFields.get(COOKIES_HEADER);

			//Load cookies to cookie manager
			if(cookiesHeader!=null) {
				for(String cookie : cookiesHeader) {
					cManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
				}
			}
		}catch(Exception e) {
			System.err.println(new Date()+": Warning: Failed to get authentication cookie with error: "+e.getMessage());
		}
	}

	//Functions to bypass SSL checking, as the Powerwall uses a self signed certificate that causes problems
	private static void setAcceptAllVerifier(HttpsURLConnection connection) throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sc=SSLContext.getInstance("SSL");
		sc.init(null, ALL_TRUSTING_TRUST_MANAGER, new java.security.SecureRandom());
		connection.setSSLSocketFactory(sc.getSocketFactory());
		connection.setHostnameVerifier(ALL_TRUSTING_HOSTNAME_VERIFIER);
	}

	private static final TrustManager[] ALL_TRUSTING_TRUST_MANAGER=new TrustManager[] {new X509TrustManager() {

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		@SuppressWarnings("unused")
		public void checkClientTrusted(X509Certificate[] certs, String authType) {
		}

		@SuppressWarnings("unused")
		public void checkServerTrusted(X509Certificate[] certs, String authType) {
		}
	}};
	private static final HostnameVerifier ALL_TRUSTING_HOSTNAME_VERIFIER=new HostnameVerifier() {

		@SuppressWarnings("unused")
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
}
