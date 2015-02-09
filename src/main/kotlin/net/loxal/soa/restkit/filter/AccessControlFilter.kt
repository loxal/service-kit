/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.filter

import javax.annotation.Priority
import javax.ws.rs.HttpMethod
import javax.ws.rs.Priorities
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.ext.Provider

Provider
Priority(Priorities.HEADER_DECORATOR)
class AccessControlFilter : ContainerResponseFilter {
    override fun filter(requestContext: ContainerRequestContext, responseContext: ContainerResponseContext) {
        val headers = responseContext.getHeaders()

        headers.add("Access-Control-Allow-Origin", "*")

        val allowHeaders = listOf("Origin", "X-Requested-With",
                HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_LANGUAGE, HttpHeaders.ACCEPT_LANGUAGE)
        headers.add("Access-Control-Allow-Headers", allowHeaders)

        val exposeHeaders = listOf(HttpHeaders.CONTENT_LOCATION, HttpHeaders.CONTENT_DISPOSITION, HttpHeaders.CONTENT_LENGTH)
        headers.add("Access-Control-Expose-Headers", exposeHeaders)

        val allowMethods = listOf(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.HEAD, HttpMethod.OPTIONS)
        headers.add("Access-Control-Allow-Methods", allowMethods)
    }
}
