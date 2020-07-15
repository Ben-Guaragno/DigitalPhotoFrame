package com.voidStudios.photoDisplay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class EnergyManager {

	private String ip;
	private DecimalFormat df;

	public EnergyManager(String ip) {
		this.ip=ip;
		df=new DecimalFormat("#0.0");
	}

	public String getEnergy() {
		try {
			URL url;
			url=new URL("https://"+ip+"/api/meters/solar");
			HttpURLConnection httpCon=(HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod("GET");

			//Ignore the self-signed SSL certificate
			setAcceptAllVerifier((HttpsURLConnection) httpCon);

			BufferedReader in=new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
			String inputLine;
			StringBuffer content=new StringBuffer();
			while((inputLine=in.readLine())!=null) {
				content.append(inputLine);
			}
			in.close();

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
		}catch(Exception e) {
			//If anything went wrong return null
			System.err.println(new Date()+": Warning: Failed to get energy");
			return null;
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
