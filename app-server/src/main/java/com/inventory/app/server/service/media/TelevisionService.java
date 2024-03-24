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

    public List<TelevisionShow> getAllBooksByCollectionTitle(String collectionTitle, String username) {
        List<TelevisionShow> televisionShowList = dao.findByField("collection_name", collectionTitle, username);
        if (televisionShowList.isEmpty()) {
            throw new ResourceNotFoundException("No television show results found with collection title " + collectionTitle);
        }
        return televisionShowList;
    }

    public List<TelevisionShow> getAllTelevisionShowsByEpisode(List<String> episodes, String username) {
        List<TelevisionShow> televisionShowList = dao.findByField(MediaInventoryAdditionalAttributes.EPISODES.getJsonKey(), episodes, username);
        if (televisionShowList.isEmpty()) {
            throw new ResourceNotFoundException("No television show results found with episode(s) " + episodes);
        }
        return televisionShowList;
    }

    public List<TelevisionShow> getAllTelevisionShowsByGenre(String genre, String username) {
        List<TelevisionShow> televisionShowList = dao.findByField("genre", genre, username);
        if (televisionShowList.isEmpty()) {
            throw new ResourceNotFoundException("No television show results found for genre " + genre);
        }
        return televisionShowList;
    }

    public List<TelevisionShow> getAllByUsername(String username) {
        List<TelevisionShow> televisionShowList = dao.findAllByUsername(username);
        if (televisionShowList.isEmpty()) {
            throw new ResourceNotFoundException("No television show data exists.");
        }
        return televisionShowList;
    }

    public TelevisionShow getById(Long id, String username) {
        try {
            return dao.findOne(id, username);
        } catch (Exception e) {
            if ( e.getClass().isInstance(EntityNotFoundException.class)) {
                throw new ResourceNotFoundException("No book exists with id: " + id);
            } else {
                throw e;
            }
        }
    }

    public TelevisionShow create(TelevisionShow televisionShow, String username) {
        if(televisionShowAlreadyExists(televisionShow, username)) {
            throw new ResourceAlreadyExistsException("Cannot create television show because television show already exists: " + televisionShow);
        }
        TelevisionShow televisionShowToSave = cloneTelevisionShow(televisionShow);
        televisionShowToSave.setId(null);
        televisionShowToSave.setVersion(1);
        return dao.createOrUpdate(televisionShowToSave);
    }

    public TelevisionShow update(TelevisionShow updatedTelevisionShow, String username) {
        if (!televisionShowAlreadyExists(updatedTelevisionShow, username)) {
            throw new ResourceNotFoundException("Cannot update television show because television show does not exist: " + updatedTelevisionShow);
        }
        TelevisionShow existingTelevisionShow = getById(updatedTelevisionShow.getId(), username);
        if (verifyIfTelevisionShowUpdated(existingTelevisionShow, updatedTelevisionShow)) {
            throw new NoChangesToUpdateException("No updates in television show to save. Will not proceed with update. Existing Television Show: " + existingTelevisionShow + "Updated Television Show: " + updatedTelevisionShow);
        }
        updatedTelevisionShow = cloneTelevisionShow(updatedTelevisionShow);
        updatedTelevisionShow.setId(existingTelevisionShow.getId());
        updatedTelevisionShow.setVersion(existingTelevisionShow.getVersion() + 1);
        return dao.createOrUpdate(updatedTelevisionShow);
    }

    public TelevisionShow deleteById(Long id, String username){
        TelevisionShow televisionShow = getById(id, username);
        if (televisionShow == null) {
            throw new ResourceNotFoundException("Cannot delete television show because television show does not exist.");
        }
        dao.deleteById(id, username);
        return televisionShow;
    }

    private TelevisionShow cloneTelevisionShow(TelevisionShow televisionShow) {
        TelevisionShow clonedTelevisionShow = new TelevisionShow();
        BeanUtils.copyProperties(televisionShow, clonedTelevisionShow);
        return clonedTelevisionShow;
    }

    private boolean televisionShowAlreadyExists(TelevisionShow televisionShow, String username) {
        return getAllTelevisionShowsByEpisode(televisionShow.getEpisodes(), username)
                .stream()
                .anyMatch(t -> televisionShow.getTitle().equals(t.getTitle()));
    }

    private boolean verifyIfTelevisionShowUpdated(TelevisionShow existingTelevisionShow, TelevisionShow updatedTelevisionShow) {
        return existingTelevisionShow.equals(updatedTelevisionShow);
    }
}
