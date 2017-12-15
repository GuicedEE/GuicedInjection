package za.co.mmagon.guiceinjection.scanners;

import io.github.lukehutch.fastclasspathscanner.matchprocessor.FileMatchContentsProcessorWithContext;
import za.co.mmagon.guiceinjection.GuiceContext;
import za.co.mmagon.guiceinjection.enumerations.FastAccessFileTypes;
import za.co.mmagon.guiceinjection.interfaces.FileContentsScanner;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class FastFileTypesScanner implements FileContentsScanner
{
	private static final Logger log = Logger.getLogger("FastFileTypesScanner");

	@Override
	public Map<String, FileMatchContentsProcessorWithContext> onMatch()
	{
		Map<String, FileMatchContentsProcessorWithContext> map = new HashMap<>();

		for (FastAccessFileTypes type : FastAccessFileTypes.values())
		{
			log.config("Fast file type " + type.name() + " loading... ");
			GuiceContext.getFastAccessFiles().computeIfAbsent(type, k -> new LinkedHashMap<>());
			FileMatchContentsProcessorWithContext processor = (classpathElt, relativePath, fileContents) ->
			{
				String idName = relativePath.substring(0, relativePath.lastIndexOf('.'));
				Map<String, byte[]> fastFiles = GuiceContext.getFastAccessFiles().get(type);
				fastFiles.put(idName, fileContents);
				log.config(type.name() + " File Loaded : " + idName);
			};
			map.put(type.getEndsWith(), processor);
		}
		return map;
	}
}
