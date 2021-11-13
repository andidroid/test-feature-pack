package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.config.common.AbstractConfigSource;
import io.smallrye.config.common.utils.ConfigSourceUtil;
import io.smallrye.config.events.ChangeEventNotifier;

public class MyFileConfigSource extends AbstractConfigSource
{
	
	private final static String CONFIG_FILE_NAME = System.getProperty("external.config.file");
	private final Map<String, String> properties = new HashMap<String, String>();
	
	private static final Logger logger = LoggerFactory.getLogger(MyFileConfigSource.class);
	
	long lastModified = 0l;
	
	public MyFileConfigSource(String fileName)
	{
		super(fileName, DEFAULT_ORDINAL);
		try
		{
			this.loadProperties();
		}
		catch(Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public MyFileConfigSource()
	{
		super(CONFIG_FILE_NAME, DEFAULT_ORDINAL);
		
		try
		{
			this.loadProperties();
		}
		catch(Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		final File file = new File(this.getName());
		this.lastModified = file.lastModified();
		java.util.TimerTask t = new java.util.TimerTask()
		{
			
			@Override
			public void run()
			{
				try
				{
					long oldLastModified = MyFileConfigSource.this.lastModified;
					MyFileConfigSource.this.lastModified = file.lastModified();
					
					if(MyFileConfigSource.this.lastModified != oldLastModified)
					{
						MyFileConfigSource.this.reloadAndFireEvents();
						
					}
				}
				catch(Exception e)
				{
					logger.error(e.getLocalizedMessage(), e);
				}
				
			}
			
		};
		
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(t, 60_000, 60_000);
		
		// Path path = new File(CONFIG_FILE_NAME).toPath();
		//
		// try {
		// WatchService watcher = FileSystems.getDefault().newWatchService();
		// path.register(watcher, java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY);
		// while(true)
		// {
		// try {
		// WatchKey key = watcher.poll(1, TimeUnit.MINUTES);
		// if(key.pollEvents().isEmpty())
		// {
		// continue;
		// }
		// else
		// {
		// System.out.println("reload properties from " + CONFIG_FILE_NAME);
		// loadProperties();
		// }
		//
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		
	}
	
	public void reloadAndFireEvents() throws Exception
	{
		logger.info("reload config properties from {} ", this.getName());
		Map<String, String> before = new HashMap<String, String>(MyFileConfigSource.this.properties);
		this.loadProperties();
		Map<String, String> after = new HashMap<String, String>(MyFileConfigSource.this.properties);
		ChangeEventNotifier.getInstance().detectChangesAndFire(before, after, MyFileConfigSource.this.getName());
	}
	
	private Properties loadPropertiesFile()
	{
		Properties p = new Properties();
		try (InputStream in = new FileInputStream(new File(this.getName())))
		{
			p.load(in);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return p;
	}
	
	private Properties loadXmlFile() throws Exception
	{
		return XmlConfigLoader.load(new File(this.getName()));
	}
	
	private void loadProperties() throws Exception
	{
		Properties p = null;
		if(this.getName().endsWith(".xml"))
		{
			p = this.loadXmlFile();
		}
		else
		{
			p = this.loadPropertiesFile();
		}
		
		this.properties.clear();
		this.properties.putAll(ConfigSourceUtil.propertiesToMap(p));
		
	}
	
	@Override
	public Map<String, String> getProperties()
	{
		return this.properties;
	}
	
	@Override
	public Set<String> getPropertyNames()
	{
		return this.properties.keySet();
	}
	
	@Override
	public String getValue(String propertyName)
	{
		return this.properties.get(propertyName);
	}
	
}
