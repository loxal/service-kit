/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.service.quote

import javax.ws.rs.Path
import net.loxal.soa.restkit.endpoint.Endpoint
import javax.ws.rs.GET
import javax.ws.rs.core.Context
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.Suspended
import javax.ws.rs.container.AsyncResponse
import net.loxal.soa.restkit.model.dilbert.Quote
import com.fasterxml.jackson.core.type.TypeReference
import java.util.Random
import java.util.concurrent.TimeUnit
import javax.ws.rs.core.Response
import java.util.logging.Logger
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.InputStreamReader
import javax.ws.rs.PathParam

Path(DilbertQuoteService.RESOURCE_PATH)
public class DilbertQuoteService : Endpoint() {

    GET
    public fun quote(Context request: HttpServletRequest, Context requestContext: ContainerRequestContext, Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val randomQuoteIndex = random.nextInt(quotes.size())
        asyncResponse.resume(Response.ok(quotes[randomQuoteIndex]).build())

        LOG.info("${requestContext.getMethod()} for $randomQuoteIndex ${request.getContextPath()}")
    }

    Path(Endpoint.ID_PATH_PARAM_PLACEHOLDER)
    GET
    public fun quote(Context request: HttpServletRequest, Context requestContext: ContainerRequestContext,
                     PathParam(Endpoint.ID_PATH_PARAM) quoteId: Int,
                     Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val quoteIndex = quoteId - 1
        asyncResponse.resume(Response.ok(quotes[quoteIndex]).build())

        LOG.info("${requestContext.getMethod()} for $quoteId ${request.getContextPath()}")
    }

    {
        quotesReader.close()
    }

    class object {
        private val LOG = Logger.getGlobal()

        private data class Quotes : TypeReference<List<Quote>>()

        private val quotesType = Quotes()
        private val objectMapper = ObjectMapper()
        private val quotesReader = InputStreamReader(javaClass.getResourceAsStream("quotes.json"))
        private val quoteData = quotesReader.readText()
        val quotes: List<Quote> = objectMapper.readValue(quoteData, quotesType)
        private val random: Random = Random()

        internal val RESOURCE_PATH = "dilbert-dev-excuse"
        internal val RESOURCE_PATH_MANAGER = "dilbert-manager-quote"
    }
}