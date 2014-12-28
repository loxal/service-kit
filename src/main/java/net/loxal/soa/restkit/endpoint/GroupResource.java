/*
 * Copyright 2014 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint;

import net.loxal.soa.restkit.client.RepositoryClient;
import net.loxal.soa.restkit.model.generic.Group;

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

@Path(GroupResource.RESOURCE_PATH)
public class GroupResource extends Endpoint {
    public static final String RESOURCE_PATH = "group";

    @Inject
    RepositoryClient<Group> client;

    @POST
    public void create(@NotNull @Valid Group group, @Context ContainerRequestContext requestContext, @Suspended AsyncResponse asyncResponse) {
        asyncResponse.setTimeout(ASYNC_RESPONSE_TIMEOUT, TimeUnit.SECONDS);

        Response response = client.post(Entity.json(group));
        final String id = extractIdOfLocation(response);

        URI entityLocation = URI.create(requestContext.getUriInfo().getRequestUri().toString() + URI_PATH_SEPARATOR + id);

        asyncResponse.resume(Response.fromResponse(response).location(entityLocation).build());
        LOG.info(requestContext.getMethod());
    }

    @Path(ID_PATH_PARAM_PLACEHOLDER)
    @DELETE
    public void delete(@NotNull @PathParam(ID_PATH_PARAM) String id, @Context ContainerRequestContext requestContext, @Suspended AsyncResponse asyncResponse) {
        asyncResponse.setTimeout(ASYNC_RESPONSE_TIMEOUT, TimeUnit.SECONDS);

        Response response = client.delete(Group.class, id);

        asyncResponse.resume(Response.fromResponse(response).type(MediaType.APPLICATION_JSON_TYPE).build());
        LOG.info(requestContext.getMethod());
    }

    @Path(ID_PATH_PARAM_PLACEHOLDER)
    @GET
    public void retrieve(@NotNull @PathParam(ID_PATH_PARAM) String id, @Context ContainerRequestContext requestContext, @Suspended AsyncResponse asyncResponse) {
        asyncResponse.setTimeout(ASYNC_RESPONSE_TIMEOUT, TimeUnit.SECONDS);

        Response response = client.get(Group.class, id);

        asyncResponse.resume(Response.fromResponse(response).build());
        LOG.info(requestContext.getMethod());
    }

    @Path(ID_PATH_PARAM_PLACEHOLDER)
    @PUT
    public void update(@NotNull @Valid Group group, @NotNull @PathParam(ID_PATH_PARAM) String id, @Context ContainerRequestContext requestContext, @Suspended AsyncResponse asyncResponse) {
        asyncResponse.setTimeout(ASYNC_RESPONSE_TIMEOUT, TimeUnit.SECONDS);

        Response response = client.put(Entity.json(group), id);

        asyncResponse.resume(Response.fromResponse(response).build());
        LOG.info(requestContext.getMethod());
    }
}
