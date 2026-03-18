package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.entity.payload.request.UpdateCreateMediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;

import java.util.List;
import java.util.Optional;

public interface BaseService<T> {
    Optional<T> getById(Long id, String username);
    MediaResponse create (UpdateCreateMediaRequest updateCreateMediaRequest);
    List<MediaResponse> search(SearchMediaRequest searchMediaRequest);
    MediaResponse update(UpdateCreateMediaRequest updateCreateMediaRequest);
    Long deleteById(Long id, String username);
}
