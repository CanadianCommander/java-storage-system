package ca.bbenetti.jss.backend.s3.exception;

public class S3MetadataDecodingException extends S3Exception
{
	public S3MetadataDecodingException()
	{
	}

	public S3MetadataDecodingException(String message)
	{
		super(message);
	}

	public S3MetadataDecodingException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
