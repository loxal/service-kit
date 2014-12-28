/*
 * Copyright 2014 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.model.common;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class ErrorDetail {
    private String field;
    @NotNull
    @Pattern(regexp = ErrorMessage.TYPE_REGEXP_PATTERN)
    private String type;
    private String message;

    public String getField() {
        return field;
    }

    public void setField(final String field) {
        this.field = field;
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
}