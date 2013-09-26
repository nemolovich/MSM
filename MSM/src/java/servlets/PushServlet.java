/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.ws.rs.Path;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;

/**
 *
 * @author Brian GOHIER
 */
@AtmosphereHandlerService(path = "/", 
interceptors= {AtmosphereResourceLifecycleInterceptor.class})
@Path("/")
public class PushServlet implements AtmosphereHandler
{
    @PostConstruct
    public void init()
    {
        System.err.println("Get construct :)");
    }
    
    @Override
    public void onRequest(AtmosphereResource resource) throws IOException {
        System.err.println("Get request: "+resource);
    }

    @Override
    public void onStateChange(AtmosphereResourceEvent event) throws IOException {
        System.err.println("Get stateChange: "+event);
    }

    @Override
    public void destroy() {
        System.err.println("Get destroyed :(");
    }
    
}