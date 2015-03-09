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
import net.loxal.soa.restkit.model.ballot.Poll

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

    Test
    fun populateQuestionnaire() {
        // TODO move this to some manual triggerable “class object” / “object”
        // TODO with sample data for the iOS app
        // TODO post with ID like simpsons-1 to simpsons-10
        fun createPolls(): Response {
            val poll1 = Poll("In which town do the Simpsons reside?",
                    listOf(
                            "Springfield-",
                            "Shelbyville",
                            "Seinfeld"
                    )
            )

            val poll2 = Poll("What is the name of the Simpsons' next door neighbor?",
                    listOf(
                            "Barney Gumble",
                            "Ned Flanders-",
                            "Principal Skinner"
                    )
            )

            val poll3 = Poll("Who founded the Simpsons’ town?",
                    listOf(
                            "Jebadiah Springfield-",
                            "Zachariah Springfield",
                            "Springfield Manhattan"
                    )
            )
            val poll4 = Poll("How old is Bart?",
                    listOf(
                            "10-",
                            "11",
                            "12"
                    )
            )
            val poll5 = Poll("What is the name of the clown on Channel 6?",
                    listOf(
                            "Gabbo",
                            "Krusty-",
                            "Bonko"
                    )
            )
            val poll6 = Poll("What is the name of Lisa’s jazz mentor?",
                    listOf(
                            "Billy Jazzman",
                            "Blind Willy Witherspoon",
                            "Bleeding Gums Murphy-"
                    )
            )

            val poll7 = Poll("Who is Mr Burns’ assistant?",
                    listOf(
                            "Seymour Skinner",
                            "Barnard Gumble",
                            "Waylon Smithers-"
                    )
            )
            val poll8 = Poll("What is the name of the bar where Homer drinks?",
                    listOf(
                            "Moe’s Tavern-",
                            "Joe’s Cavern",
                            "The Drink Hole"
                    )
            )
            val poll9 = Poll(" Which one of these is not a catchphrase Bart uses?",
                    listOf(
                            "Aye Carumba!",
                            "Don't have a cow, man!",
                            "Woohoo!-"
                    )
            )
            val poll10 = Poll("What did the Simpsons get for their first Christmas?",
                    listOf(
                            "A dog-",
                            "A cat",
                            "A hamster"
                    )
            )


            val createdPoll = AbstractEndpointTest.prepareGenericRequest(PollResource.RESOURCE_PATH).request().post(Entity.json<Poll>(poll1))
            assertEquals(Response.Status.CREATED.getStatusCode(), createdPoll.getStatus())
            assertEquals(MediaType.APPLICATION_JSON_TYPE, createdPoll.getMediaType())

            println(createdPoll.getLocation())
            return createdPoll
        }
        createPolls()
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