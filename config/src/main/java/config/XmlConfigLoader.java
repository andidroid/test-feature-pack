package config;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import config.xml.Property;
//import config.xml.Properties;
import jakarta.xml.bind.JAXBContext;

public class XmlConfigLoader {

    public static Properties load(File file) throws Exception {

        JAXBContext context = JAXBContext.newInstance(config.xml.Properties.class);
        config.xml.Properties config = (config.xml.Properties) context.createUnmarshaller()
                .unmarshal(new FileReader(file));

        Properties properties = new Properties();
        for (Property p : config.getProperties()) {
            properties.put(p.getName(), p.getValue());
        }

        return properties;
    }

}
