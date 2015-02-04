/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint

import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.core.Context
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.Suspended
import javax.ws.rs.container.AsyncResponse
import java.util.concurrent.TimeUnit
import javax.ws.rs.core.Response
import java.util.logging.Logger
import net.loxal.soa.restkit.model.dilbert.Quote
import java.io.InputStreamReader
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.type.TypeReference
import java.util.Random

Path(DilbertQuote.RESOURCE_PATH)
public class DilbertQuote : Endpoint() {

    GET
    public fun quote(Context request: HttpServletRequest, Context requestContext: ContainerRequestContext, Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val randomQuoteIndex = random.nextInt(quotes.size())
        asyncResponse.resume(Response.ok(quotes[randomQuoteIndex]).build())

        LOG.info("${requestContext.getMethod()} for $randomQuoteIndex")
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
        private val quotes: List<Quote> = objectMapper.readValue(quoteData, quotesType)
        private val random: Random = Random()

        internal val RESOURCE_PATH = "dilbert-dev-excuse"
        internal val RESOURCE_PATH_MANAGER = "dilbert-manager-quote"
    }
}
