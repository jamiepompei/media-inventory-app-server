package com.inventory.app.server.service.collection;

import com.inventory.app.server.entity.collection.Collection;
import com.inventory.app.server.entity.media.Media;
import com.inventory.app.server.entity.payload.request.SearchCollectionRequest;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.repository.IBaseDao;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class CollectionService {
    private IBaseDao<Collection> dao;

    @Autowired
    public void setDao(@Qualifier("genericDaoImpl") IBaseDao<Collection> daoToSet) {
        dao = daoToSet;
        dao.setClazz(daoToSet.getClazz());
    }

    public List<Collection> searchCollections(SearchCollectionRequest searchCollectionRequest) {
        Optional<Predicate<Collection>> searchPredicate = buildSearchPredicate(searchCollectionRequest);
        return searchPredicate.map(collectionPredicate -> dao.findAll()
                .stream()
                .filter(collectionPredicate)
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    private Optional<Predicate<Collection>> buildSearchPredicate(SearchCollectionRequest searchCollectionRequest) {
        Predicate<Collection> predicate = collection -> true; // Default Predicate

        if (StringUtils.isNotEmpty(searchCollectionRequest.getCollectionTitle())) {
            predicate = predicate.and(collection -> collection.getCollectionTitle().equals(searchCollectionRequest.getCollectionTitle()));
        }
        if (StringUtils.isNotEmpty(searchCollectionRequest.getUsername())) {
            predicate = predicate.and(collection -> collection.getCreatedBy().equals(searchCollectionRequest.getUsername()));
        }
        return Optional.of(predicate);
    }

    public Collection create(Collection resource) {
        try {
            return dao.createOrUpdate(resource);
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceAlreadyExistsException("Collection already exists " + resource);
        }
    }

    public Collection getById(Long id, String username) {
        try {
            return dao.findOne(id, username);
        } catch (Exception e) {
          if (e.getClass().isInstance(EntityNotFoundException.class)) {
              throw new ResourceNotFoundException("No media collection exists with id " + id);
          } else {
              throw e;
          }
        }
    }

    @Transactional
    public Collection update(Collection updatedCollectionMedia) {
        Collection existingCollectionMedia = getById(updatedCollectionMedia.getId(), updatedCollectionMedia.getCreatedBy());

        if (existingCollectionMedia == null) {
            throw new ResourceNotFoundException("Media collection not found. Will not proceedwith update.");
        }

        if (existingCollectionMedia.equals(updatedCollectionMedia)) {
            throw new NoChangesToUpdateException("No updates in media collection details to save. Will not proceed with update. Existing media collection details: " + existingCollectionMedia);
        }
        return  dao.createOrUpdate(updatedCollectionMedia);
    }

    public List<Media> getAllMediaByCollectionName() {
        //ensure the current user has view permissions for all the collections
        //query each media repo for the media with the collection_name
        return new ArrayList<>();
    }
}
