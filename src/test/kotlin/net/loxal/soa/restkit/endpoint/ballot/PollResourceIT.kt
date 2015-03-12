/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint.ballot

import net.loxal.soa.restkit.endpoint.AbstractEndpointTest
import org.junit.Test
import net.loxal.soa.restkit.model.common.ErrorMessage
import net.loxal.soa.restkit.model.ballot.Poll
import javax.ws.rs.core.Response
import javax.ws.rs.core.MediaType
import net.loxal.soa.restkit.endpoint.Endpoint
import java.util.Arrays
import javax.ws.rs.client.Entity
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import java.util.UUID

public class PollResourceIT : AbstractEndpointTest() {
    Test
    fun populateQuestionnaire() {
        // TODO move this to some manual triggerable “class object” / “object”
        // TODO with sample data for the iOS app
        // TODO post with ID like simpsons-1 to simpsons-10
        fun createPolls() {
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


            val polls = listOf(poll1, poll2, poll3, poll4, poll5, poll6, poll7, poll8, poll9, poll10)

            polls.forEach { poll ->
                val createdPoll = createPoll("simpsons-${UUID.randomUUID()}", poll)
                assertEquals(Response.Status.CREATED.getStatusCode(), createdPoll.getStatus())
                assertEquals(MediaType.APPLICATION_JSON_TYPE, createdPoll.getMediaType())
                assertTrue(createdPoll.getLocation().getPath().contains("/ballot/poll/"))
                assertFalse(createdPoll.getLocation().getPath().endsWith("/"))

                AbstractEndpointTest.LOG.info("Created poll: ${createdPoll.getLocation()}")
            }
        }
        createPolls()
    }

    Test
    fun createNewPoll() {
        val response = createPoll()

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType())
        assertEquals(false, response.getLocation().getPath().endsWith("null"))
        assertEquals(true, response.getLocation().getPath().startsWith(Endpoint.URI_PATH_SEPARATOR + PollResource.RESOURCE_PATH))
        assertEquals(true, response.getLocation().getSchemeSpecificPart().contains(PollResource.RESOURCE_PATH + Endpoint.URI_PATH_SEPARATOR))
        assertEquals(false, response.getLocation().getSchemeSpecificPart().endsWith(PollResource.RESOURCE_PATH))
    }

    Test
    public fun deleteNonExistentPoll() {
        val existingEntity = createPoll()

        val response = AbstractEndpointTest.prepareTarget("${existingEntity.getLocation()} ${AbstractEndpointTest.NON_EXISTENT}").request().delete()

        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType())
        val notFoundStatus = Response.Status.NOT_FOUND
        assertEquals(notFoundStatus.getStatusCode(), response.getStatus())
        val errorMessage = response.readEntity<ErrorMessage>(javaClass<ErrorMessage>())
        assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), errorMessage.type)
    }

    Test
    public fun deleteExistingPoll() {
        val existingPoll = createPoll()

        val deletion = AbstractEndpointTest.prepareTarget(existingPoll.getLocation()).request().delete()

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deletion.getStatus())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, deletion.getMediaType())
    }

    Test
    public fun retrieveExistingPoll() {
        val existingPoll = createPoll()

        val retrieval = AbstractEndpointTest.prepareTarget(existingPoll.getLocation()).request().get()

        assertEquals(Response.Status.OK.getStatusCode(), retrieval.getStatus())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, retrieval.getMediaType())
        val retrievedPoll = retrieval.readEntity<net.loxal.soa.restkit.model.ballot.Poll>(javaClass<Poll>())
        assertEquals(POLL_QUESTION, retrievedPoll.question)
        assertEquals(POLL_ANSWERS, retrievedPoll.answers)
        assertEquals(2, retrievedPoll.answers.size())
        assertEquals("Yes", retrievedPoll.answers.get(0))
        assertEquals("No", retrievedPoll.answers.get(1))
    }

    Test
    public fun retrieveNonExistentPoll() {
        val existingEntity = createPoll()

        val retrieval = AbstractEndpointTest.prepareTarget("${existingEntity.getLocation()} ${AbstractEndpointTest.NON_EXISTENT}").request().get()


        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), retrieval.getStatus())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, retrieval.getMediaType())
        val notFoundError = retrieval.readEntity<ErrorMessage>(javaClass<ErrorMessage>())
        assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), notFoundError.type)
    }

    Test
    public fun updateExistingPoll() {
        val existingPoll = createPoll()

        val newQuestion = "Does it work?"
        val firstAnswerOption = "Maybe"
        val secondAnswerOption = "Somewhat"
        val thirdAnswerOption = "Absolutely Not"
        val newAnswerOptions = Arrays.asList<String>(firstAnswerOption, secondAnswerOption, thirdAnswerOption)

        val modifiedPoll = Poll(newQuestion, newAnswerOptions)

        val update = AbstractEndpointTest.prepareTarget(existingPoll.getLocation()).request().put(Entity.json<Poll>(modifiedPoll))

        assertEquals(Response.Status.OK.getStatusCode(), update.getStatus())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, update.getMediaType())

        val retrievedUpdatedPoll = AbstractEndpointTest.prepareTarget(existingPoll.getLocation()).request().get()
        val updatedPoll = retrievedUpdatedPoll.readEntity<net.loxal.soa.restkit.model.ballot.Poll>(javaClass<Poll>())
        assertEquals(newQuestion, updatedPoll.question)
        assertEquals(newAnswerOptions.size().toInt(), updatedPoll.answers.size().toInt())
        assertEquals(firstAnswerOption, updatedPoll.answers.get(0))
        assertEquals(secondAnswerOption, updatedPoll.answers.get(1))
        assertEquals(thirdAnswerOption, updatedPoll.answers.get(2))
    }

    Test
    public fun updateNonExistentPoll() {
        val existingEntity = createPoll()

        val somePoll = Poll("Irrelevant", listOf<String>())
        val update = AbstractEndpointTest.prepareTarget("${existingEntity.getLocation()} ${AbstractEndpointTest.NON_EXISTENT}").request().put(Entity.json<Poll>(somePoll))

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), update.getStatus())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, update.getMediaType())
    }

    class object {
        private val POLL_QUESTION = "What is the meaning of life?"
        private val POLL_ANSWERS = Arrays.asList<String>("Yes", "No")

        fun createPoll(id: String = UUID.randomUUID().toString(), poll: Poll = Poll(POLL_QUESTION, POLL_ANSWERS)): Response {
            val createdPoll = AbstractEndpointTest.prepareGenericRequest(PollResource.RESOURCE_PATH)
                    .path(id).request()
                    .post(Entity.json<Poll>(poll))
            assertEquals(Response.Status.CREATED.getStatusCode(), createdPoll.getStatus())
            assertEquals(MediaType.APPLICATION_JSON_TYPE, createdPoll.getMediaType())

            return createdPoll
        }
    }
}