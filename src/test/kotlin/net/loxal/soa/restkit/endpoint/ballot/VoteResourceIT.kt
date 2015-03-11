/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint.ballot

import net.loxal.soa.restkit.endpoint.AbstractEndpointTest
import org.junit.Test
import net.loxal.soa.restkit.model.common.ErrorMessage
import net.loxal.soa.restkit.model.ballot.Vote
import javax.ws.rs.core.Response
import javax.ws.rs.core.MediaType
import net.loxal.soa.restkit.endpoint.Endpoint
import javax.ws.rs.client.Entity
import kotlin.test.assertEquals

public class VoteResourceIT : AbstractEndpointTest() {

    Test
    fun createVote() {
        val response = createVoteAssignedToPoll()

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType())
        assertEquals(false, response.getLocation().getPath().endsWith("null"))
        assertEquals(true, response.getLocation().getPath().startsWith(Endpoint.URI_PATH_SEPARATOR + VoteResource.RESOURCE_PATH))
        assertEquals(true, response.getLocation().getSchemeSpecificPart().contains(VoteResource.RESOURCE_PATH + Endpoint.URI_PATH_SEPARATOR))
        assertEquals(false, response.getLocation().getSchemeSpecificPart().endsWith(VoteResource.RESOURCE_PATH))
    }

    Test
    fun deleteNonExistentVote() {
        val existingEntity = createVoteAssignedToPoll()

        val response = AbstractEndpointTest.prepareTarget("${existingEntity.getLocation()} ${AbstractEndpointTest.NON_EXISTENT}").request().delete()

        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType())
        val notFoundStatus = Response.Status.NOT_FOUND
        assertEquals(notFoundStatus.getStatusCode(), response.getStatus())
        val errorMessage = response.readEntity<ErrorMessage>(javaClass<ErrorMessage>())
        assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), errorMessage.type)
    }

    Test
    fun deleteExistingVote() {
        val existingVote = createVoteAssignedToPoll()

        val deletion = AbstractEndpointTest.prepareTarget(existingVote.getLocation()).request().delete()

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deletion.getStatus())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, deletion.getMediaType())
    }

    Test
    fun retrieveExistingVote() {
        val existingVote = createVoteAssignedToPoll()

        val retrieval = AbstractEndpointTest.prepareTarget(existingVote.getLocation()).request().get()

        assertEquals(Response.Status.OK.getStatusCode(), retrieval.getStatus())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, retrieval.getMediaType())

        val retrievedVote = retrieval.readEntity(javaClass<Vote>())
        assertEquals(ANSWER_OPTION_INDEX.toLong(), retrievedVote.answerOptionIndex.toLong())
        assertEquals(false, retrievedVote.referencePoll.isEmpty())
        assertEquals("anonymous", retrievedVote.user)
    }

    Test
    fun retrieveExistingNonAnonymousVote() {
        val user = "Specific User"
        val existingVote = createUserVoteAssignedToPoll(user)

        val retrieval = AbstractEndpointTest.prepareTarget(existingVote.getLocation()).request().get()

        assertEquals(Response.Status.OK.getStatusCode(), retrieval.getStatus())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, retrieval.getMediaType())

        val retrievedVote = retrieval.readEntity(javaClass<Vote>())
        assertEquals(ANSWER_OPTION_INDEX.toLong(), retrievedVote.answerOptionIndex.toLong())
        assertEquals(false, retrievedVote.referencePoll.isEmpty())
        assertEquals(user, retrievedVote.user)
    }

    Test
    fun retrieveNonExistentVote() {
        val existingEntity = createVoteAssignedToPoll()

        val retrieval = AbstractEndpointTest.prepareTarget("${existingEntity.getLocation()} ${AbstractEndpointTest.NON_EXISTENT}").request().get()

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), retrieval.getStatus())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, retrieval.getMediaType())
        val notFoundError = retrieval.readEntity<ErrorMessage>(javaClass<ErrorMessage>())
        assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), notFoundError.type)
    }

    Test
    fun updateExistingVote() {
        val existingVote = createVoteAssignedToPoll()

        val updatedReference = "updated reference"
        val updatedAnswerOptionIndex = 2
        val modifiedVote = Vote(updatedReference, updatedAnswerOptionIndex)

        val update = AbstractEndpointTest.prepareTarget(existingVote.getLocation()).request().put(Entity.json<Vote>(modifiedVote))

        assertEquals(Response.Status.OK.getStatusCode(), update.getStatus())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, update.getMediaType())

        val retrievedUpdatedPoll = AbstractEndpointTest.prepareTarget(existingVote.getLocation()).request().get()
        val updatedVote = retrievedUpdatedPoll.readEntity<net.loxal.soa.restkit.model.ballot.Vote>(javaClass<Vote>())
        assertEquals(updatedReference, updatedVote.referencePoll)
        assertEquals(updatedAnswerOptionIndex.toLong(), updatedVote.answerOptionIndex.toLong())
    }

    Test
    fun updateNonExistentVote() {
        val existingEntity = createVoteAssignedToPoll()

        val someVote = Vote("Irrelevant", Integer.MAX_VALUE)
        val update = AbstractEndpointTest.prepareTarget("${existingEntity.getLocation()} ${AbstractEndpointTest.NON_EXISTENT}").request().put(Entity.json<Vote>(someVote))

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), update.getStatus())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, update.getMediaType())
    }

    private fun createVoteAssignedToPoll(): Response {
        val poll = PollResourceIT.createPoll()
        val vote = Vote(poll.getLocation().toString(), ANSWER_OPTION_INDEX)

        val createdVote = AbstractEndpointTest.prepareGenericRequest(VoteResource.RESOURCE_PATH).request().post(Entity.json<Vote>(vote))
        assertEquals(Response.Status.CREATED.getStatusCode(), createdVote.getStatus())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, createdVote.getMediaType())

        return createdVote
    }

    private fun createUserVoteAssignedToPoll(user: String): Response {
        val poll = PollResourceIT.createPoll()
        val vote = Vote.asUser(poll.getLocation().toString(), ANSWER_OPTION_INDEX, user)

        val createdVote = AbstractEndpointTest.prepareGenericRequest(VoteResource.RESOURCE_PATH).request().post(Entity.json<Vote>(vote))
        assertEquals(Response.Status.CREATED.getStatusCode(), createdVote.getStatus())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, createdVote.getMediaType())

        return createdVote
    }

    class object {
        private val ANSWER_OPTION_INDEX = 1
    }
}