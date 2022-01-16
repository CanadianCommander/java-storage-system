package ca.bbenetti.jss.backend.s3;

import ca.bbenetti.jss.Resource;
import ca.bbenetti.jss.StorageProvider;
import ca.bbenetti.jss.backend.s3.exception.S3DeleteException;
import ca.bbenetti.jss.backend.s3.exception.S3UploadException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class S3StorageProvider implements StorageProvider
{
	protected final int S3_PART_SIZE = 5242880; // 5MB
	String storageBucket = null;
	String storagePathBase = null;
	MinioClient minioClient = null;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	public S3StorageProvider(URL s3Url, String accessKey, String secretKey, String storageBucket, String storagePathBase)
	{
		this.minioClient = MinioClient.builder()
		                              .endpoint(s3Url)
		                              .credentials(accessKey, secretKey)
		                              .build();
		this.storageBucket = storageBucket;
		this.storagePathBase = storagePathBase;
	}

	// ==========================================================================
	// StorageProvider Implementation
	// ==========================================================================

	@Override
	public Resource getResource(UUID resourceId)
	{
		return null;
	}

	@Override
	public boolean resourceExists(UUID resourceId)
	{
		return false;
	}

	@Override
	public Resource saveResource(Resource resource)
	{
		this.writeResourceToS3(resource);
		return this.getResource(resource.getId());
	}

	@Override
	public void deleteResource(UUID resourceId)
	{
		this.deleteResourceFromS3(resourceId);
	}

	// ==========================================================================
	// Protected Methods
	// ==========================================================================

	/**
	 * write (upload) a resource to s3 storage
	 * @param resource - the resource to upload
	 */
	protected void writeResourceToS3(Resource resource)
	{
		try
		{
			this.minioClient.putObject(PutObjectArgs.builder()
			                                        .bucket(this.storageBucket)
			                                        .object(S3Util.resourceIdToS3Path(this.storagePathBase, resource.getId()))
			                                        .stream(Channels.newInputStream(resource.getData()), -1, S3_PART_SIZE)
			                                        .build());
		}
		catch (IOException | ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
				InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException e)
		{
			throw new S3UploadException(String.format("Failed to upload resource [%s] to S3 with error: %s", resource.getId().toString(), e.getMessage()), e);
		}
	}

	/**
	 * deletes the specified resource on S3 storage
	 * @param resourceId - the id of the resource to delete
	 */
	protected void deleteResourceFromS3(UUID resourceId)
	{
		try
		{
			this.minioClient.removeObject(RemoveObjectArgs.builder()
			                                              .bucket(this.storageBucket)
			                                              .object(S3Util.resourceIdToS3Path(this.storagePathBase, resourceId))
			                                              .build());
		}
		catch(IOException | ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
				InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException e)
		{
			throw new S3DeleteException(String.format("Failed to delete resource [%s] with error: %s", resourceId.toString(), e.getMessage()), e);
		}
	}

}
