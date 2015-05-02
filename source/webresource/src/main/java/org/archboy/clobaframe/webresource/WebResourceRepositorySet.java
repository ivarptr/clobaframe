package org.archboy.clobaframe.webresource;

import java.util.Collection;

/**
 *
 * @author yang
 */
public interface WebResourceRepositorySet {

	Collection<WebResourceRepository> getResourceRepositories();
	
	/**
	 * 
	 * @param name Resource name includes the relative path, e.g. "css/main.css".
	 * @return NULL when can not find the specify resource.
	 */
	WebResourceInfo getByName(String name);

	/**
	 * 
	 * @return EMPTY when no resources.
	 */
	Collection<WebResourceInfo> getAll();
}
