/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.mapper

import javax.ws.rs.ext.Provider
import javax.ws.rs.WebApplicationException
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.core.Response
import javax.ws.rs.NotFoundException
import javax.ws.rs.ForbiddenException
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.NotAuthorizedException
import javax.ws.rs.core.MediaType
import net.loxal.soa.restkit.model.common.ErrorMessage
import org.glassfish.jersey.server.ParamException

/**
 * Maps Java exceptions to HTTP error codes.
 */
Provider
public class ErrorStatusCodeMapper : ExceptionMapper<WebApplicationException> {
    override fun toResponse(exception: WebApplicationException): Response {
        var status: Response.Status? = EXCEPTION_TO_ERROR_MAPPING.get(exception.javaClass)
        if (status == null) {
            status = Response.Status.BAD_REQUEST
        }

        val errorMessage = ErrorMessage.create(status!!.getReasonPhrase())
        errorMessage.status = status!!.getStatusCode()
        errorMessage.message = exception.getMessage()
        errorMessage.moreInfo = status!!.getFamily().name()
        // TODO make specification of "type(MediaType.APPLICATION_JSON_TYPE)" superfluous
        return Response.status(status!!.getStatusCode()).type(MediaType.APPLICATION_JSON_TYPE).entity(errorMessage).build()
    }

    class object {
        private val EXCEPTION_TO_ERROR_MAPPING = mapOf(
                javaClass<NotFoundException>() to Response.Status.NOT_FOUND,
                javaClass<ParamException.PathParamException>() to Response.Status.NOT_FOUND,
                javaClass<ForbiddenException>()to Response.Status.FORBIDDEN,
                javaClass<InternalServerErrorException>() to Response.Status.INTERNAL_SERVER_ERROR,
                javaClass<NotAuthorizedException>() to Response.Status.UNAUTHORIZED
        )
    }
}