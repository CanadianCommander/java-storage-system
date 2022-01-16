package ca.bbenetti.jss.backend.s3;

import java.nio.file.Path;
import java.util.UUID;

public class S3Util
{
	// ==========================================================================
	// Class Methods
	// ==========================================================================

	public static String resourceIdToS3Path(String basePath, UUID id)
	{
		return Path.of(basePath, id.toString().substring(0, 2), id.toString().substring(2, 4), id.toString()).toString();
	}

}
