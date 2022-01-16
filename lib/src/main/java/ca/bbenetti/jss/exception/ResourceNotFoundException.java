package ca.bbenetti.jss.exception;

public class ResourceNotFoundException extends StorageServiceException
{
	public ResourceNotFoundException()
	{
	}

	public ResourceNotFoundException(String message)
	{
		super(message);
	}

	public ResourceNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
