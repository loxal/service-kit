/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint

import javax.ws.rs.core.Response
import java.util.logging.Logger
import java.text.MessageFormat
import java.util.regex.Pattern

public abstract class Endpoint {

    protected fun extractIdOfLocation(createdVote: Response): String {
        val entityId = ENTITY_ID_PATTERN.matcher(createdVote.getLocation().getPath())

        val id: String
        if (entityId.find()) {
            id = entityId.group(ENTITY_ID_GROUP)
        } else {
            id = ""
        }
        return id
    }

    class object {
        public val URI_PATH_SEPARATOR: String = "/"
        protected val ASYNC_RESPONSE_TIMEOUT: Int = 3
        protected val ID_PATH_PARAM: String = "id"
        protected val ID_PATH_PARAM_PLACEHOLDER: String = "{" + ID_PATH_PARAM + "}"
        protected val LOG: Logger = Logger.getGlobal()
        private val ENTITY_ID_GROUP = "entityIdGroup"
        private val ENTITY_ID_PATTERN = Pattern.compile(MessageFormat.format("/(?<{0}>[0-9a-f]+)$", ENTITY_ID_GROUP))
    }
}