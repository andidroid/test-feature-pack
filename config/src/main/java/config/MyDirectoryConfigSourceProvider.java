package config;

import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyDirectoryConfigSourceProvider implements ConfigSourceProvider
{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MyDirectoryConfigSource.class);
	
	private static final String CONFIG_FILE_DIRECTORY = System.getProperty("external.config.dir");
	
	private Map<String, ConfigSource> configSources = new HashMap<String, ConfigSource>();
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	public MyDirectoryConfigSourceProvider()
	{
		this.scanFolder();
		this.watchFolder();
	}
	
	private void watchFolder()
	{
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				MyDirectoryConfigSourceProvider.this.executorService.shutdown();
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
							String fileName = ((Path) event.context()).getFileName().toString();
							Path resolved = path.resolve((Path) event.context());
							LOGGER.debug("file event - kind: {}, affected file: {}", event.context(), event.kind());
							System.out.println(event.context());
							System.out.println(fileName);
							System.out.println(resolved.toString());
							System.out.println(resolved.getFileName().toString());
							
							String resolvedFile = resolved.toString();
							
							if(event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
							{
								MyFileConfigSource cs = new MyFileConfigSource(resolvedFile);
								MyDirectoryConfigSourceProvider.this.configSources.put(path.getFileName().toString(), cs);
							}
							else if(event.kind() == StandardWatchEventKinds.ENTRY_MODIFY)
							{
								((MyFileConfigSource) MyDirectoryConfigSourceProvider.this.configSources.get(fileName)).reloadAndFireEvents();
							}
							else if(event.kind() == StandardWatchEventKinds.ENTRY_DELETE)
							{
								MyDirectoryConfigSourceProvider.this.configSources.remove(fileName);
							}
							
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
	
	@Override
	public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader)
	{
		return this.configSources.values();
	}
	
	private void scanFolder()
	{
		
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(CONFIG_FILE_DIRECTORY)))
		{
			for(Path path: directoryStream)
			{
				// System.out.println("found " + path.getFileName().toString()); -> test.properties
				// System.out.println("found " + path.toString()); -> D:\Programmierung\MyGISfx\webgisserver\config\test.properties
				
				ConfigSource cs = new MyFileConfigSource(path.toString());
				
				this.configSources.put(path.getFileName().toString(), cs);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**************************************** Klassenvariablen ****************************************/
	
	/**************************************** Instanzvariablen ****************************************/
	
	/**************************************** Konstruktoren *******************************************/
	
	/**************************************** Instanzmethoden *****************************************/
	
	/**************************************** Getter und Setter ***************************************/
	
	/**************************************** überschriebene Methoden *********************************/
	
	/**************************************** Klassenmethoden *****************************************/
	
}
