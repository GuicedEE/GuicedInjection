package za.co.mmagon.guiceinjection.scanners;

import com.oracle.jaxb21.Persistence;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.FileMatchContentsProcessorWithContext;
import za.co.mmagon.guiceinjection.GuiceContext;

import javax.xml.bind.JAXBContext;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class PersistenceFileHandler implements FileContentsScanner, PackageContentsScanner
{
	private static final Logger log = Logger.getLogger("PersistenceFileHandler");
	private static final Set<Persistence.PersistenceUnit> persistenceUnits = new HashSet<>();

	@Override
	public Map<String, FileMatchContentsProcessorWithContext> onMatch()
	{
		Map<String, FileMatchContentsProcessorWithContext> map = new HashMap<>();

		log.config("Persistence Units Loading... ");
		FileMatchContentsProcessorWithContext processor = (classpathElt, relativePath, fileContents) ->
		{
			log.config("Found " + relativePath + " - " + classpathElt.getCanonicalPath());
			if (!GuiceContext.getAsynchronousPersistenceFileLoaderExecutionService().isShutdown())
			{
				GuiceContext.getAsynchronousPersistenceFileLoaderExecutionService().shutdown();
				try
				{
					GuiceContext.getAsynchronousPersistenceFileLoaderExecutionService().awaitTermination(5, TimeUnit.SECONDS);
				}
				catch (InterruptedException e)
				{
					log.log(Level.SEVERE, "Unable to wait for persistence jaxb context to load..", e);
				}
			}
			persistenceUnits.addAll(getPersistenceUnitFromFile(fileContents));
		};
		map.put("persistence.xml", processor);
		return map;
	}

	/**
	 * Gets all the persistence files
	 *
	 * @param persistenceFile
	 *
	 * @return
	 */
	private Set<Persistence.PersistenceUnit> getPersistenceUnitFromFile(byte[] persistenceFile)
	{
		Set<Persistence.PersistenceUnit> units = new HashSet<>();
		JAXBContext pContext = GuiceContext.getPersistenceContext();
		String content = new String(persistenceFile);
		try
		{
			Persistence p = (Persistence) pContext.createUnmarshaller().unmarshal(new StringReader(content));
			for (Persistence.PersistenceUnit persistenceUnit : p.getPersistenceUnit())
			{
				units.add(persistenceUnit);
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Unable to get the persistence xsd object", e);
		}
		return units;
	}

	/**
	 * Returns all the persistence units that were found or loaded
	 *
	 * @return
	 */
	public static Set<Persistence.PersistenceUnit> getPersistenceUnits()
	{
		return persistenceUnits;
	}

	@Override
	public Set<String> searchFor()
	{
		return new HashSet<>();
	}
}
