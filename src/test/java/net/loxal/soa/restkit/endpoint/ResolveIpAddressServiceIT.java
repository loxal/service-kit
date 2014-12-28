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

public class ResolveIpAddressServiceIT extends AbstractEndpointTest {
    @Before
    public void setUp() throws Exception {
        resourcePath = ResolveIpAddressService.RESOURCE_PATH;
    }

    @Test
    public void assureHostAddress() throws Exception {
        String hostName = "loxal.net";
        String loxalNetIpAddress = "216.239.32.21";

        Response response = prepareGenericRequest(ResolveIpAddressService.RESOURCE_PATH)
                .queryParam(ResolveIpAddressService.HOST_NAME_PARAM, hostName).request().get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());

        Host host = response.readEntity(Host.class);
        assertEquals(loxalNetIpAddress, host.getAddress());
        assertEquals(hostName, host.getName());
    }
}
