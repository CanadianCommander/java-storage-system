package ca.bbenetti.jss.backend.filesystem.converter;

import ca.bbenetti.jss.Resource;
import ca.bbenetti.jss.backend.filesystem.model.FileMetaData;
import ca.bbenetti.jss.converter.Converter;

public class ResourceToFileMetaDataConverter implements Converter<Resource, FileMetaData>
{
	// ==========================================================================
	// Converter Implementation
	// ==========================================================================

	@Override
	public FileMetaData convert(Resource from)
	{
		return new FileMetaData(from.getName(), from.getMediaType());
	}
}
