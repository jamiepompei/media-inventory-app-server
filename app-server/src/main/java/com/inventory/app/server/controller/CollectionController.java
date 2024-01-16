package com.inventory.app.server.controller;

import com.google.common.base.Preconditions;
import com.inventory.app.server.entity.CollectionDetails;
import com.inventory.app.server.entity.Media;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.service.collection.CollectionDetailsService;
import com.inventory.app.server.utility.RestPreConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/collection")
public class CollectionController {
    private CollectionDetailsService collectionService;

    @Autowired
    public CollectionController(CollectionDetailsService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping
    ResponseEntity<List<CollectionDetails>> findAllCollections(){
        return ResponseEntity.status(HttpStatus.OK).body(collectionService.getAllCollections());
    }

    @GetMapping
    ResponseEntity<List<Media>> findAllMediaByCollectionName(@PathVariable("collection_name") final String collectionName){
        return ResponseEntity.status(HttpStatus.OK).body(collectionService.getAllMediaByCollectionName());
    }

    @GetMapping
    ResponseEntity<String> findByCollectionName(@PathVariable("collection_name") final String collectionName){
        try{
        RestPreConditions.checkFound(collectionService.findByTitle(collectionName));
        } catch (ResourceNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for " + collectionName);
        }
        return ResponseEntity.status(HttpStatus.OK).body(collectionName);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CollectionDetails> createCollection(@RequestBody CollectionDetails resource){
        try{
            Preconditions.checkNotNull(resource);
        } catch (NullPointerException e){
            throw new ResponseStatusException((HttpStatus.BAD_REQUEST), "Bad request resource: " + resource);
        }
        return new ResponseEntity<>(collectionService.create(resource), HttpStatus.CREATED);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CollectionDetails> updateCollection(@RequestBody CollectionDetails resource){
        try{
            Preconditions.checkNotNull(resource);
        } catch (NullPointerException e){
            throw new ResponseStatusException((HttpStatus.BAD_REQUEST), "Bad request resource: " + resource);
        }
        return ResponseEntity.status(HttpStatus.OK).body(collectionService.update(resource));
    }
}
