/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint

import org.junit.Test
import java.util.Properties
import javax.ws.rs.client.WebTarget
import java.net.URI
import java.io.IOException
import javax.ws.rs.core.Response
import javax.ws.rs.core.MediaType
import java.util.logging.Logger
import javax.ws.rs.client.ClientBuilder
import kotlin.test.assertEquals
import kotlin.platform.platformStatic

public abstract class AbstractEndpointTest {

    Test
    public fun nonExistentPathNotFound() {
        val response = prepareGenericRequest(resourcePath).path(NON_EXISTENT).request().get()
        LOG.info("resourcePath: " + resourcePath)

        assertEquals(Response.Status.NOT_FOUND.getStatusCode().toLong(), response.getStatus().toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType())
    }

    class object {
        val NON_EXISTENT: String = "non-existent"
        val LOG = Logger.getGlobal()
        platformStatic val SERVICE_TARGET: String
        private val client = ClientBuilder.newClient()
        public var properties: Properties
        protected var resourcePath: String = "/"

        public platformStatic fun prepareGenericRequest(path: String): WebTarget {
            return prepareTarget(SERVICE_TARGET).path(path)
        }

        public platformStatic fun prepareTarget(target: String): WebTarget {
            return client.target(target)
        }

        public platformStatic fun prepareTarget(target: URI): WebTarget {
            return client.target(target)
        }

        {
            val classLoader = Thread.currentThread().getContextClassLoader()
            properties = Properties()
            try {
                properties.load(classLoader.getResourceAsStream("local.properties"))
            } catch (e: IOException) {
                LOG.severe("Impossible to load property file. \n ${e.getMessage()}")
            }

            SERVICE_TARGET = properties.getProperty("deploymentTarget")
        }
    }
}