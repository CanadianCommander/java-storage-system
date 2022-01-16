package ca.bbenetti.jss.model;

import ca.bbenetti.jss.Resource;

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

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	public BinaryResource(String name, byte[] data)
	{
		this(UUID.randomUUID(), name, data, false);
	}

	public BinaryResource(UUID id, String name, byte[] data)
	{
		this(id, name, data, false);
	}

	public BinaryResource(UUID id, String name, byte[] data, boolean publicResource)
	{
		this.id = id;
		this.name = name;
		this.data = data;
		this.publicResource = publicResource;
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
