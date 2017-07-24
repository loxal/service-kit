/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.model.whoami

class Host(
        var name: String = "",
        var address: String = "",
        var headers: Headers = Headers()
)

class Headers(
        var accept: String = "",
        var `accept-encoding`: String = "",
        var `accept-language`: String = "",
        var `cache-control`: String = "",
        var connection: String = "",
        var cookie: String = "",
        var dnt: String = "",
        var host: String = "",
        var `upgrade-insecure-requests`: String = "",
        var `user-agent`: String = ""
)