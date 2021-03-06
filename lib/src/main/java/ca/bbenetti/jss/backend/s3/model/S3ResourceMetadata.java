package ca.bbenetti.jss.backend.s3.model;

import com.google.common.net.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class S3ResourceMetadata
{
	protected String name;
	protected String mediaType;
}
