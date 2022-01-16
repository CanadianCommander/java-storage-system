package ca.bbenetti.jss.backend.s3.exception;

import ca.bbenetti.jss.exception.StorageServiceException;

public class S3Exception extends StorageServiceException
{
	public S3Exception()
	{
	}

	public S3Exception(String message)
	{
		super(message);
	}

	public S3Exception(String message, Throwable cause)
	{
		super(message, cause);
	}
}
