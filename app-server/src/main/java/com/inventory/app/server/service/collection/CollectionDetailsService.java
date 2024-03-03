package com.inventory.app.server.service.collection;

import com.inventory.app.server.entity.collection.CollectionDetails;
import com.inventory.app.server.entity.media.Media;
import com.inventory.app.server.entity.user.User;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.repository.IBaseDao;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CollectionDetailsService {
    private IBaseDao<CollectionDetails> dao;

    @Autowired
    public void setDao(IBaseDao<CollectionDetails> daoToSet) {
        dao = daoToSet;
        dao.setClazz(daoToSet.getClazz());
    }

    public List<CollectionDetails> getByTitle(String title) {
        List<CollectionDetails> collectionDetails = dao.findByField("title", title);
        if (collectionDetails.isEmpty()) {
            throw new ResourceNotFoundException("No media collection exists with title: " + title);
        }
        return collectionDetails;
    }
    public List<CollectionDetails> getCollectionsByCreatedBy(String createdBy){
        List<CollectionDetails> collectionDetails = dao.findByField("created_by", createdBy);
        if (collectionDetails.isEmpty()) {
            throw new ResourceNotFoundException("No media collection exists created by: " + createdBy);
        }
        return collectionDetails;
    }
    public List<CollectionDetails> getByTag(String tag){
        List<CollectionDetails> collectionDetails = dao.findByField("tag", tag);
        if (collectionDetails.isEmpty()) {
            throw new ResourceNotFoundException("No media collection exists with the tag " + tag);
        }
        return collectionDetails;
    }

    public CollectionDetails create(CollectionDetails resource) {
        if (collectionAlreadyExists(resource)) {
            throw new ResourceAlreadyExistsException("Cannot create media collection because media collection already exists: " + resource);
        }
        CollectionDetails collectionDetailsToSave = cloneCollectionDetails(resource);
        collectionDetailsToSave.setId(null);
        collectionDetailsToSave.setVersion(1);
        return dao.createOrUpdate(resource);
    }

    public CollectionDetails getById(Long id) {
        try {
            return dao.findOne(id);
        } catch (Exception e) {
          if (e.getClass().isInstance(EntityNotFoundException.class)) {
              throw new ResourceNotFoundException("No media collection exists with id " + id);
          } else {
              throw e;
          }
        }
    }

    public CollectionDetails update(CollectionDetails updatedCollectionDetails) {
        if (!collectionAlreadyExists(updatedCollectionDetails)) {
            throw new ResourceNotFoundException("Cannot update media collection details because collection does not exist.");
        }
        CollectionDetails existingCollectionDetails = getById(updatedCollectionDetails.getId());
        if (verifyIfCollectionDetailsUpdated(existingCollectionDetails, updatedCollectionDetails)) {
            throw new NoChangesToUpdateException("No updates in media collection details to save. Will not proceed with update. Existing media collection details: " + existingCollectionDetails);
        }
        updatedCollectionDetails = cloneCollectionDetails(updatedCollectionDetails);
        updatedCollectionDetails.setId(existingCollectionDetails.getId());
        updatedCollectionDetails.setVersion(existingCollectionDetails.getVersion() + 1);
        return  dao.createOrUpdate(updatedCollectionDetails);
    }

    public List<CollectionDetails> getAllCollectionsByUser() {
        //TODO get the current user
        User user = new User();

        return getCollectionsByCreatedBy(user.getUserLogin().getUsername());
    }

    public List<Media> getAllMediaByCollectionName() {
        //ensure the current user has view permissions for all the collections
        //query each media repo for the media with the collection_name
        return new ArrayList<>();
    }

    private boolean collectionAlreadyExists(CollectionDetails collectionDetails) {
        return getByTitle(collectionDetails.getTitle()).isEmpty();
    }

    private boolean verifyIfCollectionDetailsUpdated(CollectionDetails existingCollectionDetails, CollectionDetails updatedCollectionDetails) {
        return existingCollectionDetails.equals(updatedCollectionDetails);
    }

    private CollectionDetails cloneCollectionDetails(CollectionDetails updatedCollectionDetails) {
        CollectionDetails clonedCollectionDetails = new CollectionDetails();
        BeanUtils.copyProperties(updatedCollectionDetails, clonedCollectionDetails);
        return clonedCollectionDetails;
    }
}
