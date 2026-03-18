package com.inventory.app.server.service.collection;

import com.inventory.app.server.entity.collection.Collection;
import com.inventory.app.server.entity.payload.request.SearchCollectionRequest;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.repository.IBaseDao;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CollectionService {
    private IBaseDao<Collection> dao;

    @Autowired
    public void setDao(@Qualifier("genericDaoImpl") IBaseDao<Collection> daoToSet) {
        dao = daoToSet;
        dao.setClazz(daoToSet.getClazz());
    }

    public List<Collection> searchCollections(SearchCollectionRequest searchCollectionRequest) {
        log.info("Searching collections with criteria: {}", searchCollectionRequest);
        Optional<Predicate<Collection>> searchPredicate = buildSearchPredicate(searchCollectionRequest);
        List<Collection> collections = searchPredicate.map(collectionPredicate -> dao.findAll()
                .stream()
                .filter(collectionPredicate)
                .collect(Collectors.toList())).orElse(Collections.emptyList());
        log.info("Search completed. Number of collections found: {}", collections.size());
        return  collections;
    }

    private Optional<Predicate<Collection>> buildSearchPredicate(SearchCollectionRequest searchCollectionRequest) {
        Predicate<Collection> predicate = collection -> true; // Default Predicate

        if (StringUtils.isNotEmpty(searchCollectionRequest.getCollectionTitle())) {
            predicate = predicate.and(collection -> collection.getCollectionTitle().equals(searchCollectionRequest.getCollectionTitle()));
        }
        if (StringUtils.isNotEmpty(searchCollectionRequest.getUsername())) {
            predicate = predicate.and(collection -> collection.getCreatedBy().equals(searchCollectionRequest.getUsername()));
        }
        log.info("Search predicate: {} built successfully for criteria: {}", predicate, searchCollectionRequest);
        return Optional.of(predicate);
    }

    @Transactional
    public Collection create(Collection resource) {
        log.info("Creating collection: {}", resource);
        try {
            Collection createdCollection = dao.createOrUpdate(resource);
            log.info("Collection created successfully with ID: {}", createdCollection.getId());
            return createdCollection;
        } catch (DataIntegrityViolationException ex) {
            log.error("Collection already exists: {}", resource, ex);
            throw new ResourceAlreadyExistsException("Collection already exists: " + resource);
        } catch (Exception e) {
            log.error("Error occurred while creating collection: {}", resource, e);
            throw e;
        }
    }

    public Collection getById(Long id, String username) {
        log.info("Fetching collection with ID: {} for user: {}", id, username);
        try {
            Collection collection = dao.findOne(id, username);
            log.info("Collection fetched successfully: {}", collection);
            return collection;
        } catch (EntityNotFoundException e) {
            log.error("No collection found with ID: {}", id, e);
            throw new ResourceNotFoundException("No media collection exists with ID: " + id);
        } catch (Exception e) {
            log.error("Error occurred while fetching collection with ID: {}", id, e);
            throw e;
        }
    }

    @Transactional
    public Collection update(Collection updatedCollectionMedia) {
        log.info("Updating collection with ID: {}", updatedCollectionMedia.getId());

        try {
            Collection existingCollectionMedia = getById(updatedCollectionMedia.getId(), updatedCollectionMedia.getCreatedBy());

            if (existingCollectionMedia.equals(updatedCollectionMedia)) {
                log.warn("No changes detected for collection with ID: {}", updatedCollectionMedia.getId());
                throw new NoChangesToUpdateException("No updates in media collection details to save. Existing details: " + existingCollectionMedia);
            }

            Collection updatedCollection = dao.createOrUpdate(updatedCollectionMedia);
            log.info("Collection updated successfully with ID: {}", updatedCollection.getId());
            return updatedCollection;
        } catch (ResourceNotFoundException | NoChangesToUpdateException e) {
            log.error("Validation error during update: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while updating collection with ID: {}", updatedCollectionMedia.getId(), e);
            throw e;
        }
    }
}
