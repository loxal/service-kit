/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.service.quote

import org.junit.Before
import org.junit.Test
import javax.ws.rs.core.Response
import javax.ws.rs.core.MediaType
import kotlin.test.assertEquals
import net.loxal.soa.restkit.endpoint.AbstractEndpointTest
import net.loxal.soa.restkit.model.dilbert.Quote
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

public class DilbertQuoteServiceIT : AbstractEndpointTest() {

    Before
    public fun setUp() {
        AbstractEndpointTest.resourcePath = DilbertQuoteService.RESOURCE_PATH
    }

    Test
    public fun getQuote() {
        val response = AbstractEndpointTest.prepareGenericRequest(DilbertQuoteService.RESOURCE_PATH).request().get()
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType())

        val quote = response.readEntity(javaClass<Quote>())
        assertNotNull(quote.id)
        assertTrue(quote.id is Int)
        assertNotNull(quote.quote)
        assertTrue(quote.quote is String)
    }

    Test
    public fun getQuoteViaId() {
        val quoteId = 9
        val response = AbstractEndpointTest.prepareGenericRequest(DilbertQuoteService.RESOURCE_PATH)
                .path(quoteId.toString()).request().get()
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType())

        val quote = response.readEntity(javaClass<Quote>())
        val quoteIndex = quoteId - 1
        assertEquals(DilbertQuoteService.quotes[quoteIndex], quote)
    }
}