package ca.bbenetti.jss.exception;

public class StorageServiceException extends RuntimeException
{
	public StorageServiceException()
	{
	}

	public StorageServiceException(String message)
	{
		super(message);
	}

	public StorageServiceException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
