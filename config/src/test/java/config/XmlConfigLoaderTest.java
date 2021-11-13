package config;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.Test;

public class XmlConfigLoaderTest {

	@Test
	public void test() {
		try {
			
			Path workingDir = Path.of("", "src/test/resources");
			Path file = workingDir.resolve("config/configs.xml");
	        String content = Files.readString(file);
			System.out.println(content);
			//System.out.println(getClass().getClassLoader().getResource("src/test/resources/config/configs.xml"));
			
			Properties p = XmlConfigLoader.load(file.toFile());
			System.out.println(p);
			assertEquals(1, p.size());
			assertEquals("123", p.get("test"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

}
