package ca.bbenetti.jss.model;

import ca.bbenetti.jss.Resource;
import com.google.common.net.MediaType;

import javax.print.attribute.standard.Media;
import java.io.ByteArrayInputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.UUID;

public class BinaryResource implements Resource
{
	protected UUID id;
	protected String name;
	protected byte[] data;
	protected boolean publicResource = false;
	protected String mediaType;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	public BinaryResource(String name, byte[] data)
	{
		this(UUID.randomUUID(), name, data, MediaType.OCTET_STREAM.toString(), false);
	}

	public BinaryResource(String name, byte[] data, String mediaType)
	{
		this(UUID.randomUUID(), name, data, mediaType, false);
	}

	public BinaryResource(UUID id, String name, byte[] data)
	{
		this(id, name, data, MediaType.OCTET_STREAM.toString(), false);
	}

	public BinaryResource(UUID id, String name, byte[] data, String mediaType)
	{
		this(id, name, data, mediaType, false);
	}

	public BinaryResource(UUID id, String name, byte[] data, String mediaType, boolean publicResource)
	{
		this.id = id;
		this.name = name;
		this.data = data;
		this.publicResource = publicResource;
		this.mediaType = mediaType;
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
		return this.name;
	}

	@Override
	public String getMediaType()
	{
		return this.mediaType;
	}

	@Override
	public ReadableByteChannel getData()
	{
		return Channels.newChannel(new ByteArrayInputStream(this.data));
	}

	@Override
	public boolean isPublic()
	{
		return this.publicResource;
	}
}
