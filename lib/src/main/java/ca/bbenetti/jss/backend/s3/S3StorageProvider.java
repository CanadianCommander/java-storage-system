package ca.bbenetti.jss.backend.s3;

import ca.bbenetti.jss.Resource;
import ca.bbenetti.jss.StorageProvider;
import ca.bbenetti.jss.backend.s3.converter.ResourceToS3ResourceMetadataConverter;
import ca.bbenetti.jss.backend.s3.converter.StatObjectResponseToS3ResourceMetadataConverter;
import ca.bbenetti.jss.backend.s3.exception.S3DeleteException;
import ca.bbenetti.jss.backend.s3.exception.S3ReadException;
import ca.bbenetti.jss.backend.s3.exception.S3UploadException;
import ca.bbenetti.jss.backend.s3.factory.S3ResourceFactory;
import ca.bbenetti.jss.backend.s3.model.S3ResourceMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

public class S3StorageProvider implements StorageProvider
{
	public static final String S3_RESOURCE_METADATA_KEY = "resource-metadata";
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
		return S3ResourceFactory.forRemoteResource(resourceId, this.getResourceMetadata(resourceId), this);
	}

	@Override
	public boolean resourceExists(UUID resourceId)
	{
		return this.doesResourceExistInS3(resourceId);
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
	// Public Methods
	// ==========================================================================

	/**
	 * write (upload) a resource to s3 storage
	 * @param resource - the resource to upload
	 */
	public void writeResourceToS3(Resource resource)
	{
		try
		{
			this.minioClient.putObject(PutObjectArgs.builder()
			                                        .bucket(this.storageBucket)
			                                        .object(S3Util.resourceIdToS3Path(this.storagePathBase, resource.getId()))
			                                        .stream(Channels.newInputStream(resource.getData()), -1, S3_PART_SIZE)
			                                        .contentType(resource.getMediaType())
			                                        .userMetadata(Map.of(S3_RESOURCE_METADATA_KEY, (new ObjectMapper()).writeValueAsString((new ResourceToS3ResourceMetadataConverter()).convert(resource))))
			                                        .build());
		}
		catch (IOException | ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
				InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException e)
		{
			throw new S3UploadException(String.format("Failed to upload resource [%s] to S3 with error: %s", resource.getId().toString(), e.getMessage()), e);
		}
	}

	/**
	 * read resource from S3 as input stream
	 * @param resourceId - the resource to read
	 * @return - an input stream which can be used to read the resources data.
	 */
	public InputStream readResourceFromS3AsInputStream(UUID resourceId)
	{
		try
		{
			return this.minioClient.getObject(GetObjectArgs.builder()
			                                               .bucket(this.storageBucket)
			                                               .object(S3Util.resourceIdToS3Path(this.storagePathBase, resourceId))
			                                               .build());
		}
		catch (IOException | ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
				InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException e)
		{
			throw new S3ReadException(String.format("Failed to read resource [%s] with error: %s", resourceId.toString(), e.getMessage()), e);
		}
	}

	/**
	 * gets a temporary URI from the S3 resource
	 * @param resourceId - the resource to get the temporary uri for
	 * @param expiryTime - the time after which the resource will be invalid
	 * @param allowedMethod - what methods are allowed on this url (upload / download)
	 * @return - the temporary uri
	 */
	public URI getS3ResourceUrl(UUID resourceId, ZonedDateTime expiryTime, Method allowedMethod)
	{
		try
		{
			var builder = GetPresignedObjectUrlArgs.builder()
			                                       .bucket(this.storageBucket)
			                                       .object(S3Util.resourceIdToS3Path(this.storagePathBase, resourceId))
			                                       .method(allowedMethod);
			if (expiryTime != null)
			{
				builder.expiry((int) Duration.between(ZonedDateTime.now(), expiryTime).getSeconds(), TimeUnit.SECONDS);
			}

			return URI.create(this.minioClient.getPresignedObjectUrl(builder.build()));
		}
		catch (IOException | ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
				InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException e)
		{
			throw new S3ReadException(String.format("Failed to read temporary URI for resource [%s] with error: %s", resourceId.toString(), e.getMessage()), e);
		}
	}

	/**
	 * get metadata for an S3 resource
	 * @param resourceId - the resource to get metadata for
	 * @return - the resources metadata.
	 */
	public S3ResourceMetadata getResourceMetadata(UUID resourceId)
	{
		return (new StatObjectResponseToS3ResourceMetadataConverter()).convert(this.statS3Resource(resourceId));
	}


	/**
	 * check if a resource exists in S3 storage
	 * @param resourceId - the resource to look for
	 * @return - true / false indicating if the resource is in S3 storage or not.
	 */
	public boolean doesResourceExistInS3(UUID resourceId)
	{
		return StreamSupport.stream(this.minioClient.listObjects(ListObjectsArgs.builder()
		                                            .bucket(this.storageBucket)
		                                            .prefix(S3Util.resourceIdToS3Path(this.storagePathBase, resourceId))
		                                            .build()).spliterator(), false).count() > 0;
	}

	/**
	 * deletes the specified resource on S3 storage
	 * @param resourceId - the id of the resource to delete
	 */
	public void deleteResourceFromS3(UUID resourceId)
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

	// ==========================================================================
	// Protected Methods
	// ==========================================================================

	/**
	 * pull statistics on a resource stored in S3
	 * @param resourceId - resourceId to get stats for
	 * @return - S3 object stats
	 */
	protected StatObjectResponse statS3Resource(UUID resourceId)
	{
		try
		{
			return this.minioClient.statObject(StatObjectArgs.builder()
			                                                 .bucket(this.storageBucket)
			                                                 .object(S3Util.resourceIdToS3Path(this.storagePathBase, resourceId))
			                                                 .build());
		}
		catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
				InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException e)
		{
			throw new S3ReadException(String.format("Failed to stat resource [%s] with error: %s", resourceId, e.getMessage()), e);
		}
	}

}
