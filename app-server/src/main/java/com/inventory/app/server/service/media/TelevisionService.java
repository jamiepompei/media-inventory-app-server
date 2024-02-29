package com.inventory.app.server.service.media;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.TelevisionShow;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.repository.IBaseDao;
import jakarta.persistence.EntityNotFoundException;
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
        List<TelevisionShow> televisionShowList = dao.findByField("collection_name", collectionTitle);
        if (televisionShowList.isEmpty()) {
            throw new ResourceNotFoundException("No television show results found with collection title " + collectionTitle);
        }
        return televisionShowList;
    }

    public List<TelevisionShow> getAllTelevisionShowsByEpisode(List<String> episodes) {
        List<TelevisionShow> televisionShowList = dao.findByField(MediaInventoryAdditionalAttributes.EPISODES.getJsonKey(), episodes);
        if (televisionShowList.isEmpty()) {
            throw new ResourceNotFoundException("No television show results found with episode(s) " + episodes);
        }
        return televisionShowList;
    }

    public List<TelevisionShow> getAllTelevisionShowsByGenre(String genre) {
        List<TelevisionShow> televisionShowList = dao.findByField("genre", genre);
        if (televisionShowList.isEmpty()) {
            throw new ResourceNotFoundException("No television show results found for genre " + genre);
        }
        return televisionShowList;
    }

    public List<TelevisionShow> getAll() {
        List<TelevisionShow> televisionShowList = dao.findAll();
        if (televisionShowList.isEmpty()) {
            throw new ResourceNotFoundException("No television show data exists.");
        }
        return televisionShowList;
    }

    public TelevisionShow getById(Long id) {
        try {
            return dao.findOne(id);
        } catch (Exception e) {
            if ( e.getClass().isInstance(EntityNotFoundException.class)) {
                throw new ResourceNotFoundException("No book exists with id: " + id);
            } else {
                throw e;
            }
        }
    }

    public TelevisionShow create(TelevisionShow televisionShow) {
        if(televisionShowAlreadyExists(televisionShow)) {
            throw new ResourceAlreadyExistsException("Cannot create television show because television show already exists: " + televisionShow);
        }
        TelevisionShow televisionShowToSave = cloneTelevisionShow(televisionShow);
        televisionShowToSave.setId(null);
        televisionShowToSave.setVersion(1);
        return dao.createOrUpdate(televisionShowToSave);
    }

    public TelevisionShow update(TelevisionShow updatedTelevisionShow) {
        if (!televisionShowAlreadyExists(updatedTelevisionShow)) {
            throw new ResourceNotFoundException("Cannot update television show because television show does not exist: " + updatedTelevisionShow);
        }
        TelevisionShow existingTelevisionShow = getById(updatedTelevisionShow.getId());
        if (verifyIfTelevisionShowUpdated(existingTelevisionShow, updatedTelevisionShow)) {
            throw new NoChangesToUpdateException("No updates in television show to save. Will not proceed with update. Existing Television Show: " + existingTelevisionShow + "Updated Television Show: " + updatedTelevisionShow);
        }
        updatedTelevisionShow = cloneTelevisionShow(existingTelevisionShow, updatedTelevisionShow);
        updatedTelevisionShow.setVersion(existingTelevisionShow.getVersion() + 1);
        return dao.createOrUpdate(updatedTelevisionShow);
    }

    public TelevisionShow deleteById(Long id){
        TelevisionShow televisionShow = getById(id);
        if (televisionShow == null) {
            throw new ResourceNotFoundException("Cannot delete television show because television show does not exist.");
        }
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

    private boolean verifyIfTelevisionShowUpdated(TelevisionShow existingTelevisionShow, TelevisionShow updatedTelevisionShow) {
        return existingTelevisionShow.equals(updatedTelevisionShow);
    }
}
