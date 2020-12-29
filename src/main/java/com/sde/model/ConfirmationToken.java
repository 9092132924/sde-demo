package com.sde.model;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * @author Dastagiri Varada
 * @since 26/12/2020
 */

@Entity
public class ConfirmationToken {

	private static final int EXPIRATION = 60 * 24;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "token_id")
	private long tokenid;

	@Temporal(TemporalType.TIMESTAMP)
	private Date expiryDate;

	@Column(name = "confirmation_token")
	private String confirmToken;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	@OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "USER_ID")
	private User user;

	public ConfirmationToken() {
	}

	public ConfirmationToken(User user) {
		this.user = user;
		createdDate = new Date();
		confirmToken = UUID.randomUUID().toString();
		expiryDate = calculateExpiryDate(EXPIRATION);
	}

	private Date calculateExpiryDate(int expiryTimeInMinutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Timestamp(cal.getTime().getTime()));
		cal.add(Calendar.MINUTE, expiryTimeInMinutes);
		return new Date(cal.getTime().getTime());
	}

	public String getConfirmToken() {
		return confirmToken;
	}

	public void setConfirmToken(String confirmToken) {
		this.confirmToken = confirmToken;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public long getTokenid() {
		return tokenid;
	}

	public void setTokenid(long tokenid) {
		this.tokenid = tokenid;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}
}
