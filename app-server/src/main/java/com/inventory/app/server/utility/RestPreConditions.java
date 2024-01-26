package com.inventory.app.server.utility;

import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;

public final class RestPreConditions {

    private RestPreConditions() {
        throw new AssertionError();
    }

    /**
     * Check if some value was found, otherwise throw exception.
     *
     * @param expression
     *            has value true if found, otherwise false
     * @throws ResourceNotFoundException
     *             if expression is false, means value not found.
     */
    public static void checkFound(final boolean expression) {
        if (!expression) {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * Check if some value was found, otherwise throw exception.
     *
     * @throws ResourceNotFoundException
     *             if expression is false, means value not found.
     */
    public static <T> T checkFound(final T resource) {
        if (resource == null) {
            throw new ResourceNotFoundException("Resource not found: " + resource);
        }
        return resource;
    }

    public static <T> T checkEquals(final T existingResource, final T updatedResource) {
        if (updatedResource.equals(existingResource)) {
            throw new NoChangesToUpdateException("No updates in resource to save. Will not proceed with update. Existing Resource: " + existingResource + "Updated Resource: " + updatedResource);
        }
        return updatedResource;
    }

    public static <T> T checkAlreadyExists(final boolean expression, final T resource) {
        if (expression) {
            throw new ResourceAlreadyExistsException("Resource already exists. Will not proceed with create. Resource: " + resource.toString());
        }
        return resource;
    }

}
