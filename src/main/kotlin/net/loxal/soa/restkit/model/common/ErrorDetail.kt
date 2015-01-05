/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.model.common

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

public class ErrorDetail private() {
    var field: String = ""
    NotNull
    Pattern(regexp = ErrorMessage.TYPE_REGEXP_PATTERN)
    var type: String = ""
    var message: String = ""
}