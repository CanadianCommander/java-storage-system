package ca.bbenetti.jss;

import com.google.common.net.MediaType;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.Channels;
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
	 *
	 * @return - the id
	 */
	public UUID getId();

	/**
	 * get the name of this resource (in most cases filename).
	 *
	 * @return - the name
	 */
	public String getName();

	/**
	 * get the mime type for this resource
	 * @return - the mime type of this resource
	 */
	default String getMediaType()
	{
		return MediaType.OCTET_STREAM.toString();
	}

	/**
	 * is this resource publicly accessible or not? (access over the internet without credentials?)
	 *
	 * @return - indicates if resource is public or not
	 */
	default boolean isPublic()
	{
		return false;
	}

	/**
	 * Get the permanent URI at which the underlying resource can be found if possible.
	 *
	 * @return - a URI indicating the location of the resource.
	 */
	default Optional<URI> getPermanentURI()
	{
		return Optional.empty();
	}

	/**
	 * Get a temporary URI at which the underlying resource can be found if possible.
	 *
	 * @param expiryDateTime - time at which the URI will expire.
	 * @return - a temporary URI
	 */
	default Optional<URI> getTemporaryURI(ZonedDateTime expiryDateTime)
	{
		return Optional.empty();
	}

	/**
	 * get a data channel that can be used to read this resource
	 *
	 * @return - a data channel targeting this resource.
	 */
	public ReadableByteChannel getData();

	/**
	 * get a data input stream that can be used to read this resource
	 * @return - a data input stream targeting this resource.
	 */
	default InputStream getDataAsInputStream()
	{
		return Channels.newInputStream(this.getData());
	}

	/**
	 * get data as byte array. Reads all data immediately
	 * @return - a byte array containing resource data
	 */
	default byte[] getDataAsBytes() throws IOException
	{
		try (InputStream inputStream = this.getDataAsInputStream())
		{
			return inputStream.readAllBytes();
		}
	}



}
