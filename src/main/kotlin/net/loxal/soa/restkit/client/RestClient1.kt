/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.client

import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.client.Invocation
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.Response
import java.util.logging.Logger

abstract class RestClient1<out T> protected() {

    fun targetTenant(webTarget: WebTarget): Invocation.Builder {
        return webTarget.request()
    }

    public abstract fun post(entity: Entity<in T>): Response

    public abstract fun delete(entityType: Class<in T>, id: String): Response

    public abstract fun get(entityType: Class<in T>, id: String): Response

    public abstract fun put(json: Entity<in T>, id: String): Response

    class object {
        val LOG = Logger.getGlobal()
        val CLIENT = ClientBuilder.newClient()
    }
}