/*
 * Copyright 2014 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint.ballot;

import net.loxal.soa.restkit.endpoint.AbstractEndpointTest;
import net.loxal.soa.restkit.endpoint.Endpoint;
import net.loxal.soa.restkit.model.ballot.Vote;
import net.loxal.soa.restkit.model.common.ErrorMessage;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class VoteResourceIT extends AbstractEndpointTest {
    private static final int ANSWER_OPTION_INDEX = 1;

    @Test
    public void createVote() throws Exception {
        Response response = createVoteAssignedToPoll();

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertEquals(false, response.getLocation().getPath().endsWith("null"));
        assertEquals(true, response.getLocation().getPath().startsWith(Endpoint.URI_PATH_SEPARATOR + VoteResource.RESOURCE_PATH));
        assertEquals(true, response.getLocation().getSchemeSpecificPart().contains(VoteResource.RESOURCE_PATH + Endpoint.URI_PATH_SEPARATOR));
        assertEquals(false, response.getLocation().getSchemeSpecificPart().endsWith(VoteResource.RESOURCE_PATH));
    }

    @Test
    public void deleteNonExistentVote() throws Exception {
        Response existingVote = createVoteAssignedToPoll();

        Response response = prepareTarget(existingVote.getLocation() + NON_EXISTENT).request().delete();

        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        Response.Status notFoundStatus = Response.Status.NOT_FOUND;
        assertEquals(notFoundStatus.getStatusCode(), response.getStatus());
        ErrorMessage errorMessage = response.readEntity(ErrorMessage.class);
        assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), errorMessage.getType());
    }

    @Test
    public void deleteExistingVote() throws Exception {
        Response existingVote = createVoteAssignedToPoll();

        Response deletion = prepareTarget(existingVote.getLocation()).request().delete();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deletion.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, deletion.getMediaType());
    }

    @Test
    public void retrieveExistingVote() throws Exception {
        Response existingVote = createVoteAssignedToPoll();

        Response retrieval = prepareTarget(existingVote.getLocation()).request().get();

        assertEquals(Response.Status.OK.getStatusCode(), retrieval.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, retrieval.getMediaType());
        Vote retrievedVote = retrieval.readEntity(Vote.class);
        assertEquals(ANSWER_OPTION_INDEX, retrievedVote.getAnswerOptionIndex());
        assertEquals(false, retrievedVote.getReferencePoll().isEmpty());
    }

    @Test
    public void retrieveNonExistentVote() throws Exception {
        Response existingVote = createVoteAssignedToPoll();

        Response retrieval = prepareTarget(existingVote.getLocation() + NON_EXISTENT).request().get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), retrieval.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, retrieval.getMediaType());
        ErrorMessage notFoundError = retrieval.readEntity(ErrorMessage.class);
        assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), notFoundError.getType());
    }

    @Test
    public void updateExistingVote() throws Exception {
        Response existingVote = createVoteAssignedToPoll();

        String updatedReference = "updated reference";
        int updatedAnswerOptionIndex = 2;
        Vote modifiedVote = new Vote(updatedReference, updatedAnswerOptionIndex);

        Response update = prepareTarget(existingVote.getLocation()).request().put(Entity.json(modifiedVote));

        assertEquals(Response.Status.OK.getStatusCode(), update.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, update.getMediaType());

        Response retrievedUpdatedPoll = prepareTarget(existingVote.getLocation()).request().get();
        Vote updatedVote = retrievedUpdatedPoll.readEntity(Vote.class);
        assertEquals(updatedReference, updatedVote.getReferencePoll());
        assertEquals(updatedAnswerOptionIndex, updatedVote.getAnswerOptionIndex());
    }

    @Test
    public void updateNonExistentVote() throws Exception {
        Response existingVote = createVoteAssignedToPoll();

        Vote someVote = new Vote("Irrelevant", Integer.MAX_VALUE);
        Response update = prepareTarget(existingVote.getLocation() + NON_EXISTENT).request().put(Entity.json(someVote));

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), update.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, update.getMediaType());
    }

    private Response createVoteAssignedToPoll() {
        Response poll = PollResourceIT.createPoll();
        Vote vote = new Vote(poll.getLocation().toString(), ANSWER_OPTION_INDEX);

        Response createdVote = prepareGenericRequest(VoteResource.RESOURCE_PATH).request().post(Entity.json(vote));
        assertEquals(Response.Status.CREATED.getStatusCode(), createdVote.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, createdVote.getMediaType());

        return createdVote;
    }
}