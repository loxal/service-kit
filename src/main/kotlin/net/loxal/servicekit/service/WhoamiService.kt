/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.service

import net.loxal.servicekit.endpoint.Endpoint
import net.loxal.servicekit.model.whoami.Headers
import net.loxal.servicekit.model.whoami.Host
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

@Path(WhoamiService.RESOURCE_PATH)
class WhoamiService : Endpoint() {

    @GET
    fun whoAmI(@Context request: HttpServletRequest, @Context requestContext: ContainerRequestContext, @Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val headers = Headers(
                accept = request.getHeader("accept"),
                `accept-encoding` = request.getHeader("accept-encoding"),
                `accept-language` = request.getHeader("accept-language"),
                `cache-control` = request.getHeader("cache-control"),
                connection = request.getHeader("connection"),
                cookie = request.getHeader("cookie"),
                dnt = request.getHeader("dnt"),
                host = request.getHeader("host"),
                `upgrade-insecure-requests` = request.getHeader("upgrade-insecure-requests"),
                `user-agent` = request.getHeader("user-agent")
        )
        val host = Host(
                request.remoteHost,
                request.remoteAddr,
                headers = headers
        )

        asyncResponse.resume(Response.ok(host).build())

        LOG.info(requestContext.method)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(WhoamiService::class.java)
        const val RESOURCE_PATH = "who-am-i"
    }
}