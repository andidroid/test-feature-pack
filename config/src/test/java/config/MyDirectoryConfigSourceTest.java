package config;

import static org.junit.Assert.fail;

import org.junit.Test;

//import static org.junit.jupiter.api.Assertions.*;
//
//import org.junit.jupiter.api.Test;

public class MyDirectoryConfigSourceTest {

	@Test
	public void testFileList() {
		
		System.setProperty("external.config.dir", "D:\\Programmierung\\MyGISfx\\webgisserver\\config");
		
		MyDirectoryConfigSource cs = new MyDirectoryConfigSource();
	}

	@Test
	public void testGetPropertyNames() {
		fail("Not yet implemented");
	}

}
