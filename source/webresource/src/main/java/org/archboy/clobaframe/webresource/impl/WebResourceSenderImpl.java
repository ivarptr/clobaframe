package org.archboy.clobaframe.webresource.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.archboy.clobaframe.io.http.ClientCacheResourceSender;
import org.archboy.clobaframe.webresource.AbstractServerWebResourceInfo;
import org.archboy.clobaframe.webresource.CacheableWebResourceInfo;
import org.archboy.clobaframe.webresource.CompressibleWebResourceInfo;
import org.archboy.clobaframe.webresource.ServerWebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceManager;
import org.archboy.clobaframe.webresource.WebResourceSender;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 *
 */
@Named
public class WebResourceSenderImpl implements WebResourceSender{

	@Inject
	private ClientCacheResourceSender cacheResourceSender;

	@Inject
	private WebResourceManager webResourceManager;

	public void setCacheResourceSender(ClientCacheResourceSender cacheResourceSender) {
		this.cacheResourceSender = cacheResourceSender;
	}

	public void setWebResourceManager(WebResourceManager webResourceManager) {
		this.webResourceManager = webResourceManager;
	}

	@Override
	public void send(String resourceName, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		Assert.hasText(resourceName, "Resource name should not empty.");
		Assert.notNull(request);
		Assert.notNull(response);
		
		WebResourceInfo webResourceInfo = webResourceManager.getServerResource(resourceName);
		if (webResourceInfo == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
				"Resource not found");
		}else{
			send(webResourceInfo, request, response);
		}
	}

	private void send(WebResourceInfo webResourceInfo, HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> headers = new HashMap<String, Object>();
		
		if (webResourceInfo instanceof CompressibleWebResourceInfo ||
			(webResourceInfo instanceof AbstractServerWebResourceInfo &&
				((AbstractServerWebResourceInfo)webResourceInfo).listInheritTypes()
					.contains(ServerWebResourceInfo.TYPE_COMPRESS))){
			// it's compressed resource already.
			headers.put("Content-Encoding", "gzip");
		}
		
		cacheResourceSender.send(webResourceInfo,
				ClientCacheResourceSender.CACHE_CONTROL_PUBLIC,
				ClientCacheResourceSender.THREE_MONTH_SECONDS, headers, request, response);
	}

	@Override
	public void sendByVersionName(String versionName, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		Assert.hasText(versionName, "Resource name should not empty.");
		Assert.notNull(request);
		Assert.notNull(response);
		
		WebResourceInfo webResourceInfo = webResourceManager.getServerResourceByVersionName(versionName);
		if (webResourceInfo == null){
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Resource not found");
		}else{
			send(webResourceInfo, request, response);
		}
	}
}
