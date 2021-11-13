package config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.config.common.AbstractConfigSource;
import io.smallrye.config.common.utils.ConfigSourceUtil;

public class MyDirectoryConfigSource extends AbstractConfigSource
{
	
	private final static String CONFIG_FILE_DIRECTORY = System.getProperty("external.config.dir");
	private final Map<String, Properties> properties = new HashMap<String, Properties>();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MyDirectoryConfigSource.class);
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	public MyDirectoryConfigSource()
	{
		super(CONFIG_FILE_DIRECTORY, 1000);
		
		this.scanFolder();
		
		// loadProperties();
		// final File file = new File(CONFIG_FILE_DIRECTORY);
		
		// try
		// { logger.info("reload config properties from {} ", CONFIG_FILE_DIRECTORY);
		// Map<String,String> before = new HashMap<String,String>(MyDirectoryConfigSource.this.properties);
		//
		// loadProperties();
		//
		// Map<String,String> after = new HashMap<String,String>(MyDirectoryConfigSource.this.properties);
		// ChangeEventNotifier.getInstance().detectChangesAndFire(before, after,MyDirectoryConfigSource.this.getName());
		// }
		// catch (Exception e) {
		// logger.error(e.getLocalizedMessage(), e);
		// }
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				MyDirectoryConfigSource.this.executorService.shutdown();
			}
		}));
		this.executorService.execute(new Runnable()
		{
			
			@Override
			public void run()
			{
				try (WatchService watcher = FileSystems.getDefault().newWatchService();)
				{
					Path path = Paths.get(CONFIG_FILE_DIRECTORY);
					path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
					
					WatchKey key;
					while((key = watcher.take()) != null)
					{
						for(WatchEvent<?> event: key.pollEvents())
						{
							String fileName = ((Path) event.context()).toString();
							System.out.println("event: " + event.context() + " " + event.kind());
							if(event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
							{
								// TODO path global file path
								// path.
								
								// ((Path) event.context()).toAbsolutePath()
								Properties p = MyDirectoryConfigSource.this.loadProperties(path.resolve((Path) event.context()));
								MyDirectoryConfigSource.this.properties.put(fileName, p);
							}
							else if(event.kind() == StandardWatchEventKinds.ENTRY_MODIFY)
							{
								Properties p = MyDirectoryConfigSource.this.loadProperties(path.resolve((Path) event.context()));
								MyDirectoryConfigSource.this.properties.put(fileName, p);
							}
							else if(event.kind() == StandardWatchEventKinds.ENTRY_DELETE)
							{
								MyDirectoryConfigSource.this.properties.remove(fileName);
							}
							
							System.out.println("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");
						}
						key.reset();
					}
					
				}
				catch(Exception e1)
				{
					LOGGER.error("error while watching folder", e1);
				}
			}
		});
		
	}
	
	public static List<String> fileList(String directory)
	{
		List<String> fileNames = new ArrayList<>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory)))
		{
			for(Path path: directoryStream)
			{
				fileNames.add(path.toString());
			}
		}
		catch(IOException ex)
		{
		}
		return fileNames;
	}
	
	private void scanFolder()
	{
		
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(CONFIG_FILE_DIRECTORY)))
		{
			for(Path path: directoryStream)
			{
				// fileNames.add(path.toString());
				System.out.println("found " + path);
				Properties p = this.loadProperties(path);
				this.properties.put(path.getFileName().toString(), p);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private Properties loadProperties(Path path)
	{
		System.out.println("loadProperties " + path);
		Properties p = new Properties();
		try (InputStream in = Files.newInputStream(path))
		{
			p.load(in);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return p;
		// this.properties.putAll(ConfigSourceUtil.propertiesToMap(p));
		
	}
	
	@Override
	public Map<String, String> getProperties()
	{
		Map<String, String> map = new HashMap<String, String>();
		
		for(Entry<String, Properties> entry: this.properties.entrySet())
		{
			
			map.putAll(ConfigSourceUtil.propertiesToMap(entry.getValue()));
		}
		return map;
	}
	
	@Override
	public Set<String> getPropertyNames()
	{
		Set<String> keys = new HashSet<String>();
		for(Entry<String, Properties> entry: this.properties.entrySet())
		{
			keys.addAll(new HashSet(entry.getValue().keySet()));
		}
		
		return keys;
	}
	
	@Override
	public String getValue(String propertyName)
	{
		
		for(Entry<String, Properties> entry: this.properties.entrySet())
		{
			if(entry.getValue().containsKey(propertyName))
			{
				return entry.getValue().getProperty(propertyName);
			}
		}
		return null;
	}
	
}
