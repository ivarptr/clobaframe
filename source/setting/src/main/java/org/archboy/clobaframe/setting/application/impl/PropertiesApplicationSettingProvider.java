package org.archboy.clobaframe.setting.application.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.setting.application.ApplicationSettingProvider;
import org.archboy.clobaframe.setting.support.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
public class PropertiesApplicationSettingProvider implements ApplicationSettingProvider {

	private ResourceLoader resourceLoader;
	private String fileName;
	
	private final Logger logger = LoggerFactory.getLogger(PropertiesApplicationSettingProvider.class);
	
	public PropertiesApplicationSettingProvider(ResourceLoader resourceLoader, String fileName) {
		this.resourceLoader = resourceLoader;
		this.fileName = fileName;
	}
	
	@Override
	public int getOrder() {
		return 10;
	}

	@Override
	public Map<String, Object> getAll() {
		Resource resource = resourceLoader.getResource(fileName);
		
		if (!resource.exists()) {
			logger.warn("Default application setting [{}] not found.", fileName);
		}else{
			logger.info("Load default application setting [{}]", fileName);
			InputStream in = null;
			try{
				in = resource.getInputStream();
				return Utils.readProperties(in);
			}catch(IOException e) {
				// ignore
				logger.error("Load default application setting failed: {}", e.getMessage());
			}finally {
				IOUtils.closeQuietly(in);
			}
		}
		
		return new LinkedHashMap<String, Object>();
	}
}
