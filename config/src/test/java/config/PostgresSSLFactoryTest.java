package config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PostgresSSLFactoryTest
{
	
	@Test
	public void test()
	{
		
		System.setProperty("KEYSTORE_FILE", "postgres-client-keystore.jks");
		System.setProperty("KEYSTORE_PASSWORD", "password");
		System.setProperty("KEY_PASSWORD", "password");
		System.setProperty("TRUSTSTORE_FILE", "postgres-client-trustsore.jks");
		System.setProperty("TRUSTSTORE_PASSWORD", "password");
		
		// PostgresSSLFactory ssl = new PostgresSSLFactory();
		
		// try {
		// ssl.createSocket("localhost", 5432).close();
		// System.out.println("ssl socket created");
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		
		String url = "jdbc:postgresql://localhost:5432/test";
		Properties props = new Properties();
		props.setProperty("loggerLevel", "TRACE");
		
		props.setProperty("user", "postgres");
		props.setProperty("password", "postgres");
		props.setProperty("sslmode", "require");// verify-ca
		props.setProperty("ssl", "true");
		props.setProperty("sslfactory", "config.PostgresSSLFactory");
		
		try
		{
			Connection conn = DriverManager.getConnection(url, props);
			
			System.out.println("ssl connection created");
			conn.close();
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
