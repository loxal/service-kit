/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.service.quote

import net.loxal.soa.restkit.endpoint.AbstractEndpointTest
import org.junit.Before
import org.junit.Test
import net.loxal.soa.restkit.model.whoami.Host
import javax.ws.rs.core.Response
import javax.ws.rs.core.MediaType

public class ResolveIpAddressServiceIT : AbstractEndpointTest() {
    Before
    public fun setUp() {
        AbstractEndpointTest.resourcePath = ResolveIpAddressService.RESOURCE_PATH
    }

    Test
    public fun assureHostAddress() {
        val hostName = "loxal.net"

        val response = AbstractEndpointTest.prepareGenericRequest(ResolveIpAddressService.RESOURCE_PATH).queryParam(ResolveIpAddressService.HOST_NAME_PARAM, hostName).request().get()

        test.assertEquals(Response.Status.OK.getStatusCode().toLong(), response.getStatus().toLong())
        test.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType())

        val host = response.readEntity<Host>(javaClass<Host>())
        test.assertNotNull(host.address)
        test.assertTrue(host.address.length() > 6 && host.address.length() < 20)
        test.assertEquals(hostName, host.name)
    }
}