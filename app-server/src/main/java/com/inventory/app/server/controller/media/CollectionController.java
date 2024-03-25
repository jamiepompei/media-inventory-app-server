package com.inventory.app.server.controller.media;

import com.inventory.app.server.entity.collection.CollectionDetails;
import com.inventory.app.server.entity.media.Media;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.service.collection.CollectionDetailsService;
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
        return ResponseEntity.status(HttpStatus.OK).body(collectionService.getAllCollectionsByUser());
    }

    @GetMapping(value = "/collection/all/{collection_name}")
    ResponseEntity<List<Media>> findAllMediaByCollectionName(@PathVariable("collection_name") final String collectionName){
        return ResponseEntity.status(HttpStatus.OK).body(collectionService.getAllMediaByCollectionName());
    }

    @GetMapping(value = "/collection/{collection_name}")
    ResponseEntity<String> findByCollectionName(@PathVariable("collection_name") final String collectionName){
        try{
        } catch (ResourceNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for " + collectionName);
        }
        return ResponseEntity.status(HttpStatus.OK).body(collectionName);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CollectionDetails> createCollection(@RequestBody CollectionDetails resource){
        try{
        } catch (NullPointerException e){
            throw new ResponseStatusException((HttpStatus.BAD_REQUEST), "Bad request resource: " + resource);
        }
        return new ResponseEntity<>(collectionService.create(resource), HttpStatus.CREATED);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CollectionDetails> updateCollection(@RequestBody CollectionDetails resource){
        try{
        } catch (NullPointerException e){
            throw new ResponseStatusException((HttpStatus.BAD_REQUEST), "Bad request resource: " + resource);
        }
        return ResponseEntity.status(HttpStatus.OK).body(collectionService.update(resource));
    }
}
