package org.archboy.clobaframe.setting.application.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.setting.application.ApplicationSettingProvider;
import org.archboy.clobaframe.setting.support.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yang
 */
public class JsonApplicationSettingProvider implements ApplicationSettingProvider {

	protected String dataFolder;
	protected String fileName;
	
	protected final Logger logger = LoggerFactory.getLogger(CustomApplicationSettingRepository.class);
	
	public JsonApplicationSettingProvider(String dataFolder, String fileName) {
		this.dataFolder = dataFolder;
		this.fileName = fileName;
	}

	@Override
	public int getOrder() {
		return 1;
	}

	@Override
	public Map<String, Object> getAll() {
		File file = new File(dataFolder, fileName);
		
		if (!file.exists()){
			logger.warn("Custom application setting [{}] not found.", file.getAbsolutePath());
		}else if (file.isDirectory()) {
			logger.warn("Custom application setting [{}] duplicate name with a file.", file.getAbsolutePath());
		}else {
			logger.info("Load custom application setting [{}]", file.getAbsolutePath());

			InputStream in = null;
			try{
				in = new FileInputStream(file);
				return Utils.readJson(in);
			}catch(IOException e){
				// ignore
				logger.error("Load custom application setting failed: {}", e.getMessage());
			}finally{
				IOUtils.closeQuietly(in);
			}
		}
		
		return new LinkedHashMap<String, Object>();
	}
	
}
