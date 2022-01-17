package ca.bbenetti.jss.backend.s3.converter;

import ca.bbenetti.jss.Resource;
import ca.bbenetti.jss.backend.s3.model.S3ResourceMetadata;
import ca.bbenetti.jss.converter.Converter;

public class ResourceToS3ResourceMetadataConverter implements Converter<Resource, S3ResourceMetadata>
{
	// ==========================================================================
	// Converter Implementation
	// ==========================================================================

	@Override
	public S3ResourceMetadata convert(Resource from)
	{
		return new S3ResourceMetadata(from.getName(), from.getMediaType());
	}
}
