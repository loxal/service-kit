/*
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.servicekit.model.whois

data class Host(
        var headers: Map<String, String> = mapOf(),
        var name: String = "",
        var address: String = ""
)
