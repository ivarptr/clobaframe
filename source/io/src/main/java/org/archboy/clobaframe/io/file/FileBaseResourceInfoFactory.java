package org.archboy.clobaframe.io.file;

import java.io.File;
import java.io.IOException;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.TemporaryResources;

/**
 *
 * @author yang
 */
public interface FileBaseResourceInfoFactory {
	
	FileBaseResourceInfo make(File file);
	
	FileBaseResourceInfo wrap(ResourceInfo resourceInfo, TemporaryResources temporaryResources) throws IOException;
	
}