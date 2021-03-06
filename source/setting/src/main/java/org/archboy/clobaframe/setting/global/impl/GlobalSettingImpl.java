package org.archboy.clobaframe.setting.global.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import org.archboy.clobaframe.setting.SettingProvider;
import org.archboy.clobaframe.setting.global.GlobalSettingProvider;
import org.archboy.clobaframe.setting.global.GlobalSettingRepository;
import org.archboy.clobaframe.setting.support.Utils;
import org.archboy.clobaframe.setting.global.GlobalSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.OrderComparator;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
@Named
public class GlobalSettingImpl implements GlobalSetting { //, InitializingBean {
	
	private Map<String, Object> setting = new LinkedHashMap<String, Object>();
	
	@Autowired(required = false)
	private List<GlobalSettingProvider> globalSettingProviders;

	@Autowired(required = false)
	private GlobalSettingRepository globalSettingRepository;
	
	private final Logger logger = LoggerFactory.getLogger(GlobalSettingImpl.class);

	public void setGlobalSettingProviders(List<GlobalSettingProvider> globalSettingProviders) {
		this.globalSettingProviders = globalSettingProviders;
	}

	public void setGlobalSettingRepository(GlobalSettingRepository globalSettingRepository) {
		this.globalSettingRepository = globalSettingRepository;
	}

	@PostConstruct
	public void init() throws Exception {
		refresh();
	}
	
	@Override
	public void addProvider(SettingProvider settingProvider) {
		Assert.isInstanceOf(GlobalSettingProvider.class, settingProvider);
		
		if (globalSettingProviders == null) {
			globalSettingProviders = new ArrayList<GlobalSettingProvider>();
		}
		
		globalSettingProviders.add((GlobalSettingProvider)settingProvider);
		
		// sort 0-9
		OrderComparator.sort(globalSettingProviders);
//		globalSettingProviders.sort(new Comparator<GlobalSettingProvider>() {
//			@Override
//			public int compare(GlobalSettingProvider o1, GlobalSettingProvider o2) {
//				return o1.getOrder() - o2.getOrder();
//			}
//		});
	}

	@Override
	public void removeProvider(String providerName) {
		Assert.notNull(providerName);
		for (int idx=globalSettingProviders.size() -1; idx>=0; idx--){
			GlobalSettingProvider provider = globalSettingProviders.get(idx);
			if (providerName.equals(provider.getName())){
				globalSettingProviders.remove(idx);
				break;
			}
		}
	}
		
	@Override
	public void refresh(){
		// clear setting
		setting.clear();
		
		if (globalSettingProviders == null || globalSettingProviders.isEmpty()) {
			return;
		}
		
		// merge all providers setting in the reverse priority,
		// so the higher priority can override the lower one.
		for(int idx = globalSettingProviders.size() -1; idx >=0; idx--){
			GlobalSettingProvider provider = globalSettingProviders.get(idx);
			Map<String, Object> map = provider.list();
			setting = Utils.merge(setting, map);
		}
	}
	
	@Override
	public Object getValue(String key) {
		Object value = setting.get(key);
		return (value == null ? null : Utils.resolvePlaceholder(setting, value));
	}

	@Override
	public Object getValue(String key, Object defaultValue) {
		Object value = getValue(key);
		return (value == null ? defaultValue : value);
	}

	@Override
	public Object get(String key) {
		return setting.get(key);
	}

	@Override
	public Map<String, Object> list() {
		return setting;
	}

	@Override
	public void set(String key, Object value) {
		if (globalSettingRepository == null){
			throw new NullPointerException("No global setting repository.");
		}
		
		globalSettingRepository.update(key, value);
		setting = Utils.merge(setting, key, value);
	}

	@Override
	public void set(Map<String, Object> items) {
		if (globalSettingRepository == null){
			throw new NullPointerException("No global setting repository.");
		}
		
		globalSettingRepository.update(items);
		setting = Utils.merge(setting, items);
	}
}
