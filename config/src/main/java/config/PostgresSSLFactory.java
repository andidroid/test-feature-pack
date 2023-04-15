package config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.postgresql.ssl.DefaultJavaSSLFactory;
import org.postgresql.ssl.WrappedFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresSSLFactory extends WrappedFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(PostgresSSLFactory.class);

	private static final String SSL_CONTEXT = System.getProperty("SSL_CONTEXT", "default");

	private static final String KEYSTORE_FILE = System.getProperty("KEYSTORE_FILE");
	private static final String KEYSTORE_PASSWORD = System.getProperty("KEYSTORE_PASSWORD");
	private static final String KEY_PASSWORD = System.getProperty("KEY_PASSWORD");
	private static final String TRUSTSTORE_FILE = System.getProperty("TRUSTSTORE_FILE");
	private static final String TRUSTSTORE_PASSWORD = System.getProperty("TRUSTSTORE_PASSWORD");

	public PostgresSSLFactory() {
		try {
			if ("default".equalsIgnoreCase(SSL_CONTEXT)) {
				super.factory = SSLContext.getDefault().getSocketFactory();
			} else {
				super.factory = createSSLFactory(KEYSTORE_FILE, KEYSTORE_PASSWORD, KEY_PASSWORD, TRUSTSTORE_FILE,
						TRUSTSTORE_PASSWORD);
			}

			System.out.println("ssl factory created " + this.factory);
		} catch (Exception e) {
			LOGGER.error("error creating postgres ssl factory", e);
			e.printStackTrace();
		}
	}

	private SSLSocketFactory createSSLFactory(String keystoreFile, String keystorePassword, String keyPassword,
			String truststoreFile, String truststorePassword)
			throws Exception {

		KeyStore keystore = loadKeyStore(keystoreFile, keystorePassword);
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keystore, keyPassword.toCharArray());

		KeyStore truststore = loadKeyStore(truststoreFile, truststorePassword);
		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(truststore);

		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

		return sslContext.getSocketFactory();
	}

	private KeyStore loadKeyStore(String fileName, String password) throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, FileNotFoundException, IOException {
		char[] pwdArray = password.toCharArray();
		KeyStore ks = KeyStore.getInstance("JKS");
		try (InputStream in = new FileInputStream(fileName)) {
			ks.load(in, pwdArray);
		}

		return ks;
	}

}
