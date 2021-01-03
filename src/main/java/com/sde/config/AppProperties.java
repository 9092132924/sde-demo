package com.sde.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * @author Dastagiri Varada
 * @since 26/12/2020
 */

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {


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

		public String getConfirmAccount() {
			return confirmAccount;
		}

		public void setconfirmAccount(String confirmAccount) {
			this.confirmAccount = confirmAccount;
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
		private String confirmAccount;
		private String forgotaccount;
	

	

}