package com.inventory.app.server.controller.media;

import com.inventory.app.server.entity.collection.Collection;
import com.inventory.app.server.entity.collection.CollectionMedia;
import com.inventory.app.server.entity.payload.request.SearchCollectionRequest;
import com.inventory.app.server.service.collection.CollectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/collection")
@Slf4j
public class CollectionController {
    private CollectionService collectionService;

    @Autowired
    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Collection>> searchCollections(@RequestBody SearchCollectionRequest searchCollectionRequest) {
        log.info("Received search request with criteria: {}", searchCollectionRequest);
        try {
            List<Collection> collections = collectionService.searchCollections(searchCollectionRequest);
            log.info("Search completed successfully. Number of collections found: {}", collections.size());
            return ResponseEntity.status(HttpStatus.OK).body(collections);
        } catch (Exception e) {
            log.error("Error occurred while searching collections: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while searching collections.", e);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Collection> createCollection(@RequestBody CollectionMedia resource){
        log.info("Received request to create a collection with resource: {}", resource);
        try {
            Collection createdCollection = collectionService.create(resource.getCollection());
            log.info("Collection created successfully with ID: {}", createdCollection.getId());
            return new ResponseEntity<>(createdCollection, HttpStatus.CREATED);
        } catch (NullPointerException e) {
            log.error("Bad request resource: {}", resource, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request resource: " + resource, e);
        }
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Collection> updateCollection(@RequestBody CollectionMedia resource){
        log.info("Received request to update a collection with resource: {}", resource);
        try {
            Collection updatedCollection = collectionService.update(resource.getCollection());
            log.info("Collection updated successfully with ID: {}", updatedCollection.getId());
            return ResponseEntity.status(HttpStatus.OK).body(updatedCollection);
        } catch (NullPointerException e) {
            log.error("Bad request resource: {}", resource, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request resource: " + resource, e);
        }
    }
}
