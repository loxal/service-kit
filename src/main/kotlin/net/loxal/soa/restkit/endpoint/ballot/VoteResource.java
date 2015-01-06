/*
 * Copyright 2014 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint.ballot;

import net.loxal.soa.restkit.client.RESTClient;
import net.loxal.soa.restkit.endpoint.Endpoint;
import net.loxal.soa.restkit.model.ballot.Vote;

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

@Path(VoteResource.RESOURCE_PATH)
public class VoteResource extends Endpoint {
    private static final String RESOURCE_NAME = "vote";
    static final String RESOURCE_PATH = "ballot/" + RESOURCE_NAME;

    @Inject
    private RESTClient<Vote> client;

    @POST
    public void create(@NotNull @Valid Vote vote, @Context ContainerRequestContext requestContext, @Suspended AsyncResponse asyncResponse) {
	    asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT, TimeUnit.SECONDS);

        Response createdVote = client.post(Entity.json(vote));
        final String id = extractIdOfLocation(createdVote);

	    URI entityLocation = URI.create(requestContext.getUriInfo().getRequestUri().toString() + Endpoint.URI_PATH_SEPARATOR + id);

        asyncResponse.resume(Response.fromResponse(createdVote).location(entityLocation).build());
	    Endpoint.LOG.info(requestContext.getMethod());
    }

	@Path(Endpoint.ID_PATH_PARAM_PLACEHOLDER)
    @DELETE
	public void delete(@NotNull @PathParam(Endpoint.ID_PATH_PARAM) String id, @Context ContainerRequestContext requestContext, @Suspended AsyncResponse asyncResponse) {
		asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT, TimeUnit.SECONDS);

        Response response = client.delete(Vote.class, id);

        asyncResponse.resume(Response.fromResponse(response).type(MediaType.APPLICATION_JSON_TYPE).build());
		Endpoint.LOG.info(requestContext.getMethod());
    }

	@Path(Endpoint.ID_PATH_PARAM_PLACEHOLDER)
    @GET
	public void retrieve(@NotNull @PathParam(Endpoint.ID_PATH_PARAM) String id, @Context ContainerRequestContext requestContext, @Suspended AsyncResponse asyncResponse) {
		asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT, TimeUnit.SECONDS);

        Response response = client.get(Vote.class, id);

        asyncResponse.resume(Response.fromResponse(response).build());
		Endpoint.LOG.info(requestContext.getMethod());
    }

	@Path(Endpoint.ID_PATH_PARAM_PLACEHOLDER)
    @PUT
	public void update(@NotNull @Valid Vote vote, @NotNull @PathParam(Endpoint.ID_PATH_PARAM) String id, @Context ContainerRequestContext requestContext, @Suspended AsyncResponse asyncResponse) {
		asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT, TimeUnit.SECONDS);

        Response updated = client.put(Entity.json(vote), id);

        asyncResponse.resume(Response.fromResponse(updated).build());
		Endpoint.LOG.info(requestContext.getMethod());
    }
}
