/*
 * Copyright 2016 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.model.dilbert

data class Quote(
        var id: Int = 0,
        var quote: String = "You must have done something wrong."
)

data class Dict(
        var id: Int = 0,
        var plain: String = "",
        var euphemistic: String = ""
)