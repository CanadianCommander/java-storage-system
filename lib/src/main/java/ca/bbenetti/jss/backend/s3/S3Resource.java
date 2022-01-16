package ca.bbenetti.jss.backend.s3;

import ca.bbenetti.jss.Resource;

import java.nio.channels.ReadableByteChannel;
import java.util.UUID;

public class S3Resource implements Resource
{

	// ==========================================================================
	// Getters
	// ==========================================================================

	@Override
	public UUID getId()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return null;
	}

	@Override
	public ReadableByteChannel getData()
	{
		return null;
	}
}
