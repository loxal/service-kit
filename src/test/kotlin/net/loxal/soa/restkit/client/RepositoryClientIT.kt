/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.service.quote

import net.loxal.soa.restkit.client.RepositoryClient
import net.loxal.soa.restkit.endpoint.AbstractEndpointTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class RepositoryClientIT : AbstractEndpointTest() {
    Before
    public fun setUp() {
        AbstractEndpointTest.resourcePath = ResolveIpAddressService.RESOURCE_PATH
    }

    Test
    public fun retrieveToken() {
        val token = RepositoryClient.authorize()

        assertEquals(32, token.access_token.length())
        assertEquals(3600, token.expires_in)
        assertEquals("Bearer", token.token_type)
        assertEquals("", token.scope)
    }
}