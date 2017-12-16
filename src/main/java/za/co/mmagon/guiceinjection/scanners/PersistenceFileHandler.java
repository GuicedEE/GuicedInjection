package za.co.mmagon.guiceinjection.scanners;

import io.github.lukehutch.fastclasspathscanner.matchprocessor.FileMatchContentsProcessorWithContext;
import za.co.mmagon.guiceinjection.GuiceContext;
import za.co.mmagon.guiceinjection.enumerations.FastAccessFileTypes;
import za.co.mmagon.guiceinjection.interfaces.FileContentsScanner;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
@SuppressWarnings("unused")
public class PersistenceFileHandler implements FileContentsScanner
{
	private static final Logger log = Logger.getLogger("FastFileTypesScanner");

	@Override
	public Map<String, FileMatchContentsProcessorWithContext> onMatch()
	{
		Map<String, FileMatchContentsProcessorWithContext> map = new HashMap<>();
		GuiceContext.getFastAccessFiles().computeIfAbsent(FastAccessFileTypes.Persistence, k -> new LinkedHashMap<>());
		Map<String, byte[]> persistenceUnits = GuiceContext.getFastAccessFiles().get(FastAccessFileTypes.Persistence);

		log.config("Fast file type " + FastAccessFileTypes.Persistence.name() + " loading... ");
		FileMatchContentsProcessorWithContext processor = (classpathElt, relativePath, fileContents) ->
		{
			String idName = relativePath.substring(0, relativePath.lastIndexOf('.'));
			persistenceUnits.put(idName, fileContents);
			log.info(FastAccessFileTypes.Persistence.name() + " File Loaded : " + classpathElt.getCanonicalPath());
		};
		map.put(FastAccessFileTypes.Persistence.getEndsWith(), processor);

		return map;
	}
}
