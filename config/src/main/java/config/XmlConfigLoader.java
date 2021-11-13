package config;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XmlConfigLoader {

	public static Properties load(URL url) throws Exception
	{
		SAXReader reader = new SAXReader();
        Document document = reader.read(url);
        return parse(document);
	}
	public static Properties load(File file) throws Exception
	{
		SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        return parse(document);
	}
	
	public static Properties parse(Document document) throws Exception
	{
        Element root = document.getRootElement();
        Properties properties = new Properties();
        Iterator<Element> it = root.elementIterator("property");
        while ( it.hasNext()) {
            Element element = it.next();
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.put(name, value);
        }
        return properties;
        
	}
	
	
	
}
