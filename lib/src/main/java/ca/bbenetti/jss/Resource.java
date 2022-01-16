package ca.bbenetti.jss;

import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.nio.channels.ReadableByteChannel;
import java.time.ZonedDateTime;
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
	 * is this resource publicly accessible or not? (access over the internet without credentials?)
	 * @return - indicates if resource is public or not
	 */
	default boolean isPublic()
	{
		return false;
	}

	/**
	 * Get the permanent URI at which the underlying resource can be found if possible.
	 * @return - a URI indicating the location of the resource.
	 */
	default Optional<URI> getPermanentURI()
	{
		return Optional.empty();
	}

	/**
	 * Get a temporary URI at which the underlying resource can be found if possible.
	 * @param expiryDateTime - time at which the URI will expire.
	 * @return - a temporary URI
	 */
	default Optional<URI> getTemporaryURI(ZonedDateTime expiryDateTime)
	{
		return Optional.empty();
	}
}
