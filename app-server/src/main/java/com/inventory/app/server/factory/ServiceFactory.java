package com.inventory.app.server.factory;

import com.inventory.app.server.service.media.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Slf4j
public class ServiceFactory {
    private HashMap<String, ObjectProvider<? extends BaseService<?>>> serviceMap = new HashMap();

    public ServiceFactory(ObjectProvider<MovieService> movieServiceProvider,
                          ObjectProvider<BookService> bookServiceProvider,
                          ObjectProvider<VideoGameService> videoGameServiceProvider,
                          ObjectProvider<MusicService> musicServiceProvider,
                          ObjectProvider<TelevisionService> televisionServiceProvider) {
       serviceMap.put("book",  bookServiceProvider);
       serviceMap.put("videoGame", videoGameServiceProvider);
        serviceMap.put("music", musicServiceProvider);
        serviceMap.put("televisionShow", televisionServiceProvider);
        serviceMap.put("movie", movieServiceProvider);

    }

    public <T> BaseService<T> getService(String entityType) {
        ObjectProvider<? extends BaseService<?>> provider = serviceMap.get(entityType);
        if (provider == null) {
            throw new IllegalArgumentException("No service found for entity type " + entityType);
        }
        log.info("ServiceFactory: Retrieved service for entity type " + entityType);
        return (BaseService<T>) provider.getIfAvailable();
    }
}
