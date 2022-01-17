package ca.bbenetti.jss.backend.filesystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileMetaData
{
	protected String resourceName;
	protected String mediaType;
}
