/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.client

import javax.annotation.ManagedBean
import org.springframework.beans.factory.annotation.Value
import java.net.URI
import javax.ws.rs.client.Entity
import javax.ws.rs.core.Response
import javax.ws.rs.client.WebTarget

ManagedBean
class RepositoryClient<T> : RestClient<T>() {
    Value("\${repositoryServiceUri}")
    private val repositoryServiceUri: URI = URI.create("")
    Value("\${tenant}")
    private val tenant: String = ""

    {
        RestClient.LOG.info("repositoryServiceUri: " + repositoryServiceUri)
    }

    override fun post(entity: Entity<in T>): Response {

        return targetTenant(targetServiceRepository(explicitType(entity))).post(entity)
    }

    override fun put(json: Entity<in T>, id: String): Response {

        return targetTenant(targetServiceRepository(explicitType(json)).path(id)).put(json)
    }

    override fun delete(entityType: Class<in T>, id: String): Response {

        return targetTenant(targetServiceRepository(explicitType(entityType)).path(id)).delete()
    }

    override fun get(entityType: Class<in T>, id: String): Response {

        return targetTenant(targetServiceRepository(explicitType(entityType)).path(id)).get()
    }

    private fun targetServiceRepository(entityType: String): WebTarget {
        RestClient.LOG.info("Tenant: $tenant")
        return RestClient.CLIENT.target(repositoryServiceUri).path(tenant).path(SERVICE_PATH).path(REPOSITORY_PATH).path(entityType)
    }

    private fun explicitType(entity: Entity<in T>): String {
        return entity.getEntity()!!.javaClass.getSimpleName().toLowerCase()
    }

    private fun explicitType(entity: Class<in T>): String {
        return entity.getSimpleName().toLowerCase()
    }

    class object {
        public val SERVICE_PATH: String = "rest-kit"
        public val REPOSITORY_PATH: String = "data"
    }
}