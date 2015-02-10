/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint

import org.junit.Before
import org.junit.Test
import net.loxal.soa.restkit.model.whoami.Host
import javax.ws.rs.core.Response
import javax.ws.rs.core.MediaType
import kotlin.test.assertEquals

public class ResolveIpAddressServiceIT : AbstractEndpointTest() {
    Before
    public fun setUp() {
        AbstractEndpointTest.resourcePath = ResolveIpAddressService.RESOURCE_PATH
    }

    Test
    public fun assureHostAddress() {
        val hostName = "loxal.net"
        val loxalNetIpAddress = "216.239.34.21"

        val response = AbstractEndpointTest.prepareGenericRequest(ResolveIpAddressService.RESOURCE_PATH).queryParam(ResolveIpAddressService.HOST_NAME_PARAM, hostName).request().get()

        assertEquals(Response.Status.OK.getStatusCode().toLong(), response.getStatus().toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType())

        val host = response.readEntity<Host>(javaClass<Host>())
        assertEquals(loxalNetIpAddress, host.address)
        assertEquals(hostName, host.name)
    }
}