/**
 * 
 */
package edu.uw.trefilovatm.cp130_0.account;

import java.util.Arrays;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.account.Address;
import edu.uw.ext.framework.account.CreditCard;
import edu.uw.ext.framework.order.Order;

/**
 * @author tamara
 *
 */
public class AccountImpl implements Account {
	private static final int MIN_LENGTH_NAME = 8;
	private static Logger log = Logger.getLogger(AccountDaoImpl.class.getName());     
	private static final long serialVersionUID = 2918450339894593830L;
	
	@JsonDeserialize(as = AddressImpl.class)
	private Address address;
	private int balance;
	@JsonDeserialize(as = CreditCardImpl.class)
	private CreditCard creditCard;
	private String email;
	private String fullName;
	private String name;
	private byte[] password;
	private String phone;
	private transient AccountManager acctMngr;
	/**
	 * 
	 */
	public AccountImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + balance;
		result = prime * result + ((creditCard == null) ? 0 : creditCard.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Arrays.hashCode(password);
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
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
		AccountImpl other = (AccountImpl) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (balance != other.balance)
			return false;
		if (creditCard == null) {
			if (other.creditCard != null)
				return false;
		} else if (!creditCard.equals(other.creditCard))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (fullName == null) {
			if (other.fullName != null)
				return false;
		} else if (!fullName.equals(other.fullName))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (!Arrays.equals(password, other.password))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AccountImpl [address=" + address + ", balance=" + balance + ", creditCard=" + creditCard + ", email="
				+ email + ", fullName=" + fullName + ", name=" + name + ", password=" + Arrays.toString(password)
				+ ", phone=" + phone + "]";
	}

	//non argument constructor (check name length and balance)
	@Override
	/**
     * Gets the account address.
     *
     *  @return the accounts address
     */
	public Address getAddress() {
		return this.address;
	}

	@Override
	 /**
     * Gets the account balance, in cents.
     *
     * @return the current balance of the account
     */
	public int getBalance() {
		return this.balance;
	}

	@Override
	  /**
     * Gets the account credit card.
     *
     * @return the credit card
     */
	public CreditCard getCreditCard() {
		return this.creditCard;
	}

	@Override

    /**
     * Gets the email address.
     *
     * @return the email address
     */
	public String getEmail() {
		return this.email;
	}

	@Override
	  /**
     * Gets the full name of the account holder.
     *
     * @return the account holders full name
     */
	public String getFullName() {
		return this.fullName;
	}

	@Override
	/**
     * Get the account name.
     *
     * @return the name of the account
     */
	public String getName() {
		return this.name;
	}

	@Override
	/**
     * Gets the hashed password.
     *
     * @return the hashed password
     */
	public byte[] getPasswordHash() {
		return this.password;
	}

	@Override
	 /**
     * Gets the phone number.
     *
     * @return the phone number
     */
	public String getPhone() {
		return this.phone;
	}

	@Override
	public void reflectOrder(Order order, int executionPrice) {
		log.info("Reflect order for account "+this.name);
		int balance = getBalance();
		if(order.isBuyOrder()) {
			balance -= executionPrice*order.getNumberOfShares();
		} else {
			balance += executionPrice*order.getNumberOfShares();
		}
		setBalance(balance);
	}

	private void persist() {
		log.info("Persist account "+this.name);
		try {
			if(acctMngr!=null) {
				acctMngr.persist(this);	
			}
		} catch (AccountException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	/**
	 * Sets the account manager responsible for persisting/managing this account. This may be invoked exactly
	 *  once on any given account, any subsequent invocations should be ignored. 
	 *  The account manager member should not be serialized with implementing class object.
	 */
	public void registerAccountManager(AccountManager m) {
		log.info("Setting account manager for account "+this.name);
		this.acctMngr = m;
	}

	@Override
	 /**
     * Sets the account address.
     *
     *  @param address the address for the account
     */
	public void setAddress(Address address) {
		this.address = address;
		persist();
	}

	@Override

    /**
     * Sets the account balance.
     *
     * @param balance the value to set the balance to in cents
     */
	public void setBalance(int balance){
		this.balance = balance;
		persist();
	}

	@Override
	/**
     * Sets the account credit card.
     *
     * @param card the value to be set for the credit card
     */
	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
		persist();
	}

	@Override
	 /**
     * Sets the account email address.
     *
     * @param email the email address
     */
	public void setEmail(String email) {
		this.email = email;
		persist();
	}

	@Override
	   /**
     * Sets the full name of the account holder.
     *
     * @param fullName the account holders full name
     */
	public void setFullName(String fullName) {
		this.fullName = fullName;
		persist();
	}

	@Override
	/**
     * Sets the account name.  This operation is not generally used but is
     * provided for JavaBean conformance.
     *
     * @param acctName the value to be set for the account name
     *
     * @throws AccountException if the account name is unacceptable
     */
	public void setName(String name) throws AccountException {
		if(name.length()<MIN_LENGTH_NAME) {
			throw new AccountException();
		}
		this.name = name;
	}

	@Override
	/**
     * Sets the hashed password.
     *
     * @param passwordHash the value to be st for the password hash
     */
	public void setPasswordHash(byte[] password) {
		this.password = password;
		persist();
	}

	@Override
	 /**
     * Sets the account phone number.
     *
     * @param phone value for the account phone number
     */
	public void setPhone(String phone) {
		this.phone = phone;
		persist();
	}
}
