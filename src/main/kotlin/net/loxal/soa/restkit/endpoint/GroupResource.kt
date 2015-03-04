/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint

import javax.ws.rs.Path
import javax.inject.Inject
import net.loxal.soa.restkit.model.generic.Group
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

Path(GroupResource.RESOURCE_PATH)
class GroupResource : Endpoint() {

    Inject
    private var client: RepositoryClient<Group> = RepositoryClient()

    POST
    fun create(NotNull Valid group: Group, Context requestContext: ContainerRequestContext, Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val response = client.post(Entity.json<Group>(group))
        val id = extractIdOfLocation(response)

        val entityLocation = URI.create(requestContext.getUriInfo().getRequestUri().toString() + Endpoint.URI_PATH_SEPARATOR + id)

        asyncResponse.resume(Response.fromResponse(response).location(entityLocation).build())
        Endpoint.LOG.info(requestContext.getMethod())
    }

    Path(Endpoint.ID_PATH_PARAM_PLACEHOLDER)
    DELETE
    fun delete(NotNull PathParam(Endpoint.ID_PATH_PARAM) id: String, Context requestContext: ContainerRequestContext, Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val response = client.delete(javaClass<Group>(), id)

        asyncResponse.resume(Response.fromResponse(response).type(MediaType.APPLICATION_JSON_TYPE).build())
        Endpoint.LOG.info(requestContext.getMethod())
    }

    Path(Endpoint.ID_PATH_PARAM_PLACEHOLDER)
    GET
    fun retrieve(NotNull PathParam(Endpoint.ID_PATH_PARAM) id: String, Context requestContext: ContainerRequestContext, Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val response = client.get(javaClass<Group>(), id)

        asyncResponse.resume(Response.fromResponse(response).build())
        Endpoint.LOG.info(requestContext.getMethod())
    }

    Path(Endpoint.ID_PATH_PARAM_PLACEHOLDER)
    PUT
    fun update(NotNull Valid group: Group, NotNull PathParam(Endpoint.ID_PATH_PARAM) id: String, Context requestContext: ContainerRequestContext, Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val response = client.put(Entity.json<Group>(group), id)

        asyncResponse.resume(Response.fromResponse(response).build())
        Endpoint.LOG.info(requestContext.getMethod())
    }

    class object {
        public val RESOURCE_PATH: String = "group"
    }
}