/*
 * Copyright 2016 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.client

import net.loxal.soa.restkit.App
import net.loxal.soa.restkit.model.common.Authorization
import java.net.URI
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.Form
import javax.ws.rs.core.MultivaluedHashMap

class KitClient<T> : AbstractKitClient<T>() {

    override fun post(entity: Entity<in T>, id: String) =
            authorizeRequest(targetProxy(explicitType(entity)).path(id)).post(entity)

    override fun put(json: Entity<in T>, id: String) =
            authorizeRequest(targetProxy(explicitType(json)).path(id)).put(json)

    override fun delete(entityType: Class<in T>, id: String) =
            authorizeRequest(targetProxy(explicitType(entityType)).path(id)).delete()

    override fun get(entityType: Class<in T>, id: String) =
            authorizeRequest(targetProxy(explicitType(entityType)).path(id)).get()

    private fun targetProxy(entityType: String): WebTarget {
        AbstractKitClient.LOG.info("tenant: $tenant | clientId: $clientId | appId: $appId | bearer token: ${authorization.access_token}")
        return AbstractKitClient.CLIENT.target(repositoryServiceProxyUrl).path(tenant).path(appId).path(INFIX_PATH).path(entityType)
    }

    private fun explicitType(entity: Entity<in T>) = entity.entity!!.javaClass.simpleName.toLowerCase()

    private fun explicitType(entity: Class<in T>) = entity.simpleName.toLowerCase()

    companion object {
        val appId: String
        val clientId: String
        const val INFIX_PATH: String = "data"
        var authorization: Authorization
        private val tokenRefresher: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
        val repositoryServiceProxyUrl: URI
        val tenant: String

        init {
            clientId = App.PROPERTIES.getProperty("app.clientId")
            appId = App.PROPERTIES.getProperty("appId")
            tenant = App.PROPERTIES.getProperty("tenant")
            repositoryServiceProxyUrl = URI.create(App.PROPERTIES.getProperty("repositoryServiceProxyUrl"))

            tokenRefresher.scheduleAtFixedRate(refreshToken(), 0, 3500, TimeUnit.SECONDS)
            authorization = authorize()
        }

        private final fun refreshToken() = Runnable {
            val newAuthorization: Authorization
            newAuthorization = authorize()
            authorization = newAuthorization

            AbstractKitClient.LOG.info("A new bearer token ${authorization.access_token} has been fetched.")
        }

        final fun authorize(): Authorization {
            val tokenRequestBody = MultivaluedHashMap<String, String>()
            tokenRequestBody.putSingle("grant_type", "client_credentials")
            tokenRequestBody.putSingle("scope", "hybris.document_manage hybris.document_view")
            tokenRequestBody.putSingle("client_id", clientId)
            tokenRequestBody.putSingle("client_secret", App.PROPERTIES.getProperty("app.clientSecret"))

            val tokenRequestForm = Form(tokenRequestBody)

            val accessToken = AbstractKitClient.CLIENT.target(App.PROPERTIES.getProperty("oAuth2ServiceUrl"))
                    .request()
                    .post(Entity.form(tokenRequestForm))

            return accessToken.readEntity(Authorization::class.java)
        }
    }
}