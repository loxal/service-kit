/*
 * Copyright 2014 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint.ballot;

import net.loxal.soa.restkit.client.RESTClient;
import net.loxal.soa.restkit.endpoint.Endpoint;
import net.loxal.soa.restkit.model.ballot.Poll;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.concurrent.TimeUnit;

@Path(PollResource.RESOURCE_PATH)
public class PollResource extends Endpoint {
    private static final String RESOURCE_NAME = "poll";
    public static final String RESOURCE_PATH = "ballot/" + RESOURCE_NAME;

    @Inject
    private RESTClient<Poll> client;

    @POST
    public void create(@NotNull @Valid Poll poll, @Context ContainerRequestContext requestContext, @Suspended AsyncResponse asyncResponse) {
        asyncResponse.setTimeout(ASYNC_RESPONSE_TIMEOUT, TimeUnit.SECONDS);

        Response createdPoll = client.post(Entity.json(poll));
        final String id = extractIdOfLocation(createdPoll);

        URI entityLocation = URI.create(requestContext.getUriInfo().getRequestUri().toString() + URI_PATH_SEPARATOR + id);

        asyncResponse.resume(Response.fromResponse(createdPoll).location(entityLocation).build());
        LOG.info(requestContext.getMethod());
    }

    @Path(ID_PATH_PARAM_PLACEHOLDER)
    @DELETE
    public void delete(@NotNull @PathParam(ID_PATH_PARAM) String id, @Context ContainerRequestContext requestContext, @Suspended AsyncResponse asyncResponse) {
        asyncResponse.setTimeout(ASYNC_RESPONSE_TIMEOUT, TimeUnit.SECONDS);

        Response response = client.delete(Poll.class, id);

        asyncResponse.resume(Response.fromResponse(response).type(MediaType.APPLICATION_JSON_TYPE).build());
        LOG.info(requestContext.getMethod());
    }

    @Path(ID_PATH_PARAM_PLACEHOLDER)
    @GET
    public void retrieve(@NotNull @PathParam(ID_PATH_PARAM) String id, @Context ContainerRequestContext requestContext, @Suspended AsyncResponse asyncResponse) {
        asyncResponse.setTimeout(ASYNC_RESPONSE_TIMEOUT, TimeUnit.SECONDS);

        Response response = client.get(Poll.class, id);

        asyncResponse.resume(Response.fromResponse(response).build());
        LOG.info(requestContext.getMethod());
    }

    @Path(ID_PATH_PARAM_PLACEHOLDER)
    @PUT
    public void update(@NotNull @Valid Poll poll, @NotNull @PathParam(ID_PATH_PARAM) String id, @Context ContainerRequestContext requestContext, @Suspended AsyncResponse asyncResponse) {
        asyncResponse.setTimeout(ASYNC_RESPONSE_TIMEOUT, TimeUnit.SECONDS);

        Response updated = client.put(Entity.json(poll), id);

        asyncResponse.resume(Response.fromResponse(updated).build());
        LOG.info(requestContext.getMethod());
    }
}
