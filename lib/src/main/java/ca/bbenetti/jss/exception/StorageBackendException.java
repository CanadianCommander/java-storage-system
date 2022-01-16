package ca.bbenetti.jss.exception;

public class StorageBackendException extends StorageServiceException
{
	public StorageBackendException()
	{
	}

	public StorageBackendException(String message)
	{
		super(message);
	}

	public StorageBackendException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
