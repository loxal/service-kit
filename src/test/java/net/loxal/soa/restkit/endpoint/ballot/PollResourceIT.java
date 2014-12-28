/*
 * Copyright 2014 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint.ballot;

import net.loxal.soa.restkit.endpoint.AbstractEndpointTest;
import net.loxal.soa.restkit.endpoint.Endpoint;
import net.loxal.soa.restkit.model.ballot.Poll;
import net.loxal.soa.restkit.model.common.ErrorMessage;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PollResourceIT extends AbstractEndpointTest {
    private static final String POLL_QUESTION = "What is the meaning of life?";
    private static final List<String> POLL_ANSWERS = Arrays.asList("Yes", "No");

    static Response createPoll() {
        Poll poll = new Poll(POLL_QUESTION, POLL_ANSWERS);

        Response createdPoll = prepareGenericRequest(PollResource.RESOURCE_PATH).request().post(Entity.json(poll));
        assertEquals(Response.Status.CREATED.getStatusCode(), createdPoll.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, createdPoll.getMediaType());

        return createdPoll;
    }

    @Test
    public void createNewPoll() throws Exception {
        Response response = createPoll();

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertEquals(false, response.getLocation().getPath().endsWith("null"));
        assertEquals(true, response.getLocation().getPath().startsWith(Endpoint.URI_PATH_SEPARATOR + PollResource.RESOURCE_PATH));
        assertEquals(true, response.getLocation().getSchemeSpecificPart().contains(PollResource.RESOURCE_PATH + Endpoint.URI_PATH_SEPARATOR));
        assertEquals(false, response.getLocation().getSchemeSpecificPart().endsWith(PollResource.RESOURCE_PATH));
    }

    @Test
    public void deleteNonExistentPoll() throws Exception {
        Response existingPoll = createPoll();

        Response response = prepareTarget(existingPoll.getLocation() + NON_EXISTENT).request().delete();

        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        Response.Status notFoundStatus = Response.Status.NOT_FOUND;
        assertEquals(notFoundStatus.getStatusCode(), response.getStatus());
        ErrorMessage errorMessage = response.readEntity(ErrorMessage.class);
        assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), errorMessage.getType());
    }

    @Test
    public void deleteExistingPoll() throws Exception {
        Response existingPoll = createPoll();

        Response deletion = prepareTarget(existingPoll.getLocation()).request().delete();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deletion.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, deletion.getMediaType());
    }

    @Test
    public void retrieveExistingPoll() throws Exception {
        Response existingPoll = createPoll();

        Response retrieval = prepareTarget(existingPoll.getLocation()).request().get();

        assertEquals(Response.Status.OK.getStatusCode(), retrieval.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, retrieval.getMediaType());
        Poll retrievedPoll = retrieval.readEntity(Poll.class);
        assertEquals(POLL_QUESTION, retrievedPoll.getQuestion());
        assertEquals(POLL_ANSWERS, retrievedPoll.getAnswers());
        assertEquals(2, retrievedPoll.getAnswers().size());
        assertEquals("Yes", retrievedPoll.getAnswers().get(0));
        assertEquals("No", retrievedPoll.getAnswers().get(1));
    }

    @Test
    public void retrieveNonExistentPoll() throws Exception {
        Response existingPoll = createPoll();

        Response retrieval = prepareTarget(existingPoll.getLocation() + NON_EXISTENT).request().get();


        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), retrieval.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, retrieval.getMediaType());
        ErrorMessage notFoundError = retrieval.readEntity(ErrorMessage.class);
        assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), notFoundError.getType());
    }

    @Test
    public void updateExistingPoll() throws Exception {
        Response existingPoll = createPoll();

        String newQuestion = "Does it work?";
        String firstAnswerOption = "Maybe";
        String secondAnswerOption = "Somewhat";
        String thirdAnswerOption = "Absolutely Not";
        List<String> newAnswerOptions = Arrays.asList(firstAnswerOption, secondAnswerOption, thirdAnswerOption);

        Poll modifiedPoll = new Poll(newQuestion, newAnswerOptions);

        Response update = prepareTarget(existingPoll.getLocation()).request().put(Entity.json(modifiedPoll));

        assertEquals(Response.Status.OK.getStatusCode(), update.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, update.getMediaType());

        Response retrievedUpdatedPoll = prepareTarget(existingPoll.getLocation()).request().get();
        Poll updatedPoll = retrievedUpdatedPoll.readEntity(Poll.class);
        assertEquals(newQuestion, updatedPoll.getQuestion());
        assertEquals(newAnswerOptions.size(), updatedPoll.getAnswers().size());
        assertEquals(firstAnswerOption, updatedPoll.getAnswers().get(0));
        assertEquals(secondAnswerOption, updatedPoll.getAnswers().get(1));
        assertEquals(thirdAnswerOption, updatedPoll.getAnswers().get(2));
    }

    @Test
    public void updateNonExistentPoll() throws Exception {
        Response existingPoll = createPoll();

        Poll somePoll = new Poll("Irrelevant", Collections.emptyList());
        Response update = prepareTarget(existingPoll.getLocation() + NON_EXISTENT).request().put(Entity.json(somePoll));

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), update.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, update.getMediaType());
    }
}