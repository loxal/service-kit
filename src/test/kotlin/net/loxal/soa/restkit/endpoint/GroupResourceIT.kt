/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint

import javax.ws.rs.core.Response
import net.loxal.soa.restkit.model.generic.Group
import org.junit.Test
import net.loxal.soa.restkit.model.common.ErrorMessage
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import java.util.Arrays
import kotlin.test.assertEquals

public class GroupResourceIT : AbstractEndpointTest() {

    private fun postEntity(): Response {
        val group = Group("name", ENTRIES)

        val createdEntity = AbstractEndpointTest.prepareGenericRequest(GroupResource.RESOURCE_PATH).request().post(Entity.json<Group>(group))
        assertEquals(Response.Status.CREATED.getStatusCode().toLong(), createdEntity.getStatus().toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, createdEntity.getMediaType())

        return createdEntity
    }

    Test
    throws(javaClass<Exception>())
    public fun createEntity() {
        val response = postEntity()

        assertEquals(Response.Status.CREATED.getStatusCode().toLong(), response.getStatus().toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType())
        assertEquals(false, response.getLocation().getPath().endsWith("null"))
        assertEquals(true, response.getLocation().getPath().startsWith(Endpoint.URI_PATH_SEPARATOR + GroupResource.RESOURCE_PATH))
        assertEquals(true, response.getLocation().getSchemeSpecificPart().contains(GroupResource.RESOURCE_PATH + Endpoint.URI_PATH_SEPARATOR))
        assertEquals(false, response.getLocation().getSchemeSpecificPart().endsWith(GroupResource.RESOURCE_PATH))
    }

    Test
    throws(javaClass<Exception>())
    public fun deleteNonExistentEntity() {
        val existingEntity = postEntity()

        val response = AbstractEndpointTest.prepareTarget("${existingEntity.getLocation()} ${AbstractEndpointTest.NON_EXISTENT}").request().delete()

        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType())
        val notFoundStatus = Response.Status.NOT_FOUND
        assertEquals(notFoundStatus.getStatusCode().toLong(), response.getStatus().toLong())
        val errorMessage = response.readEntity<ErrorMessage>(javaClass<ErrorMessage>())
        assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), errorMessage.type)
    }

    Test
    throws(javaClass<Exception>())
    public fun deleteExistingEntity() {
        val existingEntity = postEntity()

        val deletion = AbstractEndpointTest.prepareTarget(existingEntity.getLocation()).request().delete()

        assertEquals(Response.Status.NO_CONTENT.getStatusCode().toLong(), deletion.getStatus().toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, deletion.getMediaType())
    }

    Test
    throws(javaClass<Exception>())
    public fun retrieveExistingEntity() {
        val existingEntity = postEntity()

        val retrieval = AbstractEndpointTest.prepareTarget(existingEntity.getLocation()).request().get()

        assertEquals(Response.Status.OK.getStatusCode().toLong(), retrieval.getStatus().toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, retrieval.getMediaType())
        val retrievedEntity = retrieval.readEntity<Group>(javaClass<Group>())
        assertEquals(ENTRIES.size().toLong(), retrievedEntity.entityReferences.size().toLong())
        assertEquals(false, retrievedEntity.entityReferences.isEmpty())
    }

    Test
    throws(javaClass<Exception>())
    public fun retrieveNonExistentEntity() {
        val existingEntity = postEntity()

        val retrieval = AbstractEndpointTest.prepareTarget("${existingEntity.getLocation()} ${AbstractEndpointTest.NON_EXISTENT}").request().get()

        assertEquals(Response.Status.NOT_FOUND.getStatusCode().toLong(), retrieval.getStatus().toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, retrieval.getMediaType())
        val notFoundError = retrieval.readEntity<ErrorMessage>(javaClass<ErrorMessage>())
        assertEquals(Response.Status.BAD_REQUEST.getReasonPhrase(), notFoundError.type)
    }

    Test
    throws(javaClass<Exception>())
    public fun updateExistingEntity() {
        val existingEntity = postEntity()

        val updatedField = "updated field"
        val entries = Arrays.asList<String>("first", "second")
        val modifiedEntity = Group(updatedField, entries)

        val update = AbstractEndpointTest.prepareTarget(existingEntity.getLocation()).request().put(Entity.json<Group>(modifiedEntity))

        assertEquals(Response.Status.OK.getStatusCode().toLong(), update.getStatus().toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, update.getMediaType())

        val retrievedUpdatedEntity = AbstractEndpointTest.prepareTarget(existingEntity.getLocation()).request().get()
        val updatedEntity = retrievedUpdatedEntity.readEntity<Group>(javaClass<Group>())
        assertEquals(updatedField, updatedEntity.name)
        assertEquals(entries.size().toLong(), updatedEntity.entityReferences.size().toLong())
        assertEquals(entries, updatedEntity.entityReferences)
    }

    Test
    throws(javaClass<Exception>())
    public fun updateNonExistentEntity() {
        val existingEntity = postEntity()

        val someEntity = Group("Irrelevant", listOf<String>())
        val update = AbstractEndpointTest.prepareTarget("${existingEntity.getLocation()} ${AbstractEndpointTest.NON_EXISTENT}").request().put(Entity.json<Group>(someEntity))

        assertEquals(Response.Status.NOT_FOUND.getStatusCode().toLong(), update.getStatus().toLong())
        assertEquals(MediaType.APPLICATION_JSON_TYPE, update.getMediaType())
    }

    class object {

        private val ENTRIES = Arrays.asList<String>("first", "second", "third")
    }
}