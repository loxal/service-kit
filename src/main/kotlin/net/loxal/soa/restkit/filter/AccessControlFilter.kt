/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.filter

import javax.annotation.Priority
import javax.ws.rs.Priorities
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.ext.Provider
import kotlin.collections.listOf

@Provider
@Priority(Priorities.HEADER_DECORATOR)
class AccessControlFilter : ContainerResponseFilter {
    override fun filter(requestContext: ContainerRequestContext, responseContext: ContainerResponseContext) {
        val headers = responseContext.headers

        headers.putSingle(allowOriginHeader, allowOriginHeaderValue)
        headers.putSingle(allowHeadersHeader, allowHeadersValue)

        headers.putSingle("Server", "RESTkit v1")
    }

    companion object {
        const val allowOriginHeader = "Access-Control-Allow-Origin"
        const val allowOriginHeaderValue = "*"
        const val allowHeadersHeader = "Access-Control-Allow-Headers"
        val allowHeadersValue = listOf("origin", "x-requested-with")
    }
}
