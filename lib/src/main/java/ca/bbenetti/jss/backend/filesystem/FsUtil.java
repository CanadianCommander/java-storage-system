package ca.bbenetti.jss.backend.filesystem;

import java.nio.file.Path;
import java.util.UUID;

public class FsUtil
{

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * generate a file system path based on the resource id
	 * @param basePath - base path under which resources are stored
	 * @param id - the resource id to generate the path for
	 * @return - the resource id path
	 */
	public static Path resourceIdToPath(Path basePath, UUID id)
	{
		return resourceIdToPath(basePath.toString(), id);
	}

	/**
	 * generate a file system path based on the resource id
	 * @param basePath - base path under which resources are stored
	 * @param id - the resource id to generate the path for
	 * @return - the resource id path
	 */
	public static Path resourceIdToPath(String basePath, UUID id)
	{
		return Path.of(basePath, id.toString().substring(0, 2), id.toString().substring(2, 4), id.toString());
	}

	/**
	 * get the resource id based on the file system path
	 * @param path - the file system path
	 * @return - the resource id
	 */
	public static UUID pathToResourceId(Path path)
	{
		return UUID.fromString(path.getParent().getParent().getFileName().toString() +
		                       path.getParent().getFileName().toString() +
		                       path.getFileName().toString().substring(4));
	}
}
