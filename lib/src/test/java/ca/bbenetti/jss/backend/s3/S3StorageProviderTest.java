package ca.bbenetti.jss.backend.s3;

import ca.bbenetti.jss.model.BinaryResource;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

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
	public void canWriteReadResourceS3()
	{
		int testByteCount = 1024*1024; // ~ 1MB
		byte[] testBytes = new byte[testByteCount];
		(new SecureRandom()).nextBytes(testBytes); // generate random bytes
		BinaryResource testResource = new BinaryResource("TestResource",testBytes);

		try
		{
			this.storageProvider.saveResource(testResource);
		}
		finally
		{
			this.storageProvider.deleteResource(testResource.getId());
		}
	}
}
