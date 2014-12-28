/*
 * Copyright 2014 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

public abstract class RESTClient<T> {
    static final Logger LOG = Logger.getGlobal();
    static final Client CLIENT = ClientBuilder.newClient();

    Invocation.Builder targetTenant(WebTarget webTarget) {
        return webTarget.request();
    }

    public abstract Response post(Entity<T> entity);

    public abstract Response delete(Class<T> entityType, String id);

    public abstract Response get(Class<T> entityType, String id);

    public abstract Response put(Entity<T> json, String id);
}