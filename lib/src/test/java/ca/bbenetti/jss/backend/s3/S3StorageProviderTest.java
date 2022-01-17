package ca.bbenetti.jss.backend.s3;

import ca.bbenetti.jss.Resource;
import ca.bbenetti.jss.model.BinaryResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.ZonedDateTime;

public class S3StorageProviderTest
{
	protected final String S3_URL_ENV = "S3_URL";
	protected final String S3_ACCESS_KEY_ENV = "S3_ACCESS_KEY";
	protected final String S3_SECRET_KEY_ENV = "S3_SECRET_KEY";
	protected final String S3_BUCKET_ENV = "S3_BUCKET";

	protected final String TESTING_STORAGE_DIRECTORY = "jss/test/storage/";

	S3StorageProvider storageProvider = null;

	// ==========================================================================
	// Tests
	// ==========================================================================

	@Before
	public void init() throws MalformedURLException
	{
		this.storageProvider = new S3StorageProvider(new URL(System.getenv(S3_URL_ENV)),
		                                             System.getenv(S3_ACCESS_KEY_ENV),
		                                             System.getenv(S3_SECRET_KEY_ENV),
		                                             System.getenv(S3_BUCKET_ENV),
		                                             TESTING_STORAGE_DIRECTORY);
	}

	// ya I know. It's an integration test
	@Test
	public void canWriteReadResourceS3() throws IOException
	{
		String resourceName = "TestResource";
		int testByteCount = 1024 * 1024; // ~ 1MB
		byte[] testBytes = new byte[testByteCount];
		(new SecureRandom()).nextBytes(testBytes); // generate random bytes
		BinaryResource testResource = new BinaryResource(resourceName, testBytes);

		try
		{
			this.storageProvider.saveResource(testResource);

			Resource readFromS3 = this.storageProvider.getResource(testResource.getId());
			Assert.assertArrayEquals("Expect data read from S3 to be what was written", testBytes, readFromS3.getDataAsBytes());
			Assert.assertEquals("Expect resource name to be what was written to S3", resourceName, readFromS3.getName());
		}
		finally
		{
			this.storageProvider.deleteResource(testResource.getId());
		}
	}

	@Test
	public void canDetectThatResourceExistsInS3() throws IOException
	{
		int testByteCount = 1024 * 1024; // ~ 1MB
		byte[] testBytes = new byte[testByteCount];
		(new SecureRandom()).nextBytes(testBytes); // generate random bytes
		BinaryResource testResource = new BinaryResource("TestResource", testBytes);

		try
		{
			this.storageProvider.saveResource(testResource);

			Assert.assertTrue("Expect resource to exist in S3", this.storageProvider.resourceExists(testResource));
		}
		finally
		{
			this.storageProvider.deleteResource(testResource.getId());
		}
	}

	@Test
	public void canGenerateS3URI() throws IOException, InterruptedException
	{
		int testByteCount = 1024 * 1024; // ~ 1MB
		byte[] testBytes = new byte[testByteCount];
		(new SecureRandom()).nextBytes(testBytes); // generate random bytes
		BinaryResource testResource = new BinaryResource("TestResource", testBytes);
		int temporaryUrlExpiryTimeMs = 5000; // 5 seconds

		try
		{
			this.storageProvider.saveResource(testResource);

			Resource readFromS3 = this.storageProvider.getResource(testResource.getId());
			URL temporaryUrl = readFromS3.getTemporaryURI(ZonedDateTime.now().plusSeconds(temporaryUrlExpiryTimeMs/1000)).get().toURL();
			try (InputStream inputStream = temporaryUrl.openConnection().getInputStream())
			{
				Assert.assertArrayEquals("Expect to be able to download file from temporary URI",
				                         testBytes,
				                         inputStream.readAllBytes());
			}

			Thread.sleep(temporaryUrlExpiryTimeMs);

			Assert.assertThrows("Expect temporary URI to expire", IOException.class, () ->
			{
				try (InputStream inputStream = temporaryUrl.openConnection().getInputStream())
				{
					inputStream.readAllBytes();
				}
			});

			try (InputStream inputStream = readFromS3.getPermanentURI().get().toURL().openConnection().getInputStream())
			{
				Assert.assertArrayEquals("Expect to be able to download file from permanent URI",
				                         testBytes,
				                         inputStream.readAllBytes());
			}
		}
		finally
		{
			this.storageProvider.deleteResource(testResource.getId());
		}
	}
}
