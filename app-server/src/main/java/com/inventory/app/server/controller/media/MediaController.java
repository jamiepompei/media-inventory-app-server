package com.inventory.app.server.controller.media;

import com.inventory.app.server.entity.payload.request.DeleteMediaRequest;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.entity.payload.request.UpdateCreateMediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.factory.ServiceFactory;
import com.inventory.app.server.service.media.BaseService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.inventory.app.server.util.MediaServerUtility.setUsername;

@RestController
@RequestMapping("/media")
@Log4j2
public class MediaController<T> {

    private final ServiceFactory serviceFactory;

    public MediaController(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

   // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    @GetMapping
    ResponseEntity<List<MediaResponse>> search(@AuthenticationPrincipal UserDetails userDetails,
                                                    @Valid @RequestBody final SearchMediaRequest searchMediaRequest) {
        BaseService<T> service = serviceFactory.getService(searchMediaRequest.getEntityType());
        setUsername(userDetails, searchMediaRequest);
        List<MediaResponse> responseList = (List<MediaResponse>) service.search(searchMediaRequest);
                
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }



    @PostMapping
   // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MediaResponse> createMedia(//@AuthenticationPrincipal UserDetails userDetails,
                                                    @Valid @RequestBody final UpdateCreateMediaRequest updateCreateMediaRequest) {
        log.info("Received request to create resource: " + updateCreateMediaRequest);
        try {
            //  setUsername(userDetails, updateCreateMediaRequest);
            BaseService<T> service = serviceFactory.getService(updateCreateMediaRequest.getEntityType());
            MediaResponse response = (MediaResponse) service.create(updateCreateMediaRequest);
            log.info("Created new book: " + response);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating resource: " + e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while creating the resource.");
        }
    }

    @PutMapping(value = "/{id}")
  //  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> updateMedia(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody final UpdateCreateMediaRequest updateMediaRequest) {
        log.info("received request to update resource: " + updateMediaRequest);
        setUsername(userDetails, updateMediaRequest);
        BaseService<T> service = serviceFactory.getService(updateMediaRequest.getEntityType());
        MediaResponse response = service.update(updateMediaRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{id}")
   // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Long> deleteMedia(@AuthenticationPrincipal UserDetails userDetails,
                                                    @PathVariable("id") final DeleteMediaRequest deleteMediaRequest){
        if (deleteMediaRequest.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Id cannot be null or empty.");
        }
        setUsername(userDetails, deleteMediaRequest);
        BaseService<T> service = serviceFactory.getService(deleteMediaRequest.getEntityType());
        Long response = service.deleteById(deleteMediaRequest.getId(), userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
