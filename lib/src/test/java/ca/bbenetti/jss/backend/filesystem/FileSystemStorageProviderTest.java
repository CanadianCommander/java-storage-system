package ca.bbenetti.jss.backend.filesystem;

import ca.bbenetti.jss.Resource;
import ca.bbenetti.jss.backend.filesystem.model.FileMetaData;
import ca.bbenetti.jss.model.BinaryResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.UUID;

public class FileSystemStorageProviderTest
{
	protected FileSystemStorageProvider fileSystemStorageProvider;
	protected final Path FILE_SYSTEM_STORAGE_PATH = Path.of("/opt/storage");

	@Before
	public void init()
	{
		this.fileSystemStorageProvider = new FileSystemStorageProvider(this.FILE_SYSTEM_STORAGE_PATH);
	}

	// ==========================================================================
	// Tests
	// ==========================================================================

	@Test
	public void canCheckThatResourceExists() throws IOException
	{
		String veryImportantMessage = "Hello World";
		UUID resourceId = UUID.randomUUID();
		Path testFilePath = FsUtil.resourceIdToPath(FILE_SYSTEM_STORAGE_PATH, resourceId);

		Assert.assertFalse("Expect resource to NOT be found", this.fileSystemStorageProvider.resourceExists(resourceId));

		Files.createDirectories(testFilePath.getParent());
		Files.createFile(testFilePath);
		Files.write(testFilePath, veryImportantMessage.getBytes(StandardCharsets.UTF_8));

		Assert.assertTrue("Expect resource to be found", this.fileSystemStorageProvider.resourceExists(resourceId));
	}

	@Test
	public void canWriteResource() throws IOException
	{
		int testByteCount = 1024*1024; // ~ 1MB
		byte[] testBytes = new byte[testByteCount];
		(new SecureRandom()).nextBytes(testBytes); // generate random bytes
		String name = "Glorp";
		UUID resourceId = UUID.randomUUID();
		Path testFilePath = FsUtil.resourceIdToPath(this.FILE_SYSTEM_STORAGE_PATH, resourceId);
		Path testMetaPath = Path.of(testFilePath.toString() + FileSystemStorageProvider.META_DATA_FILE_EXT);
		BinaryResource testResource = new BinaryResource(resourceId, name, testBytes);

		this.fileSystemStorageProvider.saveResource(testResource);

		Assert.assertArrayEquals("Expect message to be written to disk", testBytes, Files.readAllBytes(testFilePath));
		Assert.assertEquals("Expect meta data file to contain \"name\"", name, (new ObjectMapper()).readValue(testMetaPath.toFile(), FileMetaData.class).getResourceName());
	}

	@Test
	public void canReadResource() throws IOException
	{
		int testByteCount = 1024*1024; // ~ 1MB
		byte[] testBytes = new byte[testByteCount];
		(new SecureRandom()).nextBytes(testBytes); // generate random bytes
		String name = "Bob The Builder";
		UUID resourceId = UUID.randomUUID();
		Path testFilePath = FsUtil.resourceIdToPath(this.FILE_SYSTEM_STORAGE_PATH, resourceId);
		Path testMetaPath = Path.of(testFilePath.toString() + FileSystemStorageProvider.META_DATA_FILE_EXT);

		// write test file
		Files.createDirectories(testFilePath.getParent());
		Files.write(testFilePath, testBytes, StandardOpenOption.CREATE);
		(new ObjectMapper()).writeValue(testMetaPath.toFile(), new FileMetaData(name, MediaType.OCTET_STREAM.toString()));

		// read data back
		ByteBuffer byteBuffer = ByteBuffer.allocate(testByteCount);
		Resource resource = this.fileSystemStorageProvider.getResource(resourceId);
		resource.getData().read(byteBuffer);

		Assert.assertArrayEquals("Expect file content to be read from disk correctly", testBytes, byteBuffer.array());
		Assert.assertEquals("Expect resource name to be read back correctly", name, resource.getName());
	}

	@Test
	public void mediaTypeIsPreserved()
	{
		String resourceName = "TestResource";
		String testFileContent = "<h1>HELLO WORLD</h1>";
		BinaryResource testResource = new BinaryResource(resourceName, testFileContent.getBytes(StandardCharsets.UTF_8), MediaType.HTML_UTF_8.toString());

		this.fileSystemStorageProvider.saveResource(testResource);

		Resource fileResource = this.fileSystemStorageProvider.getResource(testResource.getId());
		Assert.assertEquals("Expect media type to be html", MediaType.HTML_UTF_8.toString(), fileResource.getMediaType());
	}
}
