package ca.bbenetti.jss.backend.s3.exception;

public class S3UploadException extends S3Exception
{
	public S3UploadException()
	{
	}

	public S3UploadException(String message)
	{
		super(message);
	}

	public S3UploadException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
