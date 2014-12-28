/*
 * Copyright 2014 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.model.common;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.core.Response;
import java.util.Set;

/**
 * Detailed error message used in response to provide all errors triggered by a request.
 */
public class ErrorMessage {
    static final String TYPE_REGEXP_PATTERN = "[a-z]+[[a-z]_]*[a-z]+";
    @NotNull
    @Min(100)
    @Max(599)
    private int status;
    @NotNull
    @Pattern(regexp = TYPE_REGEXP_PATTERN)
    private String type;
    private String message;
    private String moreInfo;
    private Set<ErrorDetail> details;

    /**
     * Satisfy constraint with a default value
     */
    public ErrorMessage() {
        this(Response.Status.BAD_REQUEST.getReasonPhrase());
    }

    /**
     * Assure constraints
     */
    public ErrorMessage(final String type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(final String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public Set<ErrorDetail> getDetails() {
        return details;
    }

    public void setDetails(final Set<ErrorDetail> details) {
        this.details = details;
    }
}
