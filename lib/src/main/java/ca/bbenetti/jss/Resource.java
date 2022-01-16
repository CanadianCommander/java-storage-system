package ca.bbenetti.jss;

import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;
import java.util.UUID;

public interface Resource
{
	// ==========================================================================
	// Public Methods
	// ==========================================================================


	// ==========================================================================
	// Getters
	// ==========================================================================

	/**
	 * get the id of this resource
	 * @return - the id
	 */
	public UUID getId();

	/**
	 * get the name of this resource (in most cases filename).
	 * @return - the name
	 */
	public String getName();

	/**
	 * get a data channel that can be used to read this resource
	 * @return - a data channel targeting this resource.
	 */
	public ReadableByteChannel getData();

	/**
	 * Get a URI at which the underlying resource can be found.
	 * @return - a URI indicating the location of the resource.
	 */
	public Optional<URI> getURI();
}
