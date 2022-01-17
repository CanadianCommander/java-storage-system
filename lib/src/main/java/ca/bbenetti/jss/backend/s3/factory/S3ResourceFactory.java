package ca.bbenetti.jss.backend.s3.factory;

import ca.bbenetti.jss.backend.s3.S3Resource;
import ca.bbenetti.jss.backend.s3.S3StorageProvider;
import ca.bbenetti.jss.backend.s3.model.S3ResourceMetadata;
import io.minio.http.Method;

import java.util.UUID;

public class S3ResourceFactory
{
	// ==========================================================================
	// Class Methods
	// ==========================================================================

	public static S3Resource forRemoteResource(UUID resourceId, S3ResourceMetadata metadata, S3StorageProvider s3StorageProvider)
	{
		return new S3Resource(resourceId,
		                      metadata,
		                      () -> s3StorageProvider.readResourceFromS3AsInputStream(resourceId),
		                      (expiryTime) -> s3StorageProvider.getS3ResourceUrl(resourceId, expiryTime, Method.GET));
	}

}
