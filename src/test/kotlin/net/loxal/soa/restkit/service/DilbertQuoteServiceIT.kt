/*
 * Copyright 2016 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.service

import net.loxal.soa.restkit.endpoint.AbstractEndpointTest
import net.loxal.soa.restkit.model.dilbert.Quote
import org.junit.Test
import javax.ws.rs.core.Response
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DilbertQuoteServiceIT : AbstractEndpointTest() {

    private val RESOURCE_PATH_PROGRAMMER = "${DilbertQuoteService.RESOURCE_PATH}/${DilbertQuoteService.RESOURCE_PATH_PROGRAMMER}"
    private val RESOURCE_PATH_MANAGER = "${DilbertQuoteService.RESOURCE_PATH}/${DilbertQuoteService.RESOURCE_PATH_MANAGER}"
    private val RESOURCE_PATH_ENTERPRISE = "${DilbertQuoteService.RESOURCE_PATH}/${DilbertQuoteService.RESOURCE_PATH_ENTERPRISE}"

    @Test fun getEnterpriseQuote(): Unit = retrieveSingleQuote(RESOURCE_PATH_ENTERPRISE)

    @Test fun getEnterpriseQuoteViaId(): Unit =
            retrieveSpecificQuote(resource = RESOURCE_PATH_ENTERPRISE, quotes = DilbertQuoteService.quotesEnterprise)

    @Test fun getProgrammerQuote(): Unit = retrieveSingleQuote(RESOURCE_PATH_PROGRAMMER)

    @Test fun getProgrammerQuoteViaId(): Unit =
            retrieveSpecificQuote(resource = RESOURCE_PATH_PROGRAMMER, quotes = DilbertQuoteService.quotesProgrammer)

    @Test fun getManagerQuote(): Unit = retrieveSingleQuote(RESOURCE_PATH_MANAGER)


    @Test fun getManagerQuoteViaId(): Unit =
            retrieveSpecificQuote(resource = RESOURCE_PATH_MANAGER, quotes = DilbertQuoteService.quotesManager)

    private fun retrieveSingleQuote(resource: String) {
        val response = prepareGenericRequest(resource).request().get()
        assertEquals(Response.Status.OK.statusCode, response.status)
        assertEquals(DilbertQuoteService.mediaType, response.mediaType.toString())

        val quote = response.readEntity(Quote::class.java)
        assertNotNull(quote.id)
        assertTrue(quote.id is Int)
        assertNotNull(quote.quote)
        assertTrue(quote.quote is String)
    }

    private fun retrieveSpecificQuote(resource: String, quotes: List<Quote>) {
        val quoteId = 5
        val response = prepareGenericRequest(resource)
                .path(quoteId.toString()).request().get()
        assertEquals(Response.Status.OK.statusCode, response.status)
        assertEquals(DilbertQuoteService.mediaType, response.mediaType.toString())

        val quote = response.readEntity(Quote::class.java)
        val quoteIndex = quoteId - 1
        assertEquals(quotes[quoteIndex], quote)
    }
}