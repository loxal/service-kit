/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.service.quote

import net.loxal.soa.restkit.endpoint.Endpoint
import net.loxal.soa.restkit.model.whoami.Host
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

Path(WhoamiService.RESOURCE_PATH)
class WhoamiService : Endpoint() {

    GET
    fun whoAmI(Context request: HttpServletRequest, Context requestContext: ContainerRequestContext, Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val host = Host(request.getRemoteHost(), request.getRemoteAddr())
        asyncResponse.resume(Response.ok(host).build())

        LOG.info(requestContext.getMethod())
    }

    companion object {
        private val LOG = Logger.getGlobal()
        val RESOURCE_PATH = "who-am-i"
    }
}