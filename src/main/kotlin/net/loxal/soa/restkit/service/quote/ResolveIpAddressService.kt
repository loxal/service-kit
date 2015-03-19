/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.service.quote

import net.loxal.soa.restkit.endpoint.Endpoint
import net.loxal.soa.restkit.model.common.ErrorMessage
import net.loxal.soa.restkit.model.whoami.Host
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import kotlin.platform.platformStatic

Path(ResolveIpAddressService.RESOURCE_PATH)
class ResolveIpAddressService : Endpoint() {

    GET
    fun resolveIpAddress(QueryParam(HOST_NAME_PARAM) hostName: String, Context requestContext: ContainerRequestContext, Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        try {
            val localHost = InetAddress.getByName(hostName)

            val host = Host(localHost.getHostName(), localHost.getHostAddress())
            asyncResponse.resume(Response.ok(host).build())

            LOG.info(requestContext.getMethod())
        } catch (e: UnknownHostException) {
            val errorMsg = e.getMessage()
            val errorMessage = ErrorMessage.create(errorMsg)

            asyncResponse.resume(Response.serverError().entity(errorMessage).build())
            LOG.severe(errorMsg)
        }
    }

    companion object {
        platformStatic val HOST_NAME_PARAM: String = "hostName"
        protected val LOG: Logger = Logger.getGlobal()
        val RESOURCE_PATH = "resolve-ip-address"
    }
}