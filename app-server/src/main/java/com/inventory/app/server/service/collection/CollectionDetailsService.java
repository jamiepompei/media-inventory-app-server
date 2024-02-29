package com.inventory.app.server.service.collection;

import com.inventory.app.server.entity.collection.CollectionDetails;
import com.inventory.app.server.entity.media.Media;
import com.inventory.app.server.entity.user.User;
import com.inventory.app.server.repository.IBaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CollectionDetailsService {
    private IBaseDao<CollectionDetails> dao;

    @Autowired
    public void setDao(IBaseDao<CollectionDetails> daoToSet) {
        dao = daoToSet;
        dao.setClazz(daoToSet.getClazz());
    }

    public Optional<CollectionDetails> findByTitle(String title) {
        return dao.findByField("title", title).stream().findFirst();
    }
    public List<CollectionDetails> findCollectionsByCreatedBy(String createdBy){
        return dao.findByField("created_by", createdBy);
    }
    public List<CollectionDetails> findByTag(String tag){
        return dao.findByField("tag", tag);
    }

    public CollectionDetails create(CollectionDetails resource) {
        return  dao.createOrUpdate(resource);
    }


    public CollectionDetails update(CollectionDetails resource) {
        return  dao.createOrUpdate(resource);
    }

    public List<CollectionDetails> getAllCollections() {
        //TODO get the current user
        User user = new User();
        return findCollectionsByCreatedBy(user.getUserLogin().getUsername());
    }

    public List<Media> getAllMediaByCollectionName() {
        //ensure the current user has view permissions for all the collections
        //query each media repo for the media with the collection_name
        return new ArrayList<>();
    }
}
