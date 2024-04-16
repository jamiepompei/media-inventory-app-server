package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.TelevisionShow;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.repository.IBaseDao;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.inventory.app.server.config.MediaInventoryAdditionalAttributes.*;

@Service
@Transactional
public class TelevisionService {
    private IBaseDao<TelevisionShow> dao;
    @Autowired
    public void setDao(@Qualifier("genericDaoImpl") IBaseDao<TelevisionShow> daoToSet) {
        dao = daoToSet;
        dao.setClazz(TelevisionShow.class);
    }

    public List<TelevisionShow> searchTelevisionShows(SearchMediaRequest searchMediaRequest) {
        Optional<Predicate<TelevisionShow>> searchPredicate = buildSearchPredicate(searchMediaRequest);
        return searchPredicate.map(televisionShowPredicate -> dao.findAll().stream()
                .filter(televisionShowPredicate)
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    private Optional<Predicate<TelevisionShow>> buildSearchPredicate(SearchMediaRequest searchMediaRequest) {
        Predicate<TelevisionShow> predicate = televisionShow -> true; // Default Predicate
        if (searchMediaRequest.getCollectionTitle() != null && !searchMediaRequest.getCollectionTitle().isEmpty()) {
            predicate = predicate.and(televisionShow -> televisionShow.getCollectionTitle().equals(searchMediaRequest.getCollectionTitle()));
        }
        if (searchMediaRequest.getTitle() != null && !searchMediaRequest.getTitle().isEmpty()) {
            predicate = predicate.and(televisionShow -> televisionShow.getTitle().equals(searchMediaRequest.getTitle()));
        }
        if (searchMediaRequest.getGenre() != null && !searchMediaRequest.getGenre().isEmpty()) {
            predicate = predicate.and(televisionShow -> televisionShow.getGenre().equals(searchMediaRequest.getGenre()));
        }
        if (searchMediaRequest.getFormat() != null && !searchMediaRequest.getFormat().isEmpty()) {
            predicate = predicate.and(televisionShow -> televisionShow.getFormat().equals(searchMediaRequest.getFormat()));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(EPISODES.getJsonKey()) != null && !searchMediaRequest.getAdditionalAttributes().get(EPISODES.getJsonKey()).toString().isEmpty()) {
            predicate = predicate.and(televisionShow -> televisionShow.getEpisodes().equals(searchMediaRequest.getAdditionalAttributes().get(EPISODES.getJsonKey())));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(SEASON.getJsonKey()) != null) {
            predicate = predicate.and(televisionShow -> televisionShow.getEpisodes().equals(searchMediaRequest.getAdditionalAttributes().get(SEASON.getJsonKey())));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(RELEASE_YEAR.getJsonKey()) != null) {
            predicate = predicate.and(televisionShow -> televisionShow.getEpisodes().equals(searchMediaRequest.getAdditionalAttributes().get(RELEASE_YEAR.getJsonKey())));
        }
      return Optional.of(predicate);
    }

    public Optional<TelevisionShow> getById(Long id, String username) {
        return Optional.of(dao.findOne(id, username));
    }

    public TelevisionShow create(TelevisionShow televisionShow) {
        Optional<TelevisionShow> existingTelevisionShow = getById(televisionShow.getId(), televisionShow.getCreatedBy());
        if(existingTelevisionShow.isPresent()) {
            throw new ResourceAlreadyExistsException("Cannot create television show because television show already exists: " + televisionShow);
        }
        return dao.createOrUpdate(televisionShow);
    }

    public TelevisionShow update(TelevisionShow updatedTelevisionShow) {
        Optional<TelevisionShow> existingTelevisionShow = getById(updatedTelevisionShow.getId(), updatedTelevisionShow.getCreatedBy());

        if (existingTelevisionShow.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update television show because television show does not exist: " + updatedTelevisionShow);
        }
        if (verifyIfTelevisionShowUpdated(existingTelevisionShow.get(), updatedTelevisionShow)) {
            throw new NoChangesToUpdateException("No updates in television show to save. Will not proceed with update. Existing Television Show: " + existingTelevisionShow + "Updated Television Show: " + updatedTelevisionShow);
        }
        return dao.createOrUpdate(updatedTelevisionShow);
    }

    public TelevisionShow deleteById(Long id, String username){
        Optional<TelevisionShow> televisionShow = getById(id, username);
        if (televisionShow.isEmpty()) {
            throw new ResourceNotFoundException("Cannot delete television show because television show does not exist.");
        }
        dao.deleteById(id, username);
        return televisionShow.get();
    }

    private boolean verifyIfTelevisionShowUpdated(TelevisionShow existingTelevisionShow, TelevisionShow updatedTelevisionShow) {
        return existingTelevisionShow.equals(updatedTelevisionShow);
    }
}
