package ca.bbenetti.jss.backend.filesystem;

import ca.bbenetti.jss.Resource;
import ca.bbenetti.jss.StorageProvider;
import ca.bbenetti.jss.backend.filesystem.converter.PathToFileResourceConverter;
import ca.bbenetti.jss.backend.filesystem.converter.ResourceToFileMetaDataConverter;
import ca.bbenetti.jss.backend.filesystem.exception.FileIOException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class FileSystemStorageProvider implements StorageProvider
{
	public static final String META_DATA_FILE_EXT = ".meta";
	protected static final int BYTE_BUFFER_SIZE = 1048576; // 1MB
	protected Path fsBasePath;

	public FileSystemStorageProvider(Path fsBasePath)
	{
		this.fsBasePath = fsBasePath;
	}

	// ==========================================================================
	// StorageProvider Implementation
	// ==========================================================================

	@Override
	public FileResource getResource(UUID resourceId)
	{
		return (new PathToFileResourceConverter()).convert(FsUtil.resourceIdToPath(this.fsBasePath.toString(), resourceId));
	}

	@Override
	public boolean resourceExists(UUID resourceId)
	{
		return FsUtil.resourceIdToPath(this.fsBasePath.toString(), resourceId).toFile().exists();
	}

	@Override
	public FileResource saveResource(Resource resource)
	{
		this.writeResourceToFile(resource);
		return this.getResource(resource.getId());
	}

	// ==========================================================================
	// Protected Methods
	// ==========================================================================

	/**
	 * write all data from a resource to disk
	 * @param resource - the resource to write
	 */
	protected void writeResourceToFile(Resource resource)
	{
		ObjectMapper objectMapper = new ObjectMapper();
		Path filePath = FsUtil.resourceIdToPath(this.fsBasePath.toString(), resource.getId());
		Path metaPath = Path.of(filePath.toString() + META_DATA_FILE_EXT);
		this.preparePathForWrite(filePath);

		try
		{
			this.writeChannelToFile(filePath, resource.getData());
			this.writeChannelToFile(metaPath, Channels.newChannel(
					new ByteArrayInputStream(
							objectMapper.writeValueAsBytes(
									(new ResourceToFileMetaDataConverter()).convert(resource)))));
		}
		catch (IOException e)
		{
			throw new FileIOException("Failed to write resource data to disk for resource [" + resource.getId() + "] with error: " + e.getMessage(), e);
		}
	}

	/**
	 * write the data in channel to a file
	 * @param filePath - the file to write the data to
	 * @param inputChannel - input channel containing the data to write
	 * @throws IOException - if an IO error occurs
	 */
	protected void writeChannelToFile(Path filePath, ReadableByteChannel inputChannel) throws IOException
	{
		try (FileChannel fileChannel = FileChannel.open(filePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		     inputChannel)
		{
			ByteBuffer byteBuffer = ByteBuffer.allocate(BYTE_BUFFER_SIZE);
			int totalBytesRead = 0;
			int bytesRead = 0;

			while ((bytesRead = inputChannel.read(byteBuffer)) > 0)
			{
				byteBuffer.flip();
				fileChannel.write(byteBuffer);
				byteBuffer.clear();
			}
		}
	}

	/**
	 * perform checks and created directories if required in preparation  for writing to a file
	 * @param filePath - the path to the file.
	 */
	protected void preparePathForWrite(Path filePath)
	{
		// ensure parent directories exist
		if (!filePath.toFile().getParentFile().mkdirs())
		{
			throw new FileIOException("One or more directories on path [" + filePath.toString() + "] could not be created. Check permissions");
		}
	}
}
