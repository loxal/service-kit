/*
 * Copyright 2014 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint;

import net.loxal.soa.restkit.model.common.ErrorMessage;
import net.loxal.soa.restkit.model.generic.Group;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GroupResourceIT extends AbstractEndpointTest {

    private static final List<String> ENTRIES = Arrays.asList("first", "second", "third");

    private Response postEntity() {
        Group group = new Group("name", ENTRIES);

        Response createdEntity = prepareGenericRequest(GroupResource.RESOURCE_PATH).request().post(Entity.json(group));
        assertEquals(Response.Status.CREATED.getStatusCode(), createdEntity.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, createdEntity.getMediaType());

        return createdEntity;
    }

    @Test
    public void createEntity() throws Exception {
        Response response = postEntity();

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertEquals(false, response.getLocation().getPath().endsWith("null"));
        assertEquals(true, response.getLocation().getPath().startsWith(Endpoint.URI_PATH_SEPARATOR + GroupResource.RESOURCE_PATH));
        assertEquals(true, response.getLocation().getSchemeSpecificPart().contains(GroupResource.RESOURCE_PATH + Endpoint.URI_PATH_SEPARATOR));
        assertEquals(false, response.getLocation().getSchemeSpecificPart().endsWith(GroupResource.RESOURCE_PATH));
    }

    @Test
    public void deleteNonExistentEntity() throws Exception {
        Response existingEntity = postEntity();

        Response response = prepareTarget(existingEntity.getLocation() + NON_EXISTENT).request().delete();

        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        Response.Status notFoundStatus = Response.Status.NOT_FOUND;
        assertEquals(notFoundStatus.getStatusCode(), response.getStatus());
        ErrorMessage errorMessage = response.readEntity(ErrorMessage.class);
        assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), errorMessage.getType());
    }

    @Test
    public void deleteExistingEntity() throws Exception {
        Response existingEntity = postEntity();

        Response deletion = prepareTarget(existingEntity.getLocation()).request().delete();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deletion.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, deletion.getMediaType());
    }

    @Test
    public void retrieveExistingEntity() throws Exception {
        Response existingEntity = postEntity();

        Response retrieval = prepareTarget(existingEntity.getLocation()).request().get();

        assertEquals(Response.Status.OK.getStatusCode(), retrieval.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, retrieval.getMediaType());
        Group retrievedEntity = retrieval.readEntity(Group.class);
        assertEquals(ENTRIES.size(), retrievedEntity.getEntityReferences().size());
        assertEquals(false, retrievedEntity.getEntityReferences().isEmpty());
    }

    @Test
    public void retrieveNonExistentEntity() throws Exception {
        Response existingEntity = postEntity();

        Response retrieval = prepareTarget(existingEntity.getLocation() + NON_EXISTENT).request().get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), retrieval.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, retrieval.getMediaType());
        ErrorMessage notFoundError = retrieval.readEntity(ErrorMessage.class);
        assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), notFoundError.getType());
    }

    @Test
    public void updateExistingEntity() throws Exception {
        Response existingEntity = postEntity();

        String updatedField = "updated field";
        List<String> entries = Arrays.asList("first", "second");
        Group modifiedEntity = new Group(updatedField, entries);

        Response update = prepareTarget(existingEntity.getLocation()).request().put(Entity.json(modifiedEntity));

        assertEquals(Response.Status.OK.getStatusCode(), update.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, update.getMediaType());

        Response retrievedUpdatedEntity = prepareTarget(existingEntity.getLocation()).request().get();
        Group updatedEntity = retrievedUpdatedEntity.readEntity(Group.class);
        assertEquals(updatedField, updatedEntity.getName());
        assertEquals(entries.size(), updatedEntity.getEntityReferences().size());
        assertEquals(entries, updatedEntity.getEntityReferences());
    }

    @Test
    public void updateNonExistentEntity() throws Exception {
        Response existingEntity = postEntity();

        Group someEntity = new Group("Irrelevant", Collections.emptyList());
        Response update = prepareTarget(existingEntity.getLocation() + NON_EXISTENT).request().put(Entity.json(someEntity));

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), update.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, update.getMediaType());
    }
}