/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint

import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.core.Context
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.Suspended
import javax.ws.rs.container.AsyncResponse
import java.util.concurrent.TimeUnit
import net.loxal.soa.restkit.model.whoami.Host
import javax.ws.rs.core.Response
import java.util.logging.Logger

Path(WhoamiService.RESOURCE_PATH)
public class WhoamiService : Endpoint() {

    GET
    public fun whoAmI(Context request: HttpServletRequest, Context requestContext: ContainerRequestContext, Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val host = Host(request.getRemoteHost(), request.getRemoteAddr())
        asyncResponse.resume(Response.ok(host).build())

        LOG.info(requestContext.getMethod())
    }

    class object {
        val RESOURCE_PATH = "who-am-i"
        private val LOG = Logger.getGlobal()
    }
}