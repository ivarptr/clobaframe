package org.archboy.clobaframe.webresource.impl;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.webresource.AbstractVersionStrategy;
import org.archboy.clobaframe.webresource.CacheableWebResource;
import org.archboy.clobaframe.webresource.CacheableWebResourceUpdateListener;
import org.archboy.clobaframe.webresource.ConcatenateWebResourceRepository;
import org.archboy.clobaframe.webresource.LocationGenerator;
import org.archboy.clobaframe.webresource.WebResourceCache;
import org.archboy.clobaframe.webresource.VersionStrategy;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author yang
 *
 */
@Named
public class WebResourceManagerImpl implements WebResourceManager {

	@Value("${clobaframe.webresource.versionStrategy}")
	private String versionStrategyName;
		
	@Inject
	private List<AbstractVersionStrategy> versionStrategys;

	// fields
	private VersionStrategy versionStrategy;

	private WebResourceCache webResourceCache;
	
	@Inject
	private ConcatenateWebResourceRepository concatenateResourceRepository;
	
	private List<String> textWebResourceMimeTypes; 

	@Value("${clobaframe.webresource.minify}")
	private boolean canMinify;
	
	@Value("${clobaframe.webresource.compress}")
	private boolean canCompress;
		
	@Value("${clobaframe.webresource.cache}")
	private boolean canCache;

	@Value("${clobaframe.webresource.baseLocation}")
	private String baseLocation;
	
	private LocationGenerator locationGenerator; 
	
	private static final int DEFAULT_CACHE_SECONDS = 5 * 60;
	private int cacheSeconds = DEFAULT_CACHE_SECONDS;
	
	private final Logger logger = LoggerFactory.getLogger(WebResourceManagerImpl.class);

	// to prevent infinite loop
	private Stack<String> buildingResourceNames = new Stack<String>();
	
	@PostConstruct
	public void init(){
		
		for(AbstractVersionStrategy strategy : versionStrategys) {
			if (strategy.getName().equals(versionStrategyName)) {
				this.versionStrategy = strategy;
				break;
			}
		}
		
		if (versionStrategy == null) {
			throw new IllegalArgumentException(String.format(
					"Can not find the version strategy [%s]", versionStrategyName));
		}
		
		logger.info("Using [{}] web resource version name strategy.", versionStrategyName);
		
		locationGenerator = new DefaultLocationGenerator(versionStrategy, baseLocation);
		webResourceCache = new DefaultWebResourceCache();
		
		textWebResourceMimeTypes = new ArrayList<String>();
		textWebResourceMimeTypes.add(MIME_TYPE_STYLE_SHEET);
		textWebResourceMimeTypes.addAll(MIME_TYPE_JAVA_SCRIPT);
	}

	/**
	 * 
	 * @param name
	 * @return NULL if the specify resource not found.
	 */
	private WebResourceInfo getResourceInternal(String name) {
		
		// load from collection first
		WebResourceInfo resourceInfo = webResourceCache.getByName(name);
		if (resourceInfo != null) {
			return resourceInfo;
		}
		
		// then load from composites and repository
		resourceInfo = concatenateResourceRepository.getByName(name);
		
		if (resourceInfo == null) {
			return null;
		}
		
		// wrap resource
		
		// to prevent infinite loop
		if (!buildingResourceNames.empty() && buildingResourceNames.contains(name)) {
			return null;
		}
		
		buildingResourceNames.push(name);
		
		Collection<String> childResourceNames = null;
		
		// transform url location
		if (resourceInfo.getMimeType().equals(MIME_TYPE_STYLE_SHEET)) {
			resourceInfo = new LocationTransformWebResourceInfo(this, resourceInfo);
			childResourceNames = ((LocationTransformWebResourceInfo)resourceInfo).getChildResourceNames();
		}
		
		// minify
		if (canMinify && textWebResourceMimeTypes.contains(resourceInfo.getMimeType())) {
			resourceInfo = new MinifyWebResourceInfo(resourceInfo);
		}
		
		// compress
		if (canCompress && textWebResourceMimeTypes.contains(resourceInfo.getMimeType())) {
			resourceInfo = new CompressWebResourceInfo(resourceInfo);
		}
		
		// cache
		if (canCache) {
			resourceInfo = new CacheableWebResourceInfo(resourceInfo, cacheSeconds);
			
			// insert the update listener into the child resources
			if (childResourceNames != null){
				for(String n : childResourceNames) {
					WebResourceInfo r = getResourceInternal(n);
					if (r != null && r instanceof CacheableWebResource) {
						((CacheableWebResource)r).addUpdateListener((CacheableWebResourceUpdateListener)resourceInfo);
					}
				}
			}
		}
		
		webResourceCache.add(resourceInfo);
		
		buildingResourceNames.pop();
		
		return resourceInfo;
	}
	
	@Override
	public WebResourceInfo getResource(String name) throws FileNotFoundException {
		
		WebResourceInfo resource = getResourceInternal(name);
		
		if (resource == null) {
			throw new FileNotFoundException(String.format("Can not found the web resource [%s]", name));
		}
		
		return resource;
	}
	
	@Override
	public WebResourceInfo getResourceByVersionName(String versionName) throws FileNotFoundException {
		String name = versionStrategy.revert(versionName);
		return getResource(name);
	}

	@Override
	public String getLocation(WebResourceInfo webResourceInfo) {
		return locationGenerator.getLocation(webResourceInfo);
	}

	@Override
	public String getLocation(String name) throws FileNotFoundException {
		WebResourceInfo resource = getResource(name);
		return getLocation(resource);
	}

	@Override
	public void refresh(String name) {
		WebResourceInfo resource = getResourceInternal(name);
		if (resource != null) {
			if (resource instanceof CacheableWebResource) {
				((CacheableWebResource)resource).refresh();
			}
		}
	}

	@Override
	public void setLocationGenerator(LocationGenerator locationGenerator) {
		this.locationGenerator = locationGenerator;
	}

	@Override
	public void setResourceCache(WebResourceCache webResourceCache) {
		this.webResourceCache = webResourceCache;
	}
	
}
