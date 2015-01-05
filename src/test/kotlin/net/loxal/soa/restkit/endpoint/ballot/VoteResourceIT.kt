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
    throws(javaClass<Exception>())
    public fun createVote() {
        val response = createVoteAssignedToPoll()

        assertEquals(Response.Status.CREATED.getStatusCode().toLong(), response.getStatus().toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType())
        assertEquals(false, response.getLocation().getPath().endsWith("null"))
        assertEquals(true, response.getLocation().getPath().startsWith(Endpoint.URI_PATH_SEPARATOR + VoteResource.RESOURCE_PATH))
        assertEquals(true, response.getLocation().getSchemeSpecificPart().contains(VoteResource.RESOURCE_PATH + Endpoint.URI_PATH_SEPARATOR))
        assertEquals(false, response.getLocation().getSchemeSpecificPart().endsWith(VoteResource.RESOURCE_PATH))
    }

    Test
    throws(javaClass<Exception>())
    public fun deleteNonExistentVote() {
        val existingEntity = createVoteAssignedToPoll()

        val response = AbstractEndpointTest.prepareTarget("${existingEntity.getLocation()} ${AbstractEndpointTest.NON_EXISTENT}").request().delete()

        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType())
        val notFoundStatus = Response.Status.NOT_FOUND
        assertEquals(notFoundStatus.getStatusCode().toLong(), response.getStatus().toLong())
        val errorMessage = response.readEntity<ErrorMessage>(javaClass<ErrorMessage>())
        assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), errorMessage.type)
    }

    Test
    throws(javaClass<Exception>())
    public fun deleteExistingVote() {
        val existingVote = createVoteAssignedToPoll()

        val deletion = AbstractEndpointTest.prepareTarget(existingVote.getLocation()).request().delete()

        assertEquals(Response.Status.NO_CONTENT.getStatusCode().toLong(), deletion.getStatus().toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, deletion.getMediaType())
    }

    Test
    throws(javaClass<Exception>())
    public fun retrieveExistingVote() {
        val existingVote = createVoteAssignedToPoll()

        val retrieval = AbstractEndpointTest.prepareTarget(existingVote.getLocation()).request().get()

        assertEquals(Response.Status.OK.getStatusCode().toLong(), retrieval.getStatus().toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, retrieval.getMediaType())
        val retrievedVote = retrieval.readEntity<net.loxal.soa.restkit.model.ballot.Vote>(javaClass<Vote>())
        assertEquals(ANSWER_OPTION_INDEX.toLong(), retrievedVote.answerOptionIndex.toLong())
        assertEquals(false, retrievedVote.referencePoll.isEmpty())
    }

    Test
    throws(javaClass<Exception>())
    public fun retrieveNonExistentVote() {
        val existingEntity = createVoteAssignedToPoll()

        val retrieval = AbstractEndpointTest.prepareTarget("${existingEntity.getLocation()} ${AbstractEndpointTest.NON_EXISTENT}").request().get()

        assertEquals(Response.Status.NOT_FOUND.getStatusCode().toLong(), retrieval.getStatus().toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, retrieval.getMediaType())
        val notFoundError = retrieval.readEntity<ErrorMessage>(javaClass<ErrorMessage>())
        assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), notFoundError.type)
    }

    Test
    throws(javaClass<Exception>())
    public fun updateExistingVote() {
        val existingVote = createVoteAssignedToPoll()

        val updatedReference = "updated reference"
        val updatedAnswerOptionIndex = 2
        val modifiedVote = Vote(updatedReference, updatedAnswerOptionIndex)

        val update = AbstractEndpointTest.prepareTarget(existingVote.getLocation()).request().put(Entity.json<Vote>(modifiedVote))

        assertEquals(Response.Status.OK.getStatusCode().toLong(), update.getStatus().toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, update.getMediaType())

        val retrievedUpdatedPoll = AbstractEndpointTest.prepareTarget(existingVote.getLocation()).request().get()
        val updatedVote = retrievedUpdatedPoll.readEntity<net.loxal.soa.restkit.model.ballot.Vote>(javaClass<Vote>())
        assertEquals(updatedReference, updatedVote.referencePoll)
        assertEquals(updatedAnswerOptionIndex.toLong(), updatedVote.answerOptionIndex.toLong())
    }

    Test
    throws(javaClass<Exception>())
    public fun updateNonExistentVote() {
        val existingEntity = createVoteAssignedToPoll()

        val someVote = Vote("Irrelevant", Integer.MAX_VALUE)
        val update = AbstractEndpointTest.prepareTarget("${existingEntity.getLocation()} ${AbstractEndpointTest.NON_EXISTENT}").request().put(Entity.json<Vote>(someVote))

        assertEquals(Response.Status.NOT_FOUND.getStatusCode().toLong(), update.getStatus().toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, update.getMediaType())
    }

    private fun createVoteAssignedToPoll(): Response {
        val poll = PollResourceIT.createPoll()
        val vote = Vote(poll.getLocation().toString(), ANSWER_OPTION_INDEX)

        val createdVote = AbstractEndpointTest.prepareGenericRequest(VoteResource.RESOURCE_PATH).request().post(Entity.json<Vote>(vote))
        assertEquals(Response.Status.CREATED.getStatusCode().toLong(), createdVote.getStatus().toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, createdVote.getMediaType())

        return createdVote
    }

    class object {
        private val ANSWER_OPTION_INDEX = 1
    }
}