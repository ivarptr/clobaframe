package org.archboy.clobaframe.blobstore.impl;

import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import javax.inject.Named;
import org.springframework.util.Assert;
import org.archboy.clobaframe.blobstore.StoreAgent;
import org.archboy.clobaframe.blobstore.StoreAgentFactory;

/**
 *
 * @author arch
 */
@Named
public class StoreAgentFactoryImpl implements StoreAgentFactory {

	@Inject
	private List<StoreAgent> storeAgents;

	@Value("${blobstore.agent}")
	private String agentName;

	// the default agent
	private StoreAgent storeAgent;

	private final Logger logger = LoggerFactory.getLogger(StoreAgentFactoryImpl.class);

	@PostConstruct
	public void init(){
		// get the config agent
		for(StoreAgent agent : storeAgents){
			if (agent.getName().equals(agentName)){
				storeAgent = agent;
				break;
			}
		}

		Assert.notNull(storeAgent, "Blob store agent not found.");

		logger.info("Using [{}] blob agent as the default.", agentName);
	}

	@Override
	public StoreAgent getStoreAgent() {
		return storeAgent;
	}

	@Override
	public StoreAgent getStoreAgent(String name) {
		Assert.hasText(name);
		
		for(StoreAgent agent : storeAgents) {
			if (agent.getName().equals(name)) {
				return agent;
			}
		}

		throw new IllegalArgumentException("The specify blob agent not found.");
	}

	@Override
	public List<StoreAgent> getStoreAgents() {
		return storeAgents;
	}
}
