package com.glaway.base;

import com.dassault_systemes.platform.restServices.ModelerBase;
import com.glaway.RouteStartRestfulService;
import com.glaway.SanRestfulService;
import com.glaway.TestRestfulService;
import javax.ws.rs.ApplicationPath;

/**
 * @Auther : Dumpling
 * @Description
 **/
@ApplicationPath(ModelerBase.REST_BASE_PATH+"/dsic")
public class DSICRestfulBase extends ModelerBase {
    @Override
    public Class<?>[] getServices() {
        Class<?>[] clazz = {TestRestfulService.class, SanRestfulService.class, RouteStartRestfulService.class};
        return clazz;
    }
}
