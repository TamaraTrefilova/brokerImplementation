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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.Address;
import edu.uw.ext.framework.account.CreditCard;
import edu.uw.ext.framework.dao.AccountDao;

public class JsonPersistor implements Persistor {

	private static final String TARGET_ACCOUNTS = "target/accounts/";
//	private static final String CREDITCARD_DAT = "creditcard.dat";
//	private static final String ADDRESS_DAT = "address.dat";
//	private static final String ACCOUNT_DAT = "account.dat";
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
			Account account = readAccountFile(path, accountName);
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
		}
	}

	@Override
	public void delete(String accountName) throws AccountException {
		log.info("Deleting account " + accountName);
		String path = TARGET_ACCOUNTS +accountName+ File.separator+ accountName + ".json";
		File accountDirectory = new File(path);
		if (accountDirectory.exists()) {
			try {
				File account = new File(path + File.separator + accountName + ".json");
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
		File[] files = f.listFiles();
		for (File file : files) {
			delete(file.getName());
		}

	}

//	private static CreditCard readCreditCardFile(String path) throws FileNotFoundException, IOException {
//		String fileName = path + File.separator + CREDITCARD_DAT;
//		File file = new File(fileName);
//		if (!file.exists()) {
//			return null;
//		}
//		try (InputStream in = new FileInputStream(fileName)) {
//			try (DataInputStream datain = new DataInputStream(in)) {
//				String accountNumber = datain.readUTF();
//				String expirationDate = datain.readUTF();
//				String holder = datain.readUTF();
//				String issuer = datain.readUTF();
//				String type = datain.readUTF();
//				CreditCard card = new CreditCardImpl();
//				card.setAccountNumber(accountNumber);
//				card.setExpirationDate(expirationDate);
//				card.setHolder(holder);
//				card.setIssuer(issuer);
//				card.setType(type);
//				return card;
//			}
//		}
//	}

//	private static void writeCreditCardFile(String path, CreditCard creditCard)
//			throws FileNotFoundException, IOException {
//		String fileName = path + File.separator + CREDITCARD_DAT;
//		if (creditCard == null) {
//			File file = new File(fileName);
//			file.delete();
//			return;
//		}
//		File dir = new File(path);
//		dir.mkdirs();
//		try (OutputStream out = new FileOutputStream(fileName)) {
//			try (DataOutputStream dataout = new DataOutputStream(out)) {
//				dataout.writeUTF(creditCard.getAccountNumber());
//				dataout.writeUTF(creditCard.getExpirationDate());
//				dataout.writeUTF(creditCard.getHolder());
//				dataout.writeUTF(creditCard.getIssuer());
//				dataout.writeUTF(creditCard.getType());
//			}
//		}
//	}

	private static Account readAccountFile(String path, String accountName)
			throws FileNotFoundException, IOException, AccountException {
		String fileName = path + File.separator+accountName + ".json";
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Account account =  mapper.readValue(new File(fileName), AccountImpl.class);
		return account;
	}

	private static void writeAccountFile(String path, Account account) throws FileNotFoundException, IOException {
		File dir = new File(path);
		dir.mkdirs();
		String fileName = path + File.separator + account.getName() + ".json";
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(new File(fileName), account);
		// writeAddressFile(path, account.getAddress());
		// writeCreditCardFile(path, account.getCreditCard());
	}

//	private static Address readAddressFile(String path) throws FileNotFoundException, IOException {
//		String fileName = path + File.separator + ADDRESS_DAT;
//		File file = new File(fileName);
//		if (!file.exists()) {
//			return null;
//		}
//		try (InputStream in = new FileInputStream(fileName)) {
//			try (DataInputStream datain = new DataInputStream(in)) {
//				String city = datain.readUTF();
//				String state = datain.readUTF();
//				String streetAddress = datain.readUTF();
//				String zipCode = datain.readUTF();
//				Address address = new AddressImpl();
//				address.setCity(city);
//				address.setState(state);
//				address.setStreetAddress(streetAddress);
//				address.setZipCode(zipCode);
//				return address;
//			}
//		}
//	}
//
//	private static void writeAddressFile(String path, Address address) throws FileNotFoundException, IOException {
//		String fileName = path + File.separator + ADDRESS_DAT;
//		if (address == null) {
//			File file = new File(fileName);
//			file.delete();
//			return;
//		}
//		File dir = new File(path);
//		dir.mkdirs();
//		try (OutputStream out = new FileOutputStream(fileName)) {
//			try (DataOutputStream dataout = new DataOutputStream(out)) {
//				dataout.writeUTF(address.getCity());
//				dataout.writeUTF(address.getState());
//				dataout.writeUTF(address.getStreetAddress());
//				dataout.writeUTF(address.getZipCode());
//			}
//		}
//	}

}
