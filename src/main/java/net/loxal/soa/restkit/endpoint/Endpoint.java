/*
 * Copyright 2014 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.endpoint;

import javax.ws.rs.core.Response;
import java.text.MessageFormat;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Endpoint {
    public static final String URI_PATH_SEPARATOR = "/";
    protected static final int ASYNC_RESPONSE_TIMEOUT = 3;
    protected static final String ID_PATH_PARAM = "id";
    protected static final String ID_PATH_PARAM_PLACEHOLDER = "{" + ID_PATH_PARAM + "}";
    protected static final Logger LOG = Logger.getGlobal();
    private static final String ENTITY_ID_GROUP = "entityIdGroup";
    private static final Pattern ENTITY_ID_PATTERN = Pattern.compile(MessageFormat.format("/(?<{0}>[0-9a-f]+)$", ENTITY_ID_GROUP));

    protected String extractIdOfLocation(Response createdVote) {
        Matcher entityId = ENTITY_ID_PATTERN.matcher(createdVote.getLocation().getPath());

        final String id;
        if (entityId.find()) {
            id = entityId.group(ENTITY_ID_GROUP);
        } else {
            id = "";
        }
        return id;
    }
}
