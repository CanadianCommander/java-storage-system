package ca.bbenetti.jss.backend.filesystem;

import ca.bbenetti.jss.Resource;
import ca.bbenetti.jss.backend.filesystem.exception.FileIOException;
import ca.bbenetti.jss.backend.filesystem.model.FileMetaData;
import com.google.common.net.MediaType;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public class FileResource implements Resource
{
	protected final UUID id;
	protected final Path filePath;
	protected FileMetaData fileMetaData;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	public FileResource(UUID id, Path filePath, FileMetaData fileMetaData)
	{
		this.id = id;
		this.filePath = filePath;
		this.fileMetaData = fileMetaData;
	}

	// ==========================================================================
	// Resource Implementation
	// ==========================================================================

	@Override
	public UUID getId()
	{
		return this.id;
	}

	@Override
	public String getName()
	{

		return this.fileMetaData.getResourceName();
	}

	@Override
	public String getMediaType()
	{
		return this.fileMetaData.getMediaType();
	}

	@Override
	public ReadableByteChannel getData()
	{
		try
		{
			return FileChannel.open(this.filePath);
		}
		catch (IOException e)
		{
			throw new FileIOException("Failed to open file for reading with error: " + e.getMessage(), e);
		}
	}

	@Override
	public Optional<URI> getPermanentURI()
	{
		return Optional.of(this.filePath.toUri());
	}
}
