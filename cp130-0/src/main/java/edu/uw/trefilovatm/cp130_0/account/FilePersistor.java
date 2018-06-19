package edu.uw.trefilovatm.cp130_0.account;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.Address;
import edu.uw.ext.framework.account.CreditCard;
import edu.uw.ext.framework.dao.AccountDao;

public class FilePersistor implements Persistor {
	private static final String TARGET_ACCOUNTS = "target/accounts/";
	private static final String CREDITCARD_DAT = "creditcard.dat";
	private static final String ADDRESS_DAT = "address.dat";
	private static final String ACCOUNT_DAT = "account.dat";
	private static Logger log = Logger.getLogger(FilePersistor.class.getName());

	@Override
	public Account read(String accountName) {
		log.info("Looking up account " + accountName);
		String path = TARGET_ACCOUNTS + accountName;
		File accountDirectory = new File(path);
		if (!accountDirectory.exists()) {
			return null;
		}

		try {
			Account account = readAccountFile(path);
			return account;
		} catch (IOException | AccountException e) {
			log.severe("Couldn't read account " + accountName + " : " + e.getMessage());
			return null;
		}
	}

	@Override
	public void write(Account account) throws AccountException {
		try {
			log.info("Saving account " + account.getName());
			writeAccountFile(TARGET_ACCOUNTS + account.getName(), account);
		} catch (IOException e) {
			throw new AccountException("Account persist fail", e);
		} // TODO Auto-generated method stub
	}

	@Override
	public void delete(String accountName) throws AccountException {
		log.info("Deleting account " + accountName);
		String path = TARGET_ACCOUNTS + accountName;
		File accountDirectory = new File(path);
		if (accountDirectory.exists()) {
			try {
				File address = new File(path + File.separator + ADDRESS_DAT);
				address.delete();
				File creditCard = new File(path + File.separator + CREDITCARD_DAT);
				creditCard.delete();
				File account = new File(path + File.separator + ACCOUNT_DAT);
				account.delete();
				accountDirectory.delete();
			} catch (Exception e) {
				throw new AccountException(e);
			}
		}

	}

	@Override
	public void reset() throws AccountException {
		log.info("Performing reset");
		File f = new File(TARGET_ACCOUNTS);
		f.mkdirs();
		File[] files = f.listFiles();
		for (File file : files) {
			delete(file.getName());
		}

	}

	private static CreditCard readCreditCardFile(String path) throws FileNotFoundException, IOException {
		String fileName = path + File.separator + CREDITCARD_DAT;
		File file = new File(fileName);
		if (!file.exists()) {
			return null;
		}
		try (InputStream in = new FileInputStream(fileName)) {
			try (DataInputStream datain = new DataInputStream(in)) {
				String accountNumber = datain.readUTF();
				String expirationDate = datain.readUTF();
				String holder = datain.readUTF();
				String issuer = datain.readUTF();
				String type = datain.readUTF();
				CreditCard card = new CreditCardImpl();
				card.setAccountNumber(accountNumber);
				card.setExpirationDate(expirationDate);
				card.setHolder(holder);
				card.setIssuer(issuer);
				card.setType(type);
				return card;
			}
		}
	}

	private static void writeCreditCardFile(String path, CreditCard creditCard)
			throws FileNotFoundException, IOException {
		String fileName = path + File.separator + CREDITCARD_DAT;
		if (creditCard == null) {
			File file = new File(fileName);
			file.delete();
			return;
		}
		File dir = new File(path);
		dir.mkdirs();
		try (OutputStream out = new FileOutputStream(fileName)) {
			try (DataOutputStream dataout = new DataOutputStream(out)) {
				dataout.writeUTF(creditCard.getAccountNumber());
				dataout.writeUTF(creditCard.getExpirationDate());
				dataout.writeUTF(creditCard.getHolder());
				dataout.writeUTF(creditCard.getIssuer());
				dataout.writeUTF(creditCard.getType());
			}
		}
	}

	private static Account readAccountFile(String path) throws FileNotFoundException, IOException, AccountException {
		String fileName = path + File.separator + ACCOUNT_DAT;
		try (InputStream in = new FileInputStream(fileName)) {
			try (DataInputStream datain = new DataInputStream(in)) {

				byte isNotNull = datain.readByte();
				String email = null;
				if (isNotNull == 1) {
					email = datain.readUTF();
				}
				isNotNull = datain.readByte();
				String fullName = null;
				if (isNotNull == 1) {
					fullName = datain.readUTF();
				}
				String name = datain.readUTF();
				isNotNull = datain.readByte();
				String phone = null;
				if (isNotNull == 1) {
					phone = datain.readUTF();
				}
				int length = datain.readInt();
				byte[] password = new byte[length];
				for (int i = 0; i < length; i++) {
					password[i] = datain.readByte();
				}
				int balance = datain.readInt();
				Address address = readAddressFile(path);
				CreditCard card = readCreditCardFile(path);
				Account account = new AccountImpl();
				account.setEmail(email);
				account.setFullName(fullName);
				account.setAddress(address);
				account.setBalance(balance);
				account.setCreditCard(card);
				account.setName(name);
				account.setPhone(phone);
				account.setPasswordHash(password);
				return account;
			}
		}
	}

	private static void writeAccountFile(String path, Account account) throws FileNotFoundException, IOException {
		File dir = new File(path);
		dir.mkdirs();
		String fileName = path + File.separator + ACCOUNT_DAT;
		try (OutputStream out = new FileOutputStream(fileName)) {
			try (DataOutputStream dataout = new DataOutputStream(out)) {
				if (account.getEmail() == null) {
					dataout.writeByte(0);
				} else {
					dataout.writeByte(1);
					dataout.writeUTF(account.getEmail());
				}
				if (account.getFullName() == null) {
					dataout.writeByte(0);
				} else {
					dataout.writeByte(1);
					dataout.writeUTF(account.getFullName());
				}
				dataout.writeUTF(account.getName());
				if (account.getPhone() == null) {
					dataout.writeByte(0);
				} else {
					dataout.writeByte(1);
					dataout.writeUTF(account.getPhone());
				}
				dataout.writeInt(account.getPasswordHash().length);
				dataout.write(account.getPasswordHash());
				dataout.writeInt(account.getBalance());
			}
		}
		writeAddressFile(path, account.getAddress());
		writeCreditCardFile(path, account.getCreditCard());
	}

	private static Address readAddressFile(String path) throws FileNotFoundException, IOException {
		String fileName = path + File.separator + ADDRESS_DAT;
		File file = new File(fileName);
		if (!file.exists()) {
			return null;
		}
		try (InputStream in = new FileInputStream(fileName)) {
			try (DataInputStream datain = new DataInputStream(in)) {
				String city = datain.readUTF();
				String state = datain.readUTF();
				String streetAddress = datain.readUTF();
				String zipCode = datain.readUTF();
				Address address = new AddressImpl();
				address.setCity(city);
				address.setState(state);
				address.setStreetAddress(streetAddress);
				address.setZipCode(zipCode);
				return address;
			}
		}
	}

	private static void writeAddressFile(String path, Address address) throws FileNotFoundException, IOException {
		String fileName = path + File.separator + ADDRESS_DAT;
		if (address == null) {
			File file = new File(fileName);
			file.delete();
			return;
		}
		File dir = new File(path);
		dir.mkdirs();
		try (OutputStream out = new FileOutputStream(fileName)) {
			try (DataOutputStream dataout = new DataOutputStream(out)) {
				dataout.writeUTF(address.getCity());
				dataout.writeUTF(address.getState());
				dataout.writeUTF(address.getStreetAddress());
				dataout.writeUTF(address.getZipCode());
			}
		}
	}

	public static void main(String args[]) throws FileNotFoundException, IOException, AccountException {
	}

}
