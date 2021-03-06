package com.redhat.sie;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/cache")
public class CacheResource {
	
	@Inject
	CacheService cacheService;
    
    @GET
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCache(@QueryParam(value = "type") String type, @QueryParam(value = "name") String name) {
    	return Response.ok(cacheService.createCache(name, type)).build();
    }
    
    @GET
    @Path("/remove")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeCache(@QueryParam(value = "name") String name) {
    	return Response.ok(cacheService.removeCache(name)).build();
    }
    
    @GET
    @Path("/fill")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fillCache(@QueryParam(value = "entries") int entries, @QueryParam(value = "name") String name) {
        return Response.ok(cacheService.fillCache(entries, name)).build();
    }
    
    @GET
    @Path("/fillConcurrent")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fillConcurrentCache(@QueryParam(value = "entries") int entries, @QueryParam(value = "name") String name, @QueryParam(value = "threads") int threads) {
        return Response.ok(cacheService.fillCacheConcurrent(entries, name, threads)).build();
    }
    
    @GET
    @Path("/dump")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dumpCache(@QueryParam(value = "name") String name) {
        return Response.ok(cacheService.dumpCache(name)).build();
    }
    
    @GET
    @Path("/dumpConcurrent")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dumpConcurrentCache(@QueryParam(value = "name") String name) {
        return Response.ok(cacheService.dumpByCacheSegments(name)).build();
    }
    
    @GET
    @Path("/clear")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearCache(@QueryParam(value = "name") String name) {
        return Response.ok(cacheService.clearCache(name)).build();
    }
    
    
}