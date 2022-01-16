package ca.bbenetti.jss.backend.filesystem.exception;

import ca.bbenetti.jss.exception.StorageBackendException;

public class FileIOException extends StorageBackendException
{
	public FileIOException()
	{
	}

	public FileIOException(String message)
	{
		super(message);
	}

	public FileIOException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
