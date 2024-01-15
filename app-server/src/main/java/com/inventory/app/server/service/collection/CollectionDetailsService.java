//package com.inventory.app.server.service.collection;
//
//import com.inventory.app.server.entity.CollectionDetails;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class CollectionDetailsService {
//    private IExtendedDao<CollectionDetails, Long> dao;
//
//    @Autowired
//    public void setDao(IExtendedDao<CollectionDetails, Long> daoToSet) {
//        dao = daoToSet;
//        dao.setClazz(CollectionDetails.class);
//    }
//
//    Optional<CollectionDetails> findByTitle(String title) {
//        return dao.findByAttributeContainsText("title", title).stream().findFirst();
//    }
//    List<CollectionDetails> findByCreatedBy(String createdBy){
//        return dao.findByAttributeContainsText("created_by", createdBy);
//    }
//    List<CollectionDetails> findByTag(String tag){
//        return dao.findByAttributeContainsText("tag", tag);
//    }
//}
