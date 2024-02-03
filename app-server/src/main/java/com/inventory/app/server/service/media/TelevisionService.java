package com.inventory.app.server.service.media;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.TelevisionShow;
import com.inventory.app.server.repository.IBaseDao;
import com.inventory.app.server.utility.RestPreConditions;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TelevisionService {
    private IBaseDao<TelevisionShow> dao;
    @Autowired
    public void setDao(@Qualifier("genericDaoImpl") IBaseDao<TelevisionShow> daoToSet) {
        dao = daoToSet;
        dao.setClazz(TelevisionShow.class);
    }

    public List<TelevisionShow> getAllBooksByCollectionTitle(String collectionTitle) {
        return dao.findByField("collection_name", collectionTitle);
    }

    public List<TelevisionShow> getAllTelevisionShowsByEpisode(List<String> writer) {
        return dao.findByField(MediaInventoryAdditionalAttributes.EPISODES.getJsonKey(), writer);
    }

    public List<TelevisionShow> getAllTelevisionShowsByGenre(String genre) {
        return dao.findByField("genre", genre);
    }

    public List<TelevisionShow> getAllTelevisionShows() {
        return dao.findAll();
    }

    public TelevisionShow getTelevisionShowById(Long id) {
        return dao.findOne(id);
    }

    public TelevisionShow create(TelevisionShow televisionShow) {
        // validations before performing create
        RestPreConditions.checkAlreadyExists(televisionShowAlreadyExists(televisionShow), televisionShow);

        TelevisionShow televisionShowToSave = cloneTelevisionShow(televisionShow);
        televisionShowToSave.setId(null);
        televisionShowToSave.setVersion(1);

        return dao.createOrUpdate(televisionShowToSave);
    }

    public TelevisionShow update(TelevisionShow updatedTelevisionShow) {
        //validations before performing the update
        TelevisionShow existingTelevisionShow = RestPreConditions.checkFound(getTelevisionShowById(updatedTelevisionShow.getId()));
        RestPreConditions.checkEquals(existingTelevisionShow, updatedTelevisionShow);

        updatedTelevisionShow = cloneTelevisionShow(existingTelevisionShow, updatedTelevisionShow);
        updatedTelevisionShow.setVersion(existingTelevisionShow.getVersion() + 1);

        return dao.createOrUpdate(updatedTelevisionShow);
    }

    public TelevisionShow deleteById(Long id){
        TelevisionShow televisionShow = RestPreConditions.checkFound(getTelevisionShowById(id));
        dao.deleteById(id);
        return televisionShow;
    }

    private TelevisionShow cloneTelevisionShow(TelevisionShow televisionShow) {
        TelevisionShow clonedTelevisionShow = new TelevisionShow();
        BeanUtils.copyProperties(televisionShow, clonedTelevisionShow);
        return clonedTelevisionShow;
    }

    private TelevisionShow cloneTelevisionShow(TelevisionShow existingTelevisionShow, TelevisionShow updatedTelevisionShow) {
        BeanUtils.copyProperties(updatedTelevisionShow, existingTelevisionShow);
        return existingTelevisionShow;
    }

    private boolean televisionShowAlreadyExists(TelevisionShow televisionShow) {
        return getAllTelevisionShowsByEpisode(televisionShow.getEpisodes())
                .stream()
                .anyMatch(t -> televisionShow.getTitle().equals(t.getTitle()));
    }
}
