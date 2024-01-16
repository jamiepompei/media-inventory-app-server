package com.inventory.app.server.service.collection;

import com.google.common.base.Preconditions;
import com.inventory.app.server.entity.CollectionDetails;
import com.inventory.app.server.entity.Media;
import com.inventory.app.server.entity.user.User;
import com.inventory.app.server.repository.IBaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CollectionDetailsService {
    private IBaseDao<CollectionDetails, Long> dao;

    @Autowired
    public void setDao(IBaseDao<CollectionDetails, Long> daoToSet) {
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
        Preconditions.checkNotNull(resource);
        return  dao.create(resource);
    }


    public CollectionDetails update(CollectionDetails resource) {
        Preconditions.checkNotNull(resource);
        return  dao.create(resource);
    }

    public List<CollectionDetails> getAllCollections() {
        //TODO get the currentuser
        User user = new User();
        return findCollectionsByCreatedBy(user.getUserLogin().getUsername());
    }

    public List<Media> getAllMediaByCollectionName() {
        //ensure the current user has view permissions for all the collections
        //query each media repo for the media with the collection_name
        return new ArrayList<>();
    }
}
