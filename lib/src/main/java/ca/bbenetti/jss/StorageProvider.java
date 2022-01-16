package ca.bbenetti.jss;

import java.util.UUID;

public interface StorageProvider
{
	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * retrieve a resource for the underlying storage.
	 * @param resourceId - the id of the resource to fetch
	 * @return - the resource
	 * @throws ca.bbenetti.jss.exception.ResourceNotFoundException if not resource with the given id exists
	 * @throws ca.bbenetti.jss.exception.StorageBackendException if an error occurs with the storage backend
	 */
	public Resource getResource(UUID resourceId);

	/**
	 * check if a resource with the given id exists
	 * @param resourceId - the resource id to search for
	 * @return - true / false indicating if the resource exists or not.
	 * @throws ca.bbenetti.jss.exception.StorageBackendException if an error occurs with the storage backend
	 */
	public boolean resourceExists(UUID resourceId);

	/**
	 * save a resource to the underlying storage
	 * @param resource - the resource to save
	 * @return - the newly saved resource.
	 * @throws ca.bbenetti.jss.exception.StorageBackendException if an error occurs with the storage backend
	 */
	public Resource saveResource(Resource resource);
}
