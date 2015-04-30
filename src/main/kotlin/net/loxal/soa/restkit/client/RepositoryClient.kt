/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.client

import net.loxal.soa.restkit.model.common.AccessToken
import org.springframework.beans.factory.annotation.Value
import java.net.URI
import java.util.Properties
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.annotation.ManagedBean
import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.Form
import javax.ws.rs.core.MultivaluedHashMap

ManagedBean
class RepositoryClient<T> : RestClient<T>() {
    Value("\${repositoryServiceUri}")
    private val repositoryServiceUri: URI = URI.create("")
    Value("\${repositoryServiceProxyUri}")
    private val repositoryServiceProxyUri: URI = URI.create("")
    Value("\${tenant}")
    private val tenant: String = ""

    init {
        RestClient.LOG.info("repositoryServiceUri: ${repositoryServiceUri}")
    }

    override fun post(entity: Entity<in T>, id: String) =
            authorizeRequest(targetProxy(explicitType(entity)).path(id)).post(entity)

    override fun put(json: Entity<in T>, id: String) =
            authorizeRequest(targetProxy(explicitType(json)).path(id)).put(json)

    override fun delete(entityType: Class<in T>, id: String) =
            authorizeRequest(targetProxy(explicitType(entityType)).path(id)).delete()

    override fun get(entityType: Class<in T>, id: String) =
            authorizeRequest(targetProxy(explicitType(entityType)).path(id)).get()

    private fun targetDirect(entityType: String): WebTarget {
        RestClient.LOG.info("tenant: $tenant | clientId: $clientId")
        return RestClient.CLIENT.target(repositoryServiceUri).path(tenant).path(clientId).path(INFIX_PATH).path(entityType)
    }

    private fun targetProxy(entityType: String): WebTarget {
        RestClient.LOG.info("tenant: $tenant | clientId: $clientId")
        return RestClient.CLIENT.target(repositoryServiceProxyUri).path(tenant).path(clientId).path(INFIX_PATH).path(entityType)
    }

    private fun explicitType(entity: Entity<in T>) = entity.getEntity()!!.javaClass.getSimpleName().toLowerCase()

    private fun explicitType(entity: Class<in T>) = entity.getSimpleName().toLowerCase()

    companion object {
        public val clientId: String
        val INFIX_PATH: String = "data"
        var accessToken = AccessToken()
        private val properties = Properties()
        private val tokenRefresher: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

        init {
            properties.load(javaClass<RepositoryClient<Any>>().getResourceAsStream("/local.properties"))
            clientId = properties.getProperty("app.clientId")

            tokenRefresher.scheduleAtFixedRate(refreshToken(), 0, 3500, TimeUnit.SECONDS)
        }

        private final fun refreshToken() = Runnable {
            val newToken: AccessToken
            //            do {
            newToken = retrieveToken()
            //            } while (newToken.access_token.isEmpty())
            accessToken = newToken

            RestClient.LOG.info("A new accessToken ${accessToken.access_token} has been fetched")
            println("A new accessToken ${accessToken.toString()} ${accessToken} ${accessToken.access_token} has been fetched")
        }

        private final fun retrieveToken(): AccessToken {
            val tokenRequestBody = MultivaluedHashMap<String, String>()
            tokenRequestBody.putSingle("grant_type", "client_credentials")
            tokenRequestBody.putSingle("scope", "loxal.some_scope")
            tokenRequestBody.putSingle("client_id", clientId)
            tokenRequestBody.putSingle("client_secret", properties.getProperty("app.clientSecret"))

            val tokenRequestForm = Form(tokenRequestBody)

            val accessToken = RestClient.CLIENT.target(properties.getProperty("oAuth2ServiceUri"))
                    .request()
                    .post(Entity.form(tokenRequestForm))

            return accessToken.readEntity(javaClass<AccessToken>())
        }
    }
}