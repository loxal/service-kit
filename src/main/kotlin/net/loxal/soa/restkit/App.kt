/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit

import javax.ws.rs.ApplicationPath
import org.glassfish.jersey.server.ResourceConfig

ApplicationPath("")
class App : ResourceConfig() {
    {
        packages(javaClass.getPackage().getName())
    }
}