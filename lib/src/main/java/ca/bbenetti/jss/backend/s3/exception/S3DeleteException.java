package ca.bbenetti.jss.backend.s3.exception;

public class S3DeleteException extends S3Exception
{
	public S3DeleteException()
	{
	}

	public S3DeleteException(String message)
	{
		super(message);
	}

	public S3DeleteException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
