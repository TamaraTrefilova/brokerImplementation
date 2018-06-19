package edu.uw.trefilovatm.cp130_0.codec;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import edu.uw.ext.framework.crypto.PrivateMessageCodec;
import edu.uw.ext.framework.crypto.PrivateMessageTriple;

public class PrivateMessageCodecImpl implements PrivateMessageCodec {

	@Override
	public byte[] decipher(PrivateMessageTriple triple, String recipientKeyStoreName, char[] recipientKeyStorePasswd,
			String recipientKeyName, char[] recipientKeyPasswd, String trustStoreName, char[] trustStorePasswd,
			String signerCertName) throws GeneralSecurityException, IOException {
		KeyStore keyStore = loadKeyStore(recipientKeyStoreName, "JCEKS", recipientKeyStorePasswd);
		Key keyPriv = keyStore.getKey(recipientKeyName, recipientKeyPasswd);
		byte[] decryptedKeyBytes = decryptWithKey(triple.getEncipheredSharedKey(), keyPriv);
		Key key = keyBytesToAesSecretKey(decryptedKeyBytes);
		byte[] decryptedText = decryptWithKey(triple.getCiphertext(),key);
		
		KeyStore trustStore = loadKeyStore(trustStoreName, "JCEKS", trustStorePasswd);
		Certificate cert = trustStore.getCertificate(signerCertName);
		PublicKey pubKey = cert.getPublicKey();
		if(!verifyWithKey(decryptedText, triple.getSignature(), pubKey)) {
			throw new GeneralSecurityException("Signature is not valid");
		}
		
		return decryptedText;
	}

	private byte[] decryptWithKey(byte[] encryptedData, Key key) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher decipher = Cipher.getInstance(key.getAlgorithm());
		decipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decryptedData = decipher.doFinal(encryptedData);
		return decryptedData;
	}

	private byte[] encryptWithKey(byte[] plaintext, Key secretKey) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher encryptCipher = Cipher.getInstance(secretKey.getAlgorithm());
		encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedData = encryptCipher.doFinal(plaintext);
		return encryptedData;
	}

	@Override
	public PrivateMessageTriple encipher(byte[] plaintext, String senderKeyStoreName, char[] senderKeyStorePasswd,
			String senderKeyName, char[] senderKeyPasswd, String senderTrustStoreName, char[] senderTrustStorePasswd,
			String recipientCertName) throws GeneralSecurityException, IOException {
		SecretKey secretKey = generateAesSecretKey();
		byte[] encryptedData = encryptWithKey(plaintext, secretKey);
		byte[] keyBytes = secretKey.getEncoded();

		KeyStore trustStore = loadKeyStore(senderTrustStoreName, "JCEKS", senderTrustStorePasswd);
		Certificate cert = trustStore.getCertificate(recipientCertName);
		PublicKey pubKey = cert.getPublicKey();
		byte[] encryptedKeyBytes = encryptWithKey(keyBytes, pubKey);

		KeyStore keyStore = loadKeyStore(senderKeyStoreName, "JCEKS", senderKeyStorePasswd);
		PrivateKey keyPriv = (PrivateKey) keyStore.getKey(senderKeyName, senderKeyPasswd);
		byte[] signedData = signWithKey(plaintext, keyPriv);

		return new PrivateMessageTriple(encryptedKeyBytes, encryptedData, signedData);
	}

	private byte[] signWithKey(byte[] plaintext, PrivateKey keyPriv) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature signer = Signature.getInstance("MD5withRSA");
		signer.initSign(keyPriv);
		signer.update(plaintext);
		return signer.sign();
	}

	private boolean verifyWithKey(byte[] data, byte signature[], PublicKey key) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature verifier = Signature.getInstance("MD5withRSA");
		verifier.initVerify(key);
		verifier.update(data);
		return verifier.verify(signature);
	}	
	
	public static KeyStore loadKeyStore(String storeFile, String storeType, char[] storePasswd)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		try (FileInputStream stream = new FileInputStream("target/classes/"+storeFile)) {
			return loadKeyStore(storeType, storePasswd, stream);
		}
	}

	public static KeyStore loadKeyStore(String storeType, char[] storePasswd, InputStream inputStream)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore keyStore = KeyStore.getInstance(storeType);
		keyStore.load(inputStream, storePasswd);
		return keyStore;
	}

	public static SecretKey generateAesSecretKey() throws NoSuchAlgorithmException {
		KeyGenerator generator = KeyGenerator.getInstance("AES");
		generator.init(128);
		SecretKey key = generator.generateKey();
		return key;
	}

	public static SecretKey keyBytesToAesSecretKey(final byte[] key) throws NoSuchAlgorithmException {
		SecretKey secKey = new SecretKeySpec(key, 0, 16, "AES");
		return secKey;
	}

}
