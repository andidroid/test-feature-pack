package config;

import java.util.Map.Entry;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.junit.Test;

// import static org.junit.jupiter.api.Assertions.*;
//
// import org.junit.jupiter.api.Test;

public class MyDirectoryConfigSourceProviderTest
{
	
	@Test
	public void testFileList()
	{
		
		System.setProperty("external.config.dir", "D:\\Programmierung\\MyGISfx\\webgisserver\\config");
		
		MyDirectoryConfigSourceProvider csp = new MyDirectoryConfigSourceProvider();
		for(ConfigSource cs: csp.getConfigSources(null))
		{
			System.out.println(cs.getName());
			for(Entry<String, String> entry: cs.getProperties().entrySet())
			{
				System.out.println(entry.getKey() + " - " + entry.getValue());
			}
			
		}
	}
	
}
