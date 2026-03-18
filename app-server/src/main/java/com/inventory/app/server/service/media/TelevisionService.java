package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.TelevisionShow;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.entity.payload.request.UpdateCreateMediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.mapper.TelevisionShowMapper;
import com.inventory.app.server.repository.IBaseDao;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TelevisionService implements BaseService<TelevisionShow>{
    private IBaseDao<TelevisionShow> dao;
    @Autowired
    public void setDao(@Qualifier("genericDaoImpl") IBaseDao<TelevisionShow> daoToSet) {
        dao = daoToSet;
        dao.setClazz(TelevisionShow.class);
    }

    public List<MediaResponse> search(SearchMediaRequest searchMediaRequest) {
        log.info("Initiating search for tv shows with search criteria: {}", searchMediaRequest);
        Optional<Predicate<TelevisionShow>> searchPredicate = buildSearchPredicate(searchMediaRequest);
        List<MediaResponse> mediaResponses = searchPredicate.map(televisionShowPredicate -> dao.findAll().stream()
                .filter(televisionShowPredicate)
                .map(TelevisionShowMapper.INSTANCE::mapTelevisionShowToMediaResponse)
                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
        if (mediaResponses.isEmpty()) {
            log.info("No tv shows found matchin search criteria: {}", searchMediaRequest);
        } else {
            log.info("Search completed successfully. Number of tv shows found: {}", mediaResponses.size());
        }
        return mediaResponses;
    }

    private Optional<Predicate<TelevisionShow>> buildSearchPredicate(SearchMediaRequest searchMediaRequest) {
        Predicate<TelevisionShow> predicate = televisionShow -> true; // Default Predicate

        if (searchMediaRequest.getTitle() != null && !searchMediaRequest.getTitle().isEmpty()) {
            predicate = predicate.and(televisionShow -> televisionShow.getTitle().equals(searchMediaRequest.getTitle()));
        }
        if (searchMediaRequest.getGenre() != null && !searchMediaRequest.getGenre().isEmpty()) {
            predicate = predicate.and(televisionShow -> televisionShow.getGenre().equals(searchMediaRequest.getGenre()));
        }
        if (searchMediaRequest.getFormat() != null && !searchMediaRequest.getFormat().isEmpty()) {
            predicate = predicate.and(televisionShow -> televisionShow.getFormat().equals(searchMediaRequest.getFormat()));
        }
        if (searchMediaRequest.getUsername() != null && !searchMediaRequest.getUsername().isEmpty()) {
            predicate = predicate.and(televisionShow -> televisionShow.getCreatedBy().equals(searchMediaRequest.getUsername()));
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
        log.info("Search predicate: {} built successfully for search criteria: {}", predicate, searchMediaRequest);
      return Optional.of(predicate);
    }

    public Optional<TelevisionShow> getById(Long id, String username) {
        log.info("Initiating retrieval of book with ID: {} for user: {}", id, username);
        TelevisionShow televisionShow = dao.findOne(id, username);
        if (televisionShow == null) {
            log.warn("TV show with ID: {} not found for user: {}", id, username);
            return Optional.empty();
        } else {
            log.info("TV show with ID: {} retrieved successfully for user: {}", id, username);
            return Optional.of(televisionShow);
        }
    }

    public MediaResponse create(UpdateCreateMediaRequest updateCreateMediaRequest) {
        TelevisionShow televisionShow = TelevisionShowMapper.INSTANCE.mapMediaRequestToTelevisionShow(updateCreateMediaRequest);
        Optional<TelevisionShow> existingTelevisionShow = getById(televisionShow.getId(), televisionShow.getCreatedBy());
        if(existingTelevisionShow.isPresent()) {
            log.warn("Attempting to create a TV show with an id that already exists. TV show: {}", televisionShow);
            throw new ResourceAlreadyExistsException("Cannot create television show because television show already exists: " + televisionShow);
        }
        log.info("Initiating tv show POST request. TV show to be created: {}", televisionShow);
        MediaResponse response = TelevisionShowMapper.INSTANCE.mapTelevisionShowToMediaResponse(dao.createOrUpdate(televisionShow));
        log.info("Televisio show created successfully with ID: {}", televisionShow.getId());
        return response;
    }

    public MediaResponse update(UpdateCreateMediaRequest updateCreateMediaRequest) {
        TelevisionShow updatedTelevisionShow = TelevisionShowMapper.INSTANCE.mapMediaRequestToTelevisionShow(updateCreateMediaRequest);
        Optional<TelevisionShow> existingTelevisionShow = getById(updatedTelevisionShow.getId(), updatedTelevisionShow.getCreatedBy());

        if (existingTelevisionShow.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update television show because television show does not exist: " + updatedTelevisionShow);
        }
        if (verifyIfTelevisionShowUpdated(existingTelevisionShow.get(), updatedTelevisionShow)) {
            throw new NoChangesToUpdateException("No updates in television show to save. Will not proceed with update. Existing Television Show: " + existingTelevisionShow + "Updated Television Show: " + updatedTelevisionShow);
        }
        log.info("Initiating tv show PUT request for tv show with ID: {}. Updated tv show details: {}", updatedTelevisionShow.getId(), updatedTelevisionShow);
        MediaResponse response = TelevisionShowMapper.INSTANCE.mapTelevisionShowToMediaResponse(dao.createOrUpdate(updatedTelevisionShow));
        log.info("TV show with ID: {} updated successfully", updatedTelevisionShow.getId());
        return response;
    }

    public Long deleteById(Long id, String username){
        log.info("Initiating tv show DELTE request for tv show with ID: {} by user: {}", id, username);
        Optional<TelevisionShow> televisionShow = getById(id, username);
        if (televisionShow.isEmpty()) {
            log.warn("Attempting to delete a tv show that does not exist. TV show ID: {}, User: {}", id, username);
            throw new ResourceNotFoundException("Cannot delete television show because television show does not exist.");
        }
        dao.deleteById(id, username);
        log.info("TV show with ID: {} deleted successfully by user: {}", id, username);
        return televisionShow.get().getId();
    }

    private boolean verifyIfTelevisionShowUpdated(TelevisionShow existingTelevisionShow, TelevisionShow updatedTelevisionShow) {
        return existingTelevisionShow.equals(updatedTelevisionShow);
    }
}
