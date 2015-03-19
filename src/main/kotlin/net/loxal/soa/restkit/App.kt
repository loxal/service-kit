/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit

import org.glassfish.jersey.server.ResourceConfig
import javax.ws.rs.ApplicationPath

ApplicationPath("")
class App : ResourceConfig() {
    init {
        packages(javaClass.getPackage().getName())
    }
}