/*
 * Copyright 2014 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint;

import net.loxal.soa.restkit.model.whoami.Host;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Path(WhoamiService.RESOURCE_PATH)
public class WhoamiService extends Endpoint {
    static final String RESOURCE_PATH = "who-am-i";
    private static final Logger LOG = Logger.getGlobal();

    @GET
    public void whoAmI(@Context HttpServletRequest request, @Context ContainerRequestContext requestContext, @Suspended AsyncResponse asyncResponse) {
        asyncResponse.setTimeout(ASYNC_RESPONSE_TIMEOUT, TimeUnit.SECONDS);

        Host host = new Host(request.getRemoteHost(), request.getRemoteAddr());
        asyncResponse.resume(Response.ok(host).build());

        LOG.info(requestContext.getMethod());
    }
}
