/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint.ballot

import net.loxal.soa.restkit.endpoint.AbstractEndpointTest
import net.loxal.soa.restkit.endpoint.Endpoint
import net.loxal.soa.restkit.model.ballot.Vote
import net.loxal.soa.restkit.model.common.Creation
import net.loxal.soa.restkit.model.common.ErrorMessage
import org.junit.Test
import java.util.*
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import kotlin.test.assertEquals

class VoteResourceIT : AbstractEndpointTest() {

    @Test
    fun reviewWrongVote() {
        val poll = PollResourceIT.createEntity()
        val createdPoll = poll.readEntity(Creation::class.java)
        val vote = Vote(createdPoll.id, ANSWERS)

        val createdVote = AbstractEndpointTest.prepareGenericRequest(VoteResource.RESOURCE_PATH)
                .request()
                .post(Entity.json<Vote>(vote))
        assertEquals(Response.Status.CREATED.statusCode, createdVote.status)
        assertEquals(MediaType.APPLICATION_JSON_TYPE, createdVote.mediaType)

        val retrieval = AbstractEndpointTest.Companion.prepareTarget(createdVote.location).request().get()

        val retrievedVote = retrieval.readEntity(Vote::class.java)
        assertEquals(ANSWERS, retrievedVote.answers)
        assertEquals(false, retrievedVote.correct)
        assertEquals(false, retrievedVote.referencePoll.isEmpty())
        assertEquals("anonymous", retrievedVote.user)
    }

    @Test
    fun reviewCorrectVote() {
        val poll = PollResourceIT.createEntity()
        val createdPoll = poll.readEntity(Creation::class.java)
        val vote = Vote(createdPoll.id, CORRECT_ANSWERS)

        val createdVote = AbstractEndpointTest.prepareGenericRequest(VoteResource.RESOURCE_PATH)
                .request()
                .post(Entity.json<Vote>(vote))
        assertEquals(Response.Status.CREATED.statusCode, createdVote.status)
        assertEquals(MediaType.APPLICATION_JSON_TYPE, createdVote.mediaType)

        val retrieval = AbstractEndpointTest.Companion.prepareTarget(createdVote.location).request().get()

        val retrievedVote = retrieval.readEntity(Vote::class.java)
        assertEquals(CORRECT_ANSWERS, retrievedVote.answers)
        assertEquals(true, retrievedVote.correct)
        assertEquals(false, retrievedVote.referencePoll.isEmpty())
        assertEquals("anonymous", retrievedVote.user)
    }


    @Test
    fun createCorrectVoteWithoutReview() {
        val poll = PollResourceIT.createEntity()
        val createdPoll = poll.readEntity(Creation::class.java)
        val vote = Vote(createdPoll.id, ANSWERS)
        vote.correct = true

        val createdVote = AbstractEndpointTest.prepareGenericRequest(VoteResource.RESOURCE_PATH)
                .request()
                .post(Entity.json<Vote>(vote))
        assertEquals(Response.Status.CREATED.statusCode, createdVote.status)
        assertEquals(MediaType.APPLICATION_JSON_TYPE, createdVote.mediaType)

        val retrieval = AbstractEndpointTest.Companion.prepareTarget(createdVote.location).request().get()

        validateVote(retrieval)
    }

    @Test
    fun createVoteWithAutoId() {
        val vote = Vote(UUID.randomUUID().toString(), ANSWERS)

        val createdVote = AbstractEndpointTest.prepareGenericRequest(VoteResource.RESOURCE_PATH)
                .request()
                .post(Entity.json<Vote>(vote))
        assertEquals(Response.Status.CREATED.statusCode, createdVote.status)
        assertEquals(MediaType.APPLICATION_JSON_TYPE, createdVote.mediaType)
    }

    @Test
    fun createNullVote() {
        val createdVote = AbstractEndpointTest.prepareGenericRequest(VoteResource.RESOURCE_PATH)
                .request()
                .post(Entity.json(null))
        assertEquals(Response.Status.BAD_REQUEST.statusCode, createdVote.status)
        assertEquals(MediaType.APPLICATION_JSON_TYPE, createdVote.mediaType)
    }

    @Test
    fun createVote() {
        val response = createVoteAssignedToPoll()

        assertEquals(Response.Status.CREATED.statusCode, response.status)
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.mediaType)
        assertEquals(false, response.location.path.endsWith("null"))
        assertEquals(true, response.location.path.startsWith(Endpoint.URI_PATH_SEPARATOR + VoteResource.RESOURCE_PATH))
        assertEquals(true, response.location.schemeSpecificPart.contains(VoteResource.RESOURCE_PATH + Endpoint.URI_PATH_SEPARATOR))
        assertEquals(false, response.location.schemeSpecificPart.endsWith(VoteResource.RESOURCE_PATH))
    }

    @Test
    fun deleteNonExistentVote() {
        val existingEntity = createVoteAssignedToPoll()

        val response = AbstractEndpointTest.prepareTarget("${existingEntity.location} ${AbstractEndpointTest.NON_EXISTENT}").request().delete()

        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.mediaType)
        val notFoundStatus = Response.Status.NOT_FOUND
        assertEquals(notFoundStatus.statusCode, response.status)
        val errorMessage = response.readEntity(ErrorMessage::class.java)
        assertEquals(Response.Status.BAD_REQUEST.reasonPhrase, errorMessage.reasonPhrase)
    }

    @Test
    fun deleteExistingVote() {
        val existingVote = createVoteAssignedToPoll()

        val deletion = AbstractEndpointTest.prepareTarget(existingVote.location).request().delete()

        assertEquals(Response.Status.NO_CONTENT.statusCode, deletion.status)
        assertEquals(null, deletion.mediaType)
    }

    @Test
    fun retrieveExistingVote() {
        val existingVote = createVoteAssignedToPoll()

        val retrieval = AbstractEndpointTest.prepareTarget(existingVote.location).request().get()

        assertEquals(Response.Status.OK.statusCode, retrieval.status)
        assertEquals(MediaType.APPLICATION_JSON_TYPE, retrieval.mediaType)

        validateVote(retrieval)
    }

    private fun validateVote(retrieval: Response) {
        val retrievedVote = retrieval.readEntity(Vote::class.java)
        assertEquals(ANSWERS, retrievedVote.answers)
        assertEquals(false, retrievedVote.correct)
        assertEquals(false, retrievedVote.referencePoll.isEmpty())
        assertEquals("anonymous", retrievedVote.user)
    }

    @Test
    fun retrieveExistingNonAnonymousVote() {
        val user = "Specific User"
        val existingVote = createUserVoteAssignedToPoll(user)

        val retrieval = AbstractEndpointTest.prepareTarget(existingVote.location).request().get()

        assertEquals(Response.Status.OK.statusCode, retrieval.status)
        assertEquals(MediaType.APPLICATION_JSON_TYPE, retrieval.mediaType)

        val retrievedVote = retrieval.readEntity(Vote::class.java)
        assertEquals(ANSWERS, retrievedVote.answers)
        assertEquals(false, retrievedVote.correct)
        assertEquals(false, retrievedVote.referencePoll.isEmpty())
        assertEquals(user, retrievedVote.user)
    }

    @Test
    fun retrieveNonExistentVote() {
        val existingEntity = createVoteAssignedToPoll()

        val retrieval = AbstractEndpointTest.prepareTarget("${existingEntity.location} ${AbstractEndpointTest.NON_EXISTENT}").request().get()

        assertEquals(Response.Status.NOT_FOUND.statusCode, retrieval.status)
        assertEquals(MediaType.APPLICATION_JSON_TYPE, retrieval.mediaType)
        val notFoundError = retrieval.readEntity(ErrorMessage::class.java)
        assertEquals(Response.Status.BAD_REQUEST.reasonPhrase, notFoundError.reasonPhrase)
    }

    @Test
    fun updateExistingVote() {
        val existingVote = createVoteAssignedToPoll()

        val updatedReference = "updated reference"
        val updatedAnswerOptions = listOf(2)
        val modifiedVote = Vote(updatedReference, updatedAnswerOptions)

        val update = AbstractEndpointTest.prepareTarget(existingVote.location).request().put(Entity.json<Vote>(modifiedVote))

        assertEquals(Response.Status.OK.statusCode, update.status)
        assertEquals(MediaType.APPLICATION_JSON_TYPE, update.mediaType)

        val retrievedUpdatedPoll = AbstractEndpointTest.prepareTarget(existingVote.location).request().get()
        val updatedVote = retrievedUpdatedPoll.readEntity<net.loxal.soa.restkit.model.ballot.Vote>(Vote::class.java)
        assertEquals(updatedReference, updatedVote.referencePoll)
        assertEquals(updatedAnswerOptions, updatedVote.answers)
    }

    @Test
    fun updateNonExistentVote() {
        val existingEntity = createVoteAssignedToPoll()

        val someVote = Vote("Irrelevant", listOf(Integer.MAX_VALUE))
        val update = AbstractEndpointTest.prepareTarget("${existingEntity.location} ${AbstractEndpointTest.NON_EXISTENT}").request().put(Entity.json<Vote>(someVote))

        assertEquals(Response.Status.NOT_FOUND.statusCode, update.status)
        assertEquals(MediaType.APPLICATION_JSON_TYPE, update.mediaType)
    }

    private fun createVoteAssignedToPoll(): Response {
        val poll = PollResourceIT.createEntity()
        val pollCreated = poll.readEntity(Creation::class.java)
        val vote = Vote(pollCreated.id, ANSWERS)

        val createdVote = AbstractEndpointTest.prepareGenericRequest(VoteResource.RESOURCE_PATH)
                .path(UUID.randomUUID().toString())
                .request()
                .post(Entity.json<Vote>(vote))
        assertEquals(Response.Status.CREATED.statusCode, createdVote.status)
        assertEquals(MediaType.APPLICATION_JSON_TYPE, createdVote.mediaType)

        return createdVote
    }

    private fun createUserVoteAssignedToPoll(user: String): Response {
        val poll = PollResourceIT.createEntity()
        val createdPoll = poll.readEntity(Creation::class.java)
        val vote = Vote.asUser(createdPoll.id, answers = ANSWERS, user = user)

        val createdVote = AbstractEndpointTest.prepareGenericRequest(VoteResource.RESOURCE_PATH)
                .path(UUID.randomUUID().toString())
                .request()
                .post(Entity.json<Vote>(vote))
        assertEquals(Response.Status.CREATED.statusCode, createdVote.status)
        assertEquals(MediaType.APPLICATION_JSON_TYPE, createdVote.mediaType)

        return createdVote
    }

    companion object {
        private val ANSWERS = listOf(1, 3)
        private val CORRECT_ANSWERS = listOf(1)
    }
}