package ca.bbenetti.jss.backend.filesystem;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.util.UUID;


public class FsUtilTest
{

	protected final String STORAGE_ROOT = "/opt/source";

	// ==========================================================================
	// Tests
	// ==========================================================================

	@Test
	public void canConvertResourceIdToPath()
	{
		UUID resourceId = UUID.fromString("daf6f760-43fb-4c11-97d0-7790fb99cdeb");
		Path expectedPath = Path.of(STORAGE_ROOT, "da", "f6", "daf6f760-43fb-4c11-97d0-7790fb99cdeb");

		Path resultPath = FsUtil.resourceIdToPath(STORAGE_ROOT, resourceId);

		Assert.assertEquals("Computed path should match expected path", expectedPath.toString(), resultPath.toString());
	}

	@Test
	public void canConvertPathToResourceId()
	{
		UUID expectedResourceId = UUID.fromString("daf6f760-43fb-4c11-97d0-7790fb99cdeb");
		Path resourcePath = Path.of(STORAGE_ROOT, "da", "f6", "daf6f760-43fb-4c11-97d0-7790fb99cdeb");

		UUID resultId = FsUtil.pathToResourceId(resourcePath);

		Assert.assertEquals("Computed id should match expected id", expectedResourceId.toString(), resultId.toString());
	}
}
