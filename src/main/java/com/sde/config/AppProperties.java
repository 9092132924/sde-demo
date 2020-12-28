package com.sde.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author Dastagiri Varada
 * @since 26/12/2020
 */

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
	private final Auth auth = new Auth();

	public static class Auth {
		public String getTokenSecret() {
			return tokenSecret;
		}

		public void setTokenSecret(String tokenSecret) {
			this.tokenSecret = tokenSecret;
		}

		public long getTokenExpirationMsec() {
			return tokenExpirationMsec;
		}

		public void setTokenExpirationMsec(long tokenExpirationMsec) {
			this.tokenExpirationMsec = tokenExpirationMsec;
		}

		public long getForwardMailExpiryTime() {
			return forwardMailExpiryTime;
		}

		public void setForwardMailExpiryTime(long forwardMailExpiryTime) {
			this.forwardMailExpiryTime = forwardMailExpiryTime;
		}

		public String getConfirmaccount() {
			return confirmaccount;
		}

		public void setConfirmaccount(String confirmaccount) {
			this.confirmaccount = confirmaccount;
		}

		public String getForgotaccount() {
			return forgotaccount;
		}

		public void setForgotaccount(String forgotaccount) {
			this.forgotaccount = forgotaccount;
		}

		private String tokenSecret;
		private long tokenExpirationMsec;
		private long forwardMailExpiryTime;
		private String confirmaccount;
		private String forgotaccount;
	}

	public Auth getAuth() {
		return auth;
	}

}