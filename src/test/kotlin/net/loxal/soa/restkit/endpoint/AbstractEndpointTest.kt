/*
 * Copyright 2016 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import net.loxal.soa.restkit.App
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.WebTarget

abstract class AbstractEndpointTest {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(AbstractEndpointTest::class.java)
        val NON_EXISTENT: String = "non-existent"
        val SERVICE_TARGET: String
        private val client = ClientBuilder.newClient()
        private val mapper = ObjectMapper()
        var resourcePath: String = "/"

        fun prepareGenericRequest(path: String): WebTarget {
            return prepareTarget(SERVICE_TARGET).path(path)
        }

        fun prepareTarget(target: String): WebTarget {
            return client.target(target)
        }

        fun prepareTarget(target: URI): WebTarget {
            return client.target(target)
        }

        init {
            SERVICE_TARGET = App.PROPERTIES.getProperty("deploymentTarget")

            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            client.register(mapper)
        }
    }
}