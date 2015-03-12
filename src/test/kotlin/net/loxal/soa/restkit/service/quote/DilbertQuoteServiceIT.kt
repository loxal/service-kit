/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.service.quote

import org.junit.Before
import org.junit.Test
import javax.ws.rs.core.Response
import kotlin.test.assertEquals
import net.loxal.soa.restkit.endpoint.AbstractEndpointTest
import net.loxal.soa.restkit.model.dilbert.Quote
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DilbertQuoteServiceIT : AbstractEndpointTest() {

    private val RESOURCE_PATH_DEV = "${DilbertQuoteService.RESOURCE_PATH}/${DilbertQuoteService.RESOURCE_PATH_DEV}"
    private val RESOURCE_PATH_MANAGER = "${DilbertQuoteService.RESOURCE_PATH}/${DilbertQuoteService.RESOURCE_PATH_MANAGER}"

    Before
    public fun setUp() {
        AbstractEndpointTest.resourcePath = RESOURCE_PATH_DEV
    }

    Test
    public fun getDevQuote() {
        val response = AbstractEndpointTest.prepareGenericRequest(RESOURCE_PATH_DEV).request().get()
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus())
        assertEquals(DilbertQuoteService.mediaType, response.getMediaType().toString())

        val quote = response.readEntity(javaClass<Quote>())
        assertNotNull(quote.id)
        assertTrue(quote.id is Int)
        assertNotNull(quote.quote)
        assertTrue(quote.quote is String)
    }

    Test
    public fun getDevQuoteViaId() {
        val quoteId = 9
        val response = AbstractEndpointTest.prepareGenericRequest(RESOURCE_PATH_DEV)
                .path(quoteId.toString()).request().get()
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus())
        assertEquals(DilbertQuoteService.mediaType, response.getMediaType().toString())

        val quote = response.readEntity(javaClass<Quote>())
        val quoteIndex = quoteId - 1
        assertEquals(DilbertQuoteService.quotesDev[quoteIndex], quote)
    }

    Test
    public fun getManagerQuote() {
        val response = AbstractEndpointTest.prepareGenericRequest(RESOURCE_PATH_MANAGER).request().get()
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus())
        assertEquals(DilbertQuoteService.mediaType, response.getMediaType().toString())

        val quote = response.readEntity(javaClass<Quote>())
        assertNotNull(quote.id)
        assertTrue(quote.id is Int)
        assertNotNull(quote.quote)
        assertTrue(quote.quote is String)
    }

    Test
    public fun getManagerQuoteViaId() {
        val quoteId = 9
        val response = AbstractEndpointTest.prepareGenericRequest(RESOURCE_PATH_MANAGER)
                .path(quoteId.toString()).request().get()
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus())
        assertEquals(DilbertQuoteService.mediaType, response.getMediaType().toString())

        val quote = response.readEntity(javaClass<Quote>())
        val quoteIndex = quoteId - 1
        assertEquals(DilbertQuoteService.quotesManager[quoteIndex], quote)
    }
}