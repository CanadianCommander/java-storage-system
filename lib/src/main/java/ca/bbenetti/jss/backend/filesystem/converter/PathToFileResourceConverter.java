package ca.bbenetti.jss.backend.filesystem.converter;

import ca.bbenetti.jss.backend.filesystem.FileResource;
import ca.bbenetti.jss.backend.filesystem.FsUtil;
import ca.bbenetti.jss.backend.filesystem.exception.FileIOException;
import ca.bbenetti.jss.backend.filesystem.model.FileMetaData;
import ca.bbenetti.jss.converter.Converter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;

import static ca.bbenetti.jss.backend.filesystem.FileSystemStorageProvider.META_DATA_FILE_EXT;

public class PathToFileResourceConverter implements Converter<Path, FileResource>
{
	// ==========================================================================
	// Converter Implementation
	// ==========================================================================

	@Override
	public FileResource convert(Path path)
	{
		try
		{
			Path metaPath = Path.of(path.toString() + META_DATA_FILE_EXT);
			return new FileResource(FsUtil.pathToResourceId(path), path, (new ObjectMapper()).readValue(metaPath.toFile(), FileMetaData.class));
		}
		catch (IOException e)
		{
			throw new FileIOException("Error when reading resource meta data file: " + e.toString(), e);
		}
	}
}
