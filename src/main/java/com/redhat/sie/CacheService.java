package com.redhat.sie;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.CacheContainerAdmin;
import org.infinispan.commons.util.CloseableIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.sie.model.DataRecord;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class CacheService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("CacheService");
	
	private RemoteCacheManager cacheManager;
	private RemoteCache<String, Object> cache;
	private Map<String, RemoteCache<String, Object>> remoteCaches = new HashMap<String, RemoteCache<String, Object>>();
	
	void onStart(@Observes StartupEvent ev) {
        LOGGER.info("Starting Quarkus app...");        
    }
	
	@PostConstruct
    private void init() {
        LOGGER.info("init CacheService");
        
        ConfigurationBuilder builder = new ConfigurationBuilder();
        
        builder.marshaller(new org.infinispan.commons.marshall.ProtoStreamMarshaller())
    		.statistics()
    		.disable()
    		.jmxDomain("org.example");
    
        cacheManager = new RemoteCacheManager(builder.build());
    }
	
	public boolean createCache(String name, String type) {
		try {
			cache = cacheManager.administration().withFlags(CacheContainerAdmin.AdminFlag.VOLATILE).getOrCreateCache(name, type);
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
	
	public long fillCache(int numentries) {
		long start = Instant.now().toEpochMilli();
        for(int i = 0; i < numentries; i++) {
        	
        	// DataRecord record = new DataRecord(UUID.randomUUID().toString(), "RTU00001", Instant.now().toEpochMilli(), 192, new Random().nextDouble() * 100);
        	// cache.put(record.getSignalSource(), record);
        	//cache.put(UUID.randomUUID().toString(), "SourceSignal$0098;RTU00001;98798798;192;20.98237");
        }
        long end = Instant.now().toEpochMilli();
        LOGGER.info("fill Cache Time:" + (end - start));
        return (end - start);
	}
	
	public long dumpCache() {
		long start = Instant.now().toEpochMilli();
        int batchSize = 5000;

        try (CloseableIterator<Entry<Object, Object>> iterator = cache.retrieveEntries(null, null, batchSize)) {
             while(iterator.hasNext()) {
                iterator.next();
             }
        } catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
        
        long end = Instant.now().toEpochMilli();
        return (end - start);
	}
	
	public void clearCache() {
		cache.clear();
	}
	
	public void startCacheManager() {
		cacheManager.start();
	}
	
	public void stopCacheManager() {
		cacheManager.stop();
	}
	
	

}
