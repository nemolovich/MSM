/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import javax.ws.rs.Path;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;

/**
 *
 * @author COMPUMONSTER
 */
@Path("/")
public class Jersey implements AtmosphereHandler
{
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
