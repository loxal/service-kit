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
import kotlin.test.assertEquals

class WhoamiServiceIT : AbstractEndpointTest() {
    Before
    public fun setUp() {
        AbstractEndpointTest.resourcePath = WhoamiService.RESOURCE_PATH
    }

    Test
    public fun validateHost() {
        val response = AbstractEndpointTest.prepareGenericRequest(WhoamiService.RESOURCE_PATH).request().get()

        assertEquals(Response.Status.OK.getStatusCode().toLong(), response.getStatus().toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType())

        val localhost = "127.0.0.1"
        val host = response.readEntity<Host>(javaClass<Host>())
        assertEquals(localhost, host.address)
        assertEquals(localhost, host.name)
    }
}