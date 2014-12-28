/*
 * Copyright 2014 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint;

import net.loxal.soa.restkit.model.whoami.Host;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class WhoamiServiceIT extends AbstractEndpointTest {
    @Before
    public void setUp() throws Exception {
        resourcePath = WhoamiService.RESOURCE_PATH;
    }

    @Test
    public void validateHost() throws Exception {
        Response response = prepareGenericRequest(WhoamiService.RESOURCE_PATH).request().get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());

        String localhost = "127.0.0.1";
        Host host = response.readEntity(Host.class);
        assertEquals(localhost, host.getAddress());
        assertEquals(localhost, host.getName());
    }
}
