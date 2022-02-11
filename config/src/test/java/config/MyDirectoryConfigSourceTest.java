package config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MyDirectoryConfigSourceTest
{
	
	@Test
	public void testFileList()
	{
		
		System.setProperty("external.config.dir", "D:\\Programmierung\\MyGISfx\\webgisserver\\config");
		
		MyDirectoryConfigSource cs = new MyDirectoryConfigSource();
	}
	
	@Test
	@Disabled(value = "Not yet implemented")
	public void testGetPropertyNames()
	{
		fail("Not yet implemented");
	}
	
}
