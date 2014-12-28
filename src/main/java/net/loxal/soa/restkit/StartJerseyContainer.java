/*
 * Copyright 2014 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public final class StartJerseyContainer {
    private static final Logger LOG = Logger.getGlobal();

    private StartJerseyContainer() {
        throw new AssertionError("Utility class should not be instantiated.");
    }

    public static void main(final String... args) {
        URI baseUri = UriBuilder.fromUri("http://local.loxal.net").port(8200).build();
        App jaxRsApp = new App();
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("/META-INF/applicationContext.xml");
        jaxRsApp.property("contextConfig", applicationContext);
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, jaxRsApp);

        try {
            server.start();
            LOG.info("Press any key to stop the server...");
            int keyPressed = System.in.read();
            LOG.info("Key pressed: " + keyPressed);
        } catch (IOException e) {
            LOG.severe(e.getMessage());
        }
    }
}
