package com.inventory.app.server.controller.media;

import com.inventory.app.server.entity.payload.request.DeleteMediaRequest;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.entity.payload.request.UpdateCreateMediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.factory.ServiceFactory;
import com.inventory.app.server.service.media.BaseService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.inventory.app.server.util.MediaServerUtility.setUsername;

@RestController
@RequestMapping("/media")
@Slf4j
public class MediaController<T> {

    private final ServiceFactory serviceFactory;

    public MediaController(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    @GetMapping
    ResponseEntity<List<MediaResponse>> search(@AuthenticationPrincipal UserDetails userDetails,
                                               @Valid @RequestBody final SearchMediaRequest searchMediaRequest) {
        log.info("Received search request for entity type: {} by user: {}", searchMediaRequest.getEntityType(), userDetails.getUsername());
        BaseService<T> service = serviceFactory.getService(searchMediaRequest.getEntityType());
        setUsername(userDetails, searchMediaRequest);
        log.info("Delegating search request to service for entity type: {}", searchMediaRequest.getEntityType());
        List<MediaResponse> responseList = (List<MediaResponse>) service.search(searchMediaRequest);
        log.info("Search completed successfully. Number of results: {}", responseList.size());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MediaResponse> createMedia(@AuthenticationPrincipal UserDetails userDetails,
                                                     @Valid @RequestBody final UpdateCreateMediaRequest updateCreateMediaRequest) {
        log.info("Received create request for entity type: {}", updateCreateMediaRequest.getEntityType());
        try {
            setUsername(userDetails, updateCreateMediaRequest);
            log.info("Delegating create request to service for entity type: {}", updateCreateMediaRequest.getEntityType());
            BaseService<T> service = serviceFactory.getService(updateCreateMediaRequest.getEntityType());
            MediaResponse response = (MediaResponse) service.create(updateCreateMediaRequest);
            log.info("Resource created successfully with ID: {}", response.getId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error occurred while creating resource: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while creating the resource.");
        }
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> updateMedia(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody final UpdateCreateMediaRequest updateMediaRequest) {
        log.info("Received update request for entity type: {}", updateMediaRequest.getEntityType());
        try {
            setUsername(userDetails, updateMediaRequest);
            log.info("Delegating update request to service for entity type: {}", updateMediaRequest.getEntityType());
            BaseService<T> service = serviceFactory.getService(updateMediaRequest.getEntityType());
            MediaResponse response = service.update(updateMediaRequest);
            log.info("Resource updated successfully with ID: {}", response.getId());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error occurred while updating resource: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while updating the resource.");
        }
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Long> deleteMedia(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable("id") final DeleteMediaRequest deleteMediaRequest) {
        if (deleteMediaRequest.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Id cannot be null or empty.");
        }
        log.info("Received delete request for entity type: {} with ID: {} by user: {}", deleteMediaRequest.getEntityType(), deleteMediaRequest.getId(), userDetails.getUsername());
        setUsername(userDetails, deleteMediaRequest);
        BaseService<T> service = serviceFactory.getService(deleteMediaRequest.getEntityType());
        log.info("Delegating delete request to service for entity type: {}", deleteMediaRequest.getEntityType());
        Long response = service.deleteById(deleteMediaRequest.getId(), userDetails.getUsername());
        log.info("Resource with ID: {} deleted successfully by user: {}", response, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
