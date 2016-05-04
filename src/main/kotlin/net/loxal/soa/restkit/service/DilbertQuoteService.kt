/*
 * Copyright 2016 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import net.loxal.soa.restkit.endpoint.Endpoint
import net.loxal.soa.restkit.model.dilbert.Dict
import net.loxal.soa.restkit.model.dilbert.Quote
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import java.util.*
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

@Produces(DilbertQuoteService.mediaType)
@Path(DilbertQuoteService.RESOURCE_PATH)
class DilbertQuoteService : Endpoint() {

    private fun fetchRandomDilberty(asyncResponse: AsyncResponse, request: HttpServletRequest, requestContext: ContainerRequestContext, dilbertesque: List<Any>) {
        asyncResponse.setTimeout(ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val randomQuoteIndex = random.nextInt(dilbertesque.size)
        asyncResponse.resume(Response.ok(dilbertesque[randomQuoteIndex]).build())

        LOG.info("${requestContext.method} for $randomQuoteIndex ${request.contextPath}")
    }

    @Path(RESOURCE_PATH_EXPERT)
    @GET
    fun quoteExpert(@Context request: HttpServletRequest, @Context requestContext: ContainerRequestContext, @Suspended asyncResponse: AsyncResponse) {
        fetchRandomDilberty(asyncResponse, request, requestContext, dilbertesque = quotesExpert)
    }

    @Path(RESOURCE_PATH_ENTERPRISE)
    @GET
    fun quoteEnterprise(@Context request: HttpServletRequest, @Context requestContext: ContainerRequestContext, @Suspended asyncResponse: AsyncResponse) {
        fetchRandomDilberty(asyncResponse, request, requestContext, dilbertesque = quotesEnterprise)
    }

    @Path(RESOURCE_PATH_MANAGER)
    @GET
    fun quoteManager(@Context request: HttpServletRequest, @Context requestContext: ContainerRequestContext, @Suspended asyncResponse: AsyncResponse) {
        fetchRandomDilberty(asyncResponse, request, requestContext, dilbertesque = quotesManager)
    }

    @Path(RESOURCE_PATH_PROGRAMMER)
    @GET
    fun quoteProgrammer(@Context request: HttpServletRequest, @Context requestContext: ContainerRequestContext, @Suspended asyncResponse: AsyncResponse) {
        fetchRandomDilberty(asyncResponse, request, requestContext, dilbertesque = quotesProgrammer)
    }

    //The reference guide how to become an IT pro.       / What makes you a “Expert of Stuff” // What an expert says
    @Path("$RESOURCE_PATH_EXPERT/${ID_PATH_PARAM_PLACEHOLDER}")
    @GET
    fun quoteExpert(@Context request: HttpServletRequest, @Context requestContext: ContainerRequestContext,
                    @PathParam(ID_PATH_PARAM) quoteId: Int,
                    @Suspended asyncResponse: AsyncResponse) {
        fetchDilbertyById(asyncResponse, quoteId, request, requestContext, dilbertesque = quotesExpert)
    }

    @Path("$RESOURCE_PATH_ENTERPRISE/${ID_PATH_PARAM_PLACEHOLDER}")
    @GET
    fun quoteEnterprise(@Context request: HttpServletRequest, @Context requestContext: ContainerRequestContext,
                        @PathParam(ID_PATH_PARAM) quoteId: Int,
                        @Suspended asyncResponse: AsyncResponse) {
        fetchDilbertyById(asyncResponse, quoteId, request, requestContext, dilbertesque = quotesEnterprise)
    }

    private fun fetchDilbertyById(asyncResponse: AsyncResponse, quoteId: Int, request: HttpServletRequest, requestContext: ContainerRequestContext, dilbertesque: List<Any>) {
        asyncResponse.setTimeout(ASYNC_RESPONSE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val quoteIndex = quoteId - 1
        asyncResponse.resume(Response.ok(dilbertesque[quoteIndex]).build())

        LOG.info("${requestContext.method} for $quoteId ${request.contextPath}")
    }

    @Path("$RESOURCE_PATH_MANAGER/${ID_PATH_PARAM_PLACEHOLDER}")
    @GET
    fun quoteManager(@Context request: HttpServletRequest, @Context requestContext: ContainerRequestContext,
                     @PathParam(ID_PATH_PARAM) quoteId: Int,
                     @Suspended asyncResponse: AsyncResponse) {
        fetchDilbertyById(asyncResponse, quoteId, request, requestContext, dilbertesque = quotesManager)
    }

    @Path("$RESOURCE_PATH_PROGRAMMER/${ID_PATH_PARAM_PLACEHOLDER}")
    @GET
    fun quoteProgrammer(@Context request: HttpServletRequest, @Context requestContext: ContainerRequestContext,
                        @PathParam(ID_PATH_PARAM) quoteId: Int,
                        @Suspended asyncResponse: AsyncResponse) {
        fetchDilbertyById(asyncResponse, quoteId, request, requestContext, dilbertesque = quotesProgrammer)
    }

    init {
        quotesExpertReader.close()
        quotesEnterpriseReader.close()
        quotesManagerReader.close()
        quotesProgrammerReader.close()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(DilbertQuoteService::class.java)

        private class Dicts : TypeReference<List<Dict>>()
        private class Quotes : TypeReference<List<Quote>>()

        private val dictsType = Dicts()
        private val quotesType = Quotes()
        private val objectMapper = ObjectMapper()

        private val quotesExpertReader = InputStreamReader(DilbertQuoteService::class.java.getResourceAsStream("dictionary-dilbert-expert.json"))
        private val quotesEnterpriseReader = InputStreamReader(DilbertQuoteService::class.java.getResourceAsStream("quotes-dilbert-enterprise.json"))
        private val quotesManagerReader = InputStreamReader(DilbertQuoteService::class.java.getResourceAsStream("quotes-dilbert-manager.json"))
        private val quotesProgrammerReader = InputStreamReader(DilbertQuoteService::class.java.getResourceAsStream("quotes-dilbert-programmer.json"))

        private val quoteExpertData = quotesExpertReader.readText()
        private val quoteEnterpriseData = quotesEnterpriseReader.readText()
        private val quoteManagerData = quotesManagerReader.readText()
        private val quoteProgrammerData = quotesProgrammerReader.readText()
        val quotesExpert: List<Dict> = objectMapper.readValue(quoteExpertData, dictsType)
        val quotesEnterprise: List<Quote> = objectMapper.readValue(quoteEnterpriseData, quotesType)
        val quotesManager: List<Quote> = objectMapper.readValue(quoteManagerData, quotesType)
        val quotesProgrammer: List<Quote> = objectMapper.readValue(quoteProgrammerData, quotesType)
        private val random: Random = Random()

        const val RESOURCE_PATH = "dilbert-quote"
        const val RESOURCE_PATH_PROGRAMMER = "programmer"
        const val RESOURCE_PATH_MANAGER = "manager"
        const val RESOURCE_PATH_ENTERPRISE = "enterprise"
        const val RESOURCE_PATH_EXPERT = "expert"
        const val mediaType = MediaType.APPLICATION_JSON + ";charset=utf-8"
    }
}