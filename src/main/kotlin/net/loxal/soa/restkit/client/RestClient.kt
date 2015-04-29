/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.client

import java.util.logging.Logger
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.client.Invocation
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response

abstract class RestClient<out T> protected() {

    fun targetTenant(webTarget: WebTarget): Invocation.Builder = webTarget.request()

    fun applyToken(request: Invocation.Builder): Invocation.Builder = request.header(HttpHeaders.AUTHORIZATION, "Bearer ${RepositoryClient.retrieveToken().access_token}")

    fun authorizeRequest(target: WebTarget): Invocation.Builder = applyToken(targetTenant(target))

    abstract fun post(entity: Entity<in T>, id: String): Response

    abstract fun delete(entityType: Class<in T>, id: String): Response

    abstract fun get(entityType: Class<in T>, id: String): Response

    abstract fun put(json: Entity<in T>, id: String): Response

    companion object {
        val LOG = Logger.getGlobal()
        val CLIENT = ClientBuilder.newClient()
    }
}