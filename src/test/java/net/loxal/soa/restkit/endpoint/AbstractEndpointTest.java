/*
 * Copyright 2014 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint;

import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public abstract class AbstractEndpointTest {
    protected static final String NON_EXISTENT = "non-existent";
    static final Logger LOG = Logger.getGlobal();
    private static final String SERVICE_TARGET;
    private static final Client client = ClientBuilder.newClient();
    public static Properties properties;
    protected static String resourcePath = "/";

    public static WebTarget prepareGenericRequest(String path) {
        return prepareTarget(SERVICE_TARGET).path(path);
    }

    public static WebTarget prepareTarget(String target) {
        return client.target(target);
    }

    public static WebTarget prepareTarget(URI target) {
        return client.target(target);
    }

    static {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        properties = new Properties();
        try {
            properties.load(classLoader.getResourceAsStream("local.properties"));
        } catch (IOException e) {
            LOG.severe(String.format("Impossible to load property file. \n %s", e.getMessage()));
        }
        SERVICE_TARGET = properties.getProperty("deploymentTarget");
    }

    @Test
    public void nonExistentPathNotFound() throws Exception {
        Response response = prepareGenericRequest(resourcePath).path(NON_EXISTENT).request().get();
        LOG.info("resourcePath: " + resourcePath);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
    }
}
