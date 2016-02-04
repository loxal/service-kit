/*
 * Copyright 2016 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint.ballot

import net.loxal.soa.restkit.client.KitClient
import net.loxal.soa.restkit.endpoint.Endpoint
import net.loxal.soa.restkit.model.ballot.Poll
import net.loxal.soa.restkit.model.ballot.Vote
import java.net.URI
import java.util.*
import java.util.concurrent.TimeUnit
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.client.Entity
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

@Path(VoteResource.RESOURCE_PATH)
class VoteResource : Endpoint() {

    private var client: KitClient<Vote> = KitClient()
    private var pollClient: KitClient<Poll> = KitClient()

    @Path(Endpoint.ID_PATH_PARAM_PLACEHOLDER)
    @POST
    fun create(
            @Valid vote: Vote,
            @PathParam(Endpoint.ID_PATH_PARAM) id: String,
            @Context req: ContainerRequestContext,
            @Suspended asyncResponse: AsyncResponse
    ) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        reviewVote(vote)

        val createdVote = client.post(Entity.json<Vote>(vote), id = id)
        val entityLocation =
                if (req.uriInfo.requestUri.toString().endsWith(id))
                    req.uriInfo.requestUri
                else
                    URI("${req.uriInfo.requestUri}/$id")

        asyncResponse.resume(Response.fromResponse(createdVote).location(entityLocation).build())
        Endpoint.LOG.info(req.method)
    }

    private fun reviewVote(vote: Vote): Unit {
        val response = pollClient.get(Poll::class.java, vote.referencePoll)
        val poll = response.readEntity(Poll::class.java)
        val hasCorrectAnswers = poll.correctAnswers?.equals(vote.answers)

        vote.correct = hasCorrectAnswers
    }

    @POST
    fun create(
            @Valid vote: Vote,
            @Context req: ContainerRequestContext,
            @Suspended asyncResponse: AsyncResponse
    ) {
        create(vote, UUID.randomUUID().toString(), req, asyncResponse)
    }

    @Path(Endpoint.ID_PATH_PARAM_PLACEHOLDER)
    @DELETE
    fun delete(
            @PathParam(Endpoint.ID_PATH_PARAM) id: String,
            @Context req: ContainerRequestContext,
            @Suspended asyncResponse: AsyncResponse
    ) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val response = client.delete(Vote::class.java, id)

        asyncResponse.resume(Response
                .fromResponse(response)
                .build())
        Endpoint.LOG.info(req.method)
    }

    @Path(Endpoint.ID_PATH_PARAM_PLACEHOLDER)
    @GET
    fun retrieve(
            @NotNull @PathParam(Endpoint.ID_PATH_PARAM) id: String,
            @Context req: ContainerRequestContext,
            @Suspended asyncResponse: AsyncResponse
    ) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val response = client.get(Vote::class.java, id)

        asyncResponse.resume(Response
                .fromResponse(response)
                .build())
        Endpoint.LOG.info(req.method)
    }

    @Path(Endpoint.ID_PATH_PARAM_PLACEHOLDER)
    @PUT
    fun update(@NotNull @Valid vote: Vote, @NotNull @PathParam(Endpoint.ID_PATH_PARAM) id: String, @Context requestContext: ContainerRequestContext, @Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val updated = client.put(Entity.json<Vote>(vote), id)

        asyncResponse.resume(Response.fromResponse(updated).build())
        Endpoint.LOG.info(requestContext.method)
    }

    companion object {
        private const val RESOURCE_NAME = "vote"
        const val RESOURCE_PATH = "ballot/" + RESOURCE_NAME
    }
}