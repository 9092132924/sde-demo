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

@Entity
public class ConfirmationToken {
	
	private static final int EXPIRATION =60 * 24;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="token_id")
	private long tokenid;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiryDate;

	@Column(name="confirmation_token")
	private String confirmationToken;
	
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
		confirmationToken = UUID.randomUUID().toString();
		expiryDate=calculateExpiryDate(EXPIRATION);
	}
	
	private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE,expiryTimeInMinutes );
        return new Date(cal.getTime().getTime());
    }

	public String getConfirmationToken() {
		return confirmationToken;
	}

	public void setConfirmationToken(String confirmationToken) {
		this.confirmationToken = confirmationToken;
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
