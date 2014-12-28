/*
 * Copyright 2014 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint;

import net.loxal.soa.restkit.model.common.ErrorMessage;
import net.loxal.soa.restkit.model.whoami.Host;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Path(ResolveIpAddressService.RESOURCE_PATH)
public class ResolveIpAddressService extends Endpoint {
    protected static final String HOST_NAME_PARAM = "hostName";
    protected static final Logger LOG = Logger.getGlobal();
    static final String RESOURCE_PATH = "resolve-ip-address";

    @GET
    public void resolveIpAddress(@QueryParam(HOST_NAME_PARAM) String hostName, @Context ContainerRequestContext requestContext, @Suspended AsyncResponse asyncResponse) {
        asyncResponse.setTimeout(ASYNC_RESPONSE_TIMEOUT, TimeUnit.SECONDS);

        try {
            InetAddress localHost = InetAddress.getByName(hostName);

            Host host = new Host(localHost.getHostName(), localHost.getHostAddress());
            asyncResponse.resume(Response.ok(host).build());

            LOG.info(requestContext.getMethod());
        } catch (UnknownHostException e) {
            String errorMsg = e.getMessage();
            ErrorMessage errorMessage = new ErrorMessage(errorMsg);

            asyncResponse.resume(Response.serverError().entity(errorMessage).build());
            LOG.severe(errorMsg);
        }
    }
}
