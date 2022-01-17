package ca.bbenetti.jss.backend.s3.exception;

public class S3ReadException extends S3Exception
{
	public S3ReadException()
	{
	}

	public S3ReadException(String message)
	{
		super(message);
	}

	public S3ReadException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
