/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint

import java.util.logging.Logger
import java.text.MessageFormat
import java.util.regex.Pattern

abstract class Endpoint protected() {
    class object {
        public val URI_PATH_SEPARATOR: String = "/"
        val ASYNC_RESPONSE_TIMEOUT: Int = 3
        val ID_PATH_PARAM: String = "id"
        val ID_PATH_PARAM_PLACEHOLDER: String = "{" + ID_PATH_PARAM + "}"
        val LOG: Logger = Logger.getGlobal()
        private val ENTITY_ID_GROUP = "entityIdGroup"
        private val ENTITY_ID_PATTERN = Pattern.compile(MessageFormat.format("/(?<{0}>[0-9a-f-]+)$", ENTITY_ID_GROUP))
    }
}