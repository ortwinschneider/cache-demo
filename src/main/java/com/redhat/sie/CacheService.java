package com.redhat.sie;

import java.net.SocketAddress;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.infinispan.client.hotrod.CacheTopologyInfo;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.SaslQop;
import org.infinispan.commons.api.CacheContainerAdmin;
import org.infinispan.commons.util.CloseableIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.sie.model.DataRecord;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class CacheService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("CacheService");
	
	// @Inject
	private RemoteCacheManager cacheManager; 
	
	private Map<String, RemoteCache<String, Object>> remoteCaches = new HashMap<String, RemoteCache<String, Object>>();
	
	@ConfigProperty(name = "infinispan.host")
	String host;
	
	@ConfigProperty(name = "infinispan.port")
	int port;
	
	@ConfigProperty(name = "infinispan.user")
	String user;
	
	@ConfigProperty(name = "infinispan.password")
	String password;
	
	void onStart(@Observes StartupEvent ev) {
        LOGGER.info("Starting Quarkus app... " + cacheManager.getConfiguration().toString());
    }
	
	@PostConstruct
    private void init() {
        LOGGER.info("init CacheService");
        
        ConfigurationBuilder builder = new ConfigurationBuilder();
        
        builder
        	.marshaller(new org.infinispan.commons.marshall.ProtoStreamMarshaller())
        	.addServer()
        		.host(host)
        		.port(port)
        		.version(org.infinispan.client.hotrod.ProtocolVersion.PROTOCOL_VERSION_30)
        	.clientIntelligence(ClientIntelligence.TOPOLOGY_AWARE)	
        	.security()
	            .authentication().enable()
	               .username(user)
	               .password(password)
	               //.serverName("example-infinispan")
	               .saslQop(SaslQop.AUTH)
	               .saslMechanism("DIGEST-MD5")
    		.statistics()
    			.enable()
    		.jmxDomain("org.example");
    
        cacheManager = new RemoteCacheManager(builder.build());
        
    }
	
	public boolean createCache(String name, String type) {
		try {
			cacheManager.administration().withFlags(CacheContainerAdmin.AdminFlag.VOLATILE).getOrCreateCache(name, type);
			return true;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return false;
		}
		
	}
	
	public boolean removeCache(String name) {
		try {
			cacheManager.administration().removeCache(name);
			return true;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return false;
		}
		
	}
	
	public String fillCache(int numentries, String name) {
		RemoteCache<String, Object> cache = retrieveRemoteCache(name);
		if(cache != null) {
			long start = Instant.now().toEpochMilli();
	        for(int i = 0; i < numentries; i++) {
	        	
	        	// DataRecord record = new DataRecord(UUID.randomUUID().toString(), "RTU00001", Instant.now().toEpochMilli(), 192, new Random().nextDouble() * 100);
	        	// cache.put(record.getSignalSource(), record);
	        	cache.put(UUID.randomUUID().toString(), "SourceSignal$0098;RTU00001;98798798;192;20.98237");
	        }
	        long end = Instant.now().toEpochMilli();
	        LOGGER.info("Fill Cache Time:" + (end - start));
	        return "Fill cache with "+numentries+" took "+(end - start)+" ms";
		}
		return null;
	}
	
	public String dumpCache(String name) {
		RemoteCache<String, Object> cache = retrieveRemoteCache(name);
		if(cache != null) {
			long start = Instant.now().toEpochMilli();
	        int batchSize = 5000;
	        int count = 0;
	        try (CloseableIterator<Entry<Object, Object>> iterator = cache.retrieveEntries(null, null, batchSize)) {
	             while(iterator.hasNext()) {
	                iterator.next();
	                count++;
	             }
	        } catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
	        
	        long end = Instant.now().toEpochMilli();
	        LOGGER.info("Dump Cache Time:" + (end - start));
	        return "Dump "+count+" entries in "+(end - start)+" ms";
		}
		return null;
		
	}
	
	public String clearCache(String name) {
		RemoteCache<String, Object> cache = retrieveRemoteCache(name);
		try {
			cache.clear();
			return "Cache cleared";
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return "Error clearing cache";
		}
		
	}
	
	public void startCacheManager() {
		cacheManager.start();
	}
	
	public void stopCacheManager() {
		cacheManager.stop();
	}
	
	private RemoteCache<String, Object> retrieveRemoteCache(String name){
		return cacheManager.getCache(name);
	}
	
	public void getCacheSegments(String name) {
		RemoteCache<String, Object> cache = retrieveRemoteCache(name);
		CacheTopologyInfo cacheTopologyInfo = cache.getCacheTopologyInfo();
		Map<SocketAddress, Set<Integer>> segmentsByAddress = cacheTopologyInfo.getSegmentsPerServer();
		System.out.println(segmentsByAddress.toString() + " : "+ segmentsByAddress.size());
		
		segmentsByAddress.values().stream().forEach((el) -> {
			System.out.println(el.toString());
		});
	}
	
	

}
