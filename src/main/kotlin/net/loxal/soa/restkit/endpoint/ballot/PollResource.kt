/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint.ballot

import javax.ws.rs.Path
import net.loxal.soa.restkit.endpoint.Endpoint
import javax.inject.Inject
import net.loxal.soa.restkit.model.ballot.Poll
import net.loxal.soa.restkit.client.RepositoryClient
import javax.ws.rs.POST
import javax.validation.constraints.NotNull
import javax.validation.Valid
import javax.ws.rs.core.Context
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.Suspended
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.DELETE
import javax.ws.rs.PathParam
import javax.ws.rs.GET
import javax.ws.rs.PUT
import java.util.concurrent.TimeUnit
import javax.ws.rs.client.Entity
import java.net.URI
import javax.ws.rs.core.Response
import javax.ws.rs.core.MediaType
import java.util.UUID

Path(PollResource.RESOURCE_PATH)
class PollResource : Endpoint() {

    Inject
    private var client: RepositoryClient<Poll> = RepositoryClient()

    POST
    Path("{id}")
    fun create(NotNull Valid poll: Poll, Context requestContext: ContainerRequestContext, Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val createdPoll = client.post(Entity.json<Poll>(poll), id = UUID.randomUUID().toString())
        val id = extractIdOfLocation(createdPoll)

        val entityLocation = URI.create(requestContext.getUriInfo().getRequestUri().toString() + Endpoint.URI_PATH_SEPARATOR + id)

        asyncResponse.resume(Response.fromResponse(createdPoll).location(entityLocation).build())
        Endpoint.LOG.info(requestContext.getMethod())
    }

    Path(Endpoint.ID_PATH_PARAM_PLACEHOLDER)
    DELETE
    fun delete(NotNull PathParam(Endpoint.ID_PATH_PARAM) id: String, Context requestContext: ContainerRequestContext, Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val response = client.delete(javaClass<Poll>(), id)

        asyncResponse.resume(Response.fromResponse(response).type(MediaType.APPLICATION_JSON_TYPE).build())
        Endpoint.LOG.info(requestContext.getMethod())
    }

    Path(Endpoint.ID_PATH_PARAM_PLACEHOLDER)
    GET
    fun retrieve(NotNull PathParam(Endpoint.ID_PATH_PARAM) id: String, Context requestContext: ContainerRequestContext, Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val response = client.get(javaClass<Poll>(), id)

        asyncResponse.resume(Response.fromResponse(response).build())
        Endpoint.LOG.info(requestContext.getMethod())
    }

    Path(Endpoint.ID_PATH_PARAM_PLACEHOLDER)
    PUT
    fun update(NotNull Valid poll: Poll, NotNull PathParam(Endpoint.ID_PATH_PARAM) id: String, Context requestContext: ContainerRequestContext, Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val updated = client.put(Entity.json<Poll>(poll), id)

        asyncResponse.resume(Response.fromResponse(updated).build())
        Endpoint.LOG.info(requestContext.getMethod())
    }

    class object {
        private val RESOURCE_NAME = "poll"
        public val RESOURCE_PATH: String = "ballot/" + RESOURCE_NAME
    }
}