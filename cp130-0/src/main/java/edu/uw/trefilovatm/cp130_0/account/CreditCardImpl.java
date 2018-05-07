package edu.uw.trefilovatm.cp130_0.account;

import java.util.logging.Logger;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import edu.uw.ext.framework.account.CreditCard;

/**
 * CrediCardImpl class provide a no argument constructor. Contains information
 * about account's credit card: AccountNumber, Expiration date, holder, issuer,
 * and type.
 * 
 * @author tamara
 *
 */

public class CreditCardImpl implements CreditCard {
	private static Logger log = Logger.getLogger(CreditCardImpl.class.getName());
	private static final long serialVersionUID = 6164864681681631219L;
	private String accountNumber;
	private String expirationDate;
	private String holder;
	private String issuer;
	private String type = "Amex";

	@Override
	public String toString() {
		return "CreditCardImpl [accountNumber=" + accountNumber + ", expirationDate=" + expirationDate + ", holder="
				+ holder + ", issuer=" + issuer + ", type=" + type + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountNumber == null) ? 0 : accountNumber.hashCode());
		result = prime * result + ((expirationDate == null) ? 0 : expirationDate.hashCode());
		result = prime * result + ((holder == null) ? 0 : holder.hashCode());
		result = prime * result + ((issuer == null) ? 0 : issuer.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CreditCardImpl other = (CreditCardImpl) obj;
		if (accountNumber == null) {
			if (other.accountNumber != null)
				return false;
		} else if (!accountNumber.equals(other.accountNumber))
			return false;
		if (expirationDate == null) {
			if (other.expirationDate != null)
				return false;
		} else if (!expirationDate.equals(other.expirationDate))
			return false;
		if (holder == null) {
			if (other.holder != null)
				return false;
		} else if (!holder.equals(other.holder))
			return false;
		if (issuer == null) {
			if (other.issuer != null)
				return false;
		} else if (!issuer.equals(other.issuer))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uw.ext.framework.account.CreditCard#getAccountNumber()
	 */
	public String getAccountNumber() {
		return this.accountNumber;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uw.ext.framework.account.CreditCard#getExpirationDate()
	 */
	public String getExpirationDate() {
		return this.expirationDate;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uw.ext.framework.account.CreditCard#getHolder()
	 */
	public String getHolder() {
		return this.holder;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uw.ext.framework.account.CreditCard#getIssuer()
	 */
	public String getIssuer() {
		return this.issuer;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uw.ext.framework.account.CreditCard#getType()
	 */
	public String getType() {
		return this.type;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uw.ext.framework.account.CreditCard#setAccountNumber(java.lang.String)
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uw.ext.framework.account.CreditCard#setExpirationDate(java.lang.String)
	 */
	public void setExpirationDate(String expDate) {
		this.expirationDate = expDate;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uw.ext.framework.account.CreditCard#setHolder(java.lang.String)
	 */
	public void setHolder(String name) {
		this.holder = name;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uw.ext.framework.account.CreditCard#setIssuer(java.lang.String)
	 */
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uw.ext.framework.account.CreditCard#setType(java.lang.String)
	 */
	public void setType(String type) {
		this.type = type;
	}

	
}
