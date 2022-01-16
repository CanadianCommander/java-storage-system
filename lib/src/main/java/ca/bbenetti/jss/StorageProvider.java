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
	default boolean resourceExists(Resource resource) { return this.resourceExists(resource.getId()); }

	/**
	 * save a resource to the underlying storage
	 * @param resource - the resource to save
	 * @return - the newly saved resource.
	 * @throws ca.bbenetti.jss.exception.StorageBackendException if an error occurs with the storage backend
	 */
	public Resource saveResource(Resource resource);

	/**
	 * delete the resource with the given id
	 * @param resourceId - the id of the resource to delete
	 */
	public void deleteResource(UUID resourceId);
	default void deleteResource(Resource resource) { this.deleteResource(resource.getId()); }
}
