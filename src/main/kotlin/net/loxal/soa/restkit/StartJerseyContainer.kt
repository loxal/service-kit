/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.springframework.context.support.ClassPathXmlApplicationContext

import javax.ws.rs.core.UriBuilder
import java.io.IOException
import java.util.logging.Logger

public class StartJerseyContainer private() {

    {
        throw AssertionError("Utility class should not be instantiated.")
    }

    class object {
        private val LOG = Logger.getGlobal()

        public fun main(vararg args: String) {
            LOG.info(args.toString())
            val baseUri = UriBuilder.fromUri("http://local.loxal.net").port(8200).build()
            val jaxRsApp = App()
            val applicationContext = ClassPathXmlApplicationContext("/META-INF/applicationContext.xml")
            jaxRsApp.property("contextConfig", applicationContext)
            val server = GrizzlyHttpServerFactory.createHttpServer(baseUri, jaxRsApp)

            try {
                server.start()
                LOG.info("Press any key to stop the server...")
                val keyPressed = System.`in`.read()
                LOG.info("Key pressed: " + keyPressed)
            } catch (e: IOException) {
                LOG.severe(e.getMessage())
            }
        }
    }
}

fun main(vararg args: String) = StartJerseyContainer.main(*args)
