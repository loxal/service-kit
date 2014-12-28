/*
 * Copyright 2014 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.client;

import org.springframework.beans.factory.annotation.Value;

import javax.annotation.ManagedBean;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;

@ManagedBean
public class RepositoryClient<T> extends RESTClient<T> {
    public static final String SERVICE_PATH = "rest-kit";
    public static final String REPOSITORY_PATH = "data";
    @Value("${repositoryServiceUri}")
    private URI repositoryServiceUri;
    @Value("${tenant}")
    private String tenant;

    private RepositoryClient() {
        LOG.info("repositoryServiceUri: " + repositoryServiceUri);
    }

    @Override
    public Response post(Entity<T> entity) {

        return targetTenant(targetServiceRepository(explicitType(entity)))
                .post(entity);
    }

    @Override
    public Response put(Entity<T> entity, String id) {

        return targetTenant(targetServiceRepository(explicitType(entity))
                .path(id))
                .put(entity);
    }

    @Override
    public Response delete(Class<T> entity, String id) {

        return targetTenant(targetServiceRepository(explicitType(entity))
                .path(id))
                .delete();
    }

    @Override
    public Response get(Class<T> entity, String id) {

        return targetTenant(targetServiceRepository(explicitType(entity))
                .path(id))
                .get();
    }

    private WebTarget targetServiceRepository(String entityType) {
        LOG.info(String.format("Tenant: %s", tenant));
        return CLIENT
                .target(repositoryServiceUri)
                .path(tenant)
                .path(SERVICE_PATH)
                .path(REPOSITORY_PATH)
                .path(entityType);
    }

    private String explicitType(Entity<T> entity) {
        return entity.getEntity().getClass().getSimpleName().toLowerCase();
    }

    private String explicitType(Class<T> entity) {
        return entity.getSimpleName().toLowerCase();
    }
}
