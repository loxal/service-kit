/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.client

import net.loxal.soa.restkit.model.common.AccessToken
import org.springframework.beans.factory.annotation.Value
import java.net.URI
import java.util.Properties
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

    private val token: String = ""

    init {
        RestClient.LOG.info("repositoryServiceUri: ${repositoryServiceUri}")
    }

    override fun post(entity: Entity<in T>, id: String) =
            targetTenant(targetServiceRepository(explicitType(entity)).path(id)).post(entity)

    override fun put(json: Entity<in T>, id: String) =
            targetTenant(targetServiceRepository(explicitType(json)).path(id)).put(json)

    override fun delete(entityType: Class<in T>, id: String) =
            targetTenant(targetServiceRepository(explicitType(entityType)).path(id)).delete()

    override fun get(entityType: Class<in T>, id: String) =
            targetTenant(targetServiceRepository(explicitType(entityType)).path(id)).get()

    private fun targetServiceRepository(entityType: String): WebTarget {
        RestClient.LOG.info("Tenant: $tenant")
        return RestClient.CLIENT.target(repositoryServiceUri).path(tenant).path(clientId).path(INFIX_PATH).path(entityType)
    }

    private fun targetRepository(entityType: String): WebTarget {
        RestClient.LOG.info("Tenant: $tenant")
        return RestClient.CLIENT.target(repositoryServiceProxyUri).path(tenant).path(clientId).path(INFIX_PATH).path(entityType)
    }

    private fun explicitType(entity: Entity<in T>) = entity.getEntity()!!.javaClass.getSimpleName().toLowerCase()

    private fun explicitType(entity: Class<in T>) = entity.getSimpleName().toLowerCase()

    companion object {
        public val clientId: String
        val INFIX_PATH: String = "data"
        private val properties = Properties()

        init {
            properties.load(javaClass<RepositoryClient<Any>>().getResourceAsStream("/local.properties"))
            clientId = properties.getProperty("app.clientId")
        }

        final fun retrieveToken(): AccessToken {
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