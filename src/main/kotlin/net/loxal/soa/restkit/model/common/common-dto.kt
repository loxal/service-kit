/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.model.common

import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.ws.rs.core.Response
import kotlin.platform.platformStatic

/**
 * Detailed error message used in response to provide all errors triggered by a request.
 */
data class ErrorMessage private() {
    NotNull Pattern(regexp = TYPE_REGEXP_PATTERN)
    var type: String? = Response.Status.BAD_REQUEST.getReasonPhrase()
    NotNull
    Min(value = 100)
    Max(value = 599)
    var status: Int = 0
    var message: String? = ""
    var moreInfo: String = ""
    var details: Set<ErrorDetail> = emptySet()

    companion object {
        val TYPE_REGEXP_PATTERN = "[a-z]+[[a-z]_]*[a-z]+"

        platformStatic fun create(errorMsg: String?): ErrorMessage {
            val e = ErrorMessage()
            e.type = errorMsg

            return e
        }
    }
}

data class ErrorDetail private() {
    var field: String = ""
    NotNull
    Pattern(regexp = ErrorMessage.TYPE_REGEXP_PATTERN)
    var type: String = ""
    var message: String = ""
}

data class AccessToken () {
    var access_token: String = ""
    var expires_in: Int = 0
    var token_type: String = ""
    var scope: String = ""
}