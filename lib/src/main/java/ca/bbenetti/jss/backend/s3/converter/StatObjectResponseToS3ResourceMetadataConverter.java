package ca.bbenetti.jss.backend.s3.converter;

import ca.bbenetti.jss.backend.s3.S3StorageProvider;
import ca.bbenetti.jss.backend.s3.exception.S3MetadataDecodingException;
import ca.bbenetti.jss.backend.s3.model.S3ResourceMetadata;
import ca.bbenetti.jss.converter.Converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.StatObjectResponse;

import java.util.stream.Collectors;

public class StatObjectResponseToS3ResourceMetadataConverter implements Converter<StatObjectResponse, S3ResourceMetadata>
{
	// ==========================================================================
	// Converter Implementation
	// ==========================================================================

	@Override
	public S3ResourceMetadata convert(StatObjectResponse from)
	{
		try
		{
			return (new ObjectMapper()).readValue(from.userMetadata().get(S3StorageProvider.S3_RESOURCE_METADATA_KEY), S3ResourceMetadata.class);
		}
		catch (JsonProcessingException e)
		{
			throw new S3MetadataDecodingException(String.format("Failed to decode metadata for S3 object [%s] with error %s", from.object(), e.getMessage()), e);
		}
	}
}
