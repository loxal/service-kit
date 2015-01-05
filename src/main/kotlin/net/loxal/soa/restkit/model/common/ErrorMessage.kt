/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.model.common

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Min
import javax.validation.constraints.Max
import kotlin.platform.platformStatic
import javax.ws.rs.core.Response

/**
 * Detailed error message used in response to provide all errors triggered by a request.
 */
public class ErrorMessage private() {
    NotNull Pattern(regexp = TYPE_REGEXP_PATTERN)
    var type: String? = Response.Status.BAD_REQUEST.getReasonPhrase()
    NotNull
    Min(value = 100)
    Max(value = 599)
    var status: Int = 0
    var message: String = ""
    var moreInfo: String = ""
    var details: Set<ErrorDetail> = emptySet()

    class object {
        val TYPE_REGEXP_PATTERN = "[a-z]+[[a-z]_]*[a-z]+"

        platformStatic fun create(errorMsg: String?): ErrorMessage {
            val e = ErrorMessage()
            e.type = errorMsg

            return e
        }
    }
}