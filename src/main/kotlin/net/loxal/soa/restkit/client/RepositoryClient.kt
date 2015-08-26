/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.client

import net.loxal.soa.restkit.model.common.Authorization
import java.net.URI
import java.util.Properties
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.Form
import javax.ws.rs.core.MultivaluedHashMap

//ManagedBean
class RepositoryClient<T> : RestClient<T>() {
    //    Value("\${repositoryServiceProxyUri}")
    private val repositoryServiceProxyUri: URI = URI.create("https://api.yaas.io/hybris/document/b2")
    //    Value("\${tenant}")
    private val tenant: String = "default"

    override fun post(entity: Entity<in T>, id: String) =
            authorizeRequest(targetProxy(explicitType(entity)).path(id)).post(entity)

    override fun put(json: Entity<in T>, id: String) =
            authorizeRequest(targetProxy(explicitType(json)).path(id)).put(json)

    override fun delete(entityType: Class<in T>, id: String) =
            authorizeRequest(targetProxy(explicitType(entityType)).path(id)).delete()

    override fun get(entityType: Class<in T>, id: String) =
            authorizeRequest(targetProxy(explicitType(entityType)).path(id)).get()

    private fun targetProxy(entityType: String): WebTarget {
        RestClient.LOG.info("tenant: $tenant | clientId: $clientId | appId: $appId")
        return RestClient.CLIENT.target(repositoryServiceProxyUri).path(tenant).path(appId).path(INFIX_PATH).path(entityType)
    }

    private fun explicitType(entity: Entity<in T>) = entity.getEntity()!!.javaClass.getSimpleName().toLowerCase()

    private fun explicitType(entity: Class<in T>) = entity.getSimpleName().toLowerCase()

    companion object {
        public val appId: String
        public val clientId: String
        val INFIX_PATH: String = "data"
        var authorization = Authorization()
        private val properties = Properties()
        private val tokenRefresher: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

        init {
            properties.load(javaClass<RepositoryClient<Any>>().getResourceAsStream("/local.properties"))
            clientId = properties.getProperty("app.clientId")
            appId = properties.getProperty("appId")

            tokenRefresher.scheduleAtFixedRate(refreshToken(), 0, 3500, TimeUnit.SECONDS)
        }

        private final fun refreshToken() = Runnable {
            val newAuthorization: Authorization
            newAuthorization = authorize()
            authorization = newAuthorization

            RestClient.LOG.info("A new token ${authorization.access_token} has been fetched")
        }

        final fun authorize(): Authorization {
            val tokenRequestBody = MultivaluedHashMap<String, String>()
            tokenRequestBody.putSingle("grant_type", "client_credentials")
            tokenRequestBody.putSingle("scope", "hybris.document_manage hybris.document_view")
            tokenRequestBody.putSingle("client_id", clientId)
            tokenRequestBody.putSingle("client_secret", properties.getProperty("app.clientSecret"))

            val tokenRequestForm = Form(tokenRequestBody)

            val accessToken = RestClient.CLIENT.target(properties.getProperty("oAuth2ServiceUri"))
                    .request()
                    .post(Entity.form(tokenRequestForm))

            return accessToken.readEntity(javaClass<Authorization>())
        }
    }
}