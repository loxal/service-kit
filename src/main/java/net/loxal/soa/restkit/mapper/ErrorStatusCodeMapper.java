/*
 * Copyright 2014 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.mapper;

import net.loxal.soa.restkit.model.common.ErrorMessage;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps Java exceptions to HTTP error codes.
 */
@Provider
public class ErrorStatusCodeMapper implements ExceptionMapper<WebApplicationException> {

    private static final Map<Class<? extends WebApplicationException>, Response.Status> EXCEPTION_TO_ERROR_MAPPING = new HashMap<>();

    static {
        EXCEPTION_TO_ERROR_MAPPING.put(NotFoundException.class, Response.Status.NOT_FOUND);
        EXCEPTION_TO_ERROR_MAPPING.put(ForbiddenException.class, Response.Status.FORBIDDEN);
        EXCEPTION_TO_ERROR_MAPPING.put(InternalServerErrorException.class, Response.Status.INTERNAL_SERVER_ERROR);
        EXCEPTION_TO_ERROR_MAPPING.put(NotAuthorizedException.class, Response.Status.UNAUTHORIZED);
    }

    @Override
    public Response toResponse(final WebApplicationException exception) {
        Response.Status status = EXCEPTION_TO_ERROR_MAPPING.get(exception.getClass());
        if (status == null) {
            status = Response.Status.BAD_REQUEST;
        }

        final ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setStatus(status.getStatusCode());
        errorMessage.setMessage(exception.getMessage());
        errorMessage.setMoreInfo(status.getFamily().name());
        errorMessage.setType(status.getReasonPhrase());
        return Response.status(status.getStatusCode())
                // TODO make specification of "type(MediaType.APPLICATION_JSON_TYPE)" superfluous
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(errorMessage).build();
    }

}
