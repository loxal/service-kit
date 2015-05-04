/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.service.quote

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import net.loxal.soa.restkit.endpoint.Endpoint
import net.loxal.soa.restkit.model.dilbert.Quote
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import java.util.Random
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

Produces(DilbertQuoteService.mediaType)
Path(DilbertQuoteService.RESOURCE_PATH)
class DilbertQuoteService : Endpoint() {

    Path(RESOURCE_PATH_DEV)
    GET
    fun quoteDev(Context request: HttpServletRequest, Context requestContext: ContainerRequestContext, Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val randomQuoteIndex = random.nextInt(quotesDev.size())
        asyncResponse.resume(Response.ok(quotesDev[randomQuoteIndex]).build())

        LOG.info("${requestContext.getMethod()} for $randomQuoteIndex ${request.getContextPath()}")
    }

    Path("$RESOURCE_PATH_DEV/${Endpoint.ID_PATH_PARAM_PLACEHOLDER}")
    GET
    fun quoteDev(Context request: HttpServletRequest, Context requestContext: ContainerRequestContext,
                        PathParam(Endpoint.ID_PATH_PARAM) quoteId: Int,
                        Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val quoteIndex = quoteId - 1
        asyncResponse.resume(Response.ok(quotesDev[quoteIndex]).build())

        LOG.info("${requestContext.getMethod()} for $quoteId ${request.getContextPath()}")
    }

    Path(RESOURCE_PATH_MANAGER)
    GET
    fun quoteManager(Context request: HttpServletRequest, Context requestContext: ContainerRequestContext, Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val randomQuoteIndex = random.nextInt(quotesManager.size())
        asyncResponse.resume(Response.ok(quotesManager[randomQuoteIndex]).build())

        LOG.info("${requestContext.getMethod()} for $randomQuoteIndex ${request.getContextPath()}")
    }

    Path("$RESOURCE_PATH_MANAGER/${Endpoint.ID_PATH_PARAM_PLACEHOLDER}")
    GET
    fun quoteManager(Context request: HttpServletRequest, Context requestContext: ContainerRequestContext,
                            PathParam(Endpoint.ID_PATH_PARAM) quoteId: Int,
                            Suspended asyncResponse: AsyncResponse) {
        asyncResponse.setTimeout(Endpoint.ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val quoteIndex = quoteId - 1
        asyncResponse.resume(Response.ok(quotesManager[quoteIndex]).build())

        LOG.info("${requestContext.getMethod()} for $quoteId ${request.getContextPath()}")
    }

    init {
        quotesDevReader.close()
        quotesManagerReader.close()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(javaClass<DilbertQuoteService>())

        private data class Quotes : TypeReference<List<Quote>>()

        private val quotesType = Quotes()
        private val objectMapper = ObjectMapper()
        private val quotesDevReader = InputStreamReader(javaClass.getResourceAsStream("quotes-dev.json"))
        private val quotesManagerReader = InputStreamReader(javaClass.getResourceAsStream("quotes-manager.json"))
        private val quoteDevData = quotesDevReader.readText()
        private val quoteManagerData = quotesManagerReader.readText()
        val quotesDev: List<Quote> = objectMapper.readValue(quoteDevData, quotesType)
        val quotesManager: List<Quote> = objectMapper.readValue(quoteManagerData, quotesType)
        private val random: Random = Random()

        val RESOURCE_PATH = "dilbert-quote"
        val RESOURCE_PATH_DEV = "dev-excuse"
        val RESOURCE_PATH_MANAGER = "manager"
        val mediaType = MediaType.APPLICATION_JSON + ";charset=utf-8"
    }
}