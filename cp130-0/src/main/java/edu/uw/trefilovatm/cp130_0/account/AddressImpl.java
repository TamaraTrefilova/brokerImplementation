package edu.uw.trefilovatm.cp130_0.account;

import java.util.logging.Logger;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import edu.uw.ext.framework.account.Address;

/**
 * Class AddressImpl implements Address interface, provides no argument
 * constructor. Contains information about account's address : City, State,
 * Street, and ZipCode.
 * 
 * @author tamara
 *
 */

public class AddressImpl implements Address {
	private static Logger log = Logger.getLogger(AddressImpl.class.getName());
	private static final long serialVersionUID = -8858430743268071318L;
	private String city;
	private String state;
	private String streetAddress;
	private String zipCode;

	/**
	 * A no argument constructor
	 */
	public AddressImpl() {
		super();
		city = null;
		state = null;
		streetAddress = null;
		zipCode = null;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uw.ext.framework.account.Address#getCity()
	 */
	public String getCity() {
		return this.city;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uw.ext.framework.account.Address#getState()
	 */
	public String getState() {
		return this.state;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uw.ext.framework.account.Address#getStreetAddress()
	 */
	public String getStreetAddress() {
		return this.streetAddress;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uw.ext.framework.account.Address#getZipCode()
	 */
	public String getZipCode() {
		return this.zipCode;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uw.ext.framework.account.Address#setCity(java.lang.String)
	 */
	public void setCity(String city) {
		this.city = city;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uw.ext.framework.account.Address#setState(java.lang.String)
	 */
	public void setState(String state) {
		this.state = state;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uw.ext.framework.account.Address#setStreetAddress(java.lang.String)
	 */
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uw.ext.framework.account.Address#setZipCode(java.lang.String)
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	@Override
	public String toString() {
		return "AddressImpl [city=" + city + ", state=" + state + ", streetAddress=" + streetAddress + ", zipCode="
				+ zipCode + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((streetAddress == null) ? 0 : streetAddress.hashCode());
		result = prime * result + ((zipCode == null) ? 0 : zipCode.hashCode());
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
		AddressImpl other = (AddressImpl) obj;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (streetAddress == null) {
			if (other.streetAddress != null)
				return false;
		} else if (!streetAddress.equals(other.streetAddress))
			return false;
		if (zipCode == null) {
			if (other.zipCode != null)
				return false;
		} else if (!zipCode.equals(other.zipCode))
			return false;
		return true;
	}

}
