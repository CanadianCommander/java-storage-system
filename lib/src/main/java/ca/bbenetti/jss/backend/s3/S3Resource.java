package ca.bbenetti.jss.backend.s3;

import ca.bbenetti.jss.Resource;
import ca.bbenetti.jss.backend.s3.model.S3ResourceMetadata;

import java.io.InputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class S3Resource implements Resource
{
	protected UUID id;
	protected S3ResourceMetadata metadata = null;
	protected Supplier<InputStream> inputStreamSupplier = null;
	protected Function<ZonedDateTime, URI> UriGenerationFunction = null;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * S3 resource
	 * @param id - id
	 * @param metadata - resource meta data
	 * @param inputStreamSupplier - a supplier function that produces an input stream targeting the S3 resource
	 */
	public S3Resource(UUID id,
	                  S3ResourceMetadata metadata,
	                  Supplier<InputStream> inputStreamSupplier,
	                  Function<ZonedDateTime, URI> UriGenerationFunction)
	{
		this.id = id;
		this.inputStreamSupplier = inputStreamSupplier;
		this.metadata = metadata;
		this.UriGenerationFunction = UriGenerationFunction;
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	@Override
	public UUID getId()
	{
		return this.id;
	}

	@Override
	public String getName()
	{
		return this.metadata.getName();
	}

	@Override
	public ReadableByteChannel getData()
	{
		return Channels.newChannel(this.inputStreamSupplier.get());
	}

	@Override
	public Optional<URI> getPermanentURI()
	{
		return Optional.ofNullable(this.UriGenerationFunction.apply(null));
	}

	@Override
	public Optional<URI> getTemporaryURI(ZonedDateTime expiryDateTime)
	{
		return Optional.ofNullable(this.UriGenerationFunction.apply(expiryDateTime));
	}
}
