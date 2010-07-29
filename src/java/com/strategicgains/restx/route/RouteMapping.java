/*
    Copyright 2010, Strategic Gains, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package com.strategicgains.restx.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpMethod;

/**
 * Contains the routes for a given service implementation.  Sub-classes will implement the initialize() method which
 * calls map() to specify how URL request will be routed to the underlying controllers. 
 * 
 * @author toddf
 * @since May 21, 2010
 */
public abstract class RouteMapping
{
	// SECTION: INSTANCE VARIABLES

	Map<HttpMethod, List<Route>> routes;
	private List<Route> deleteRoutes = new ArrayList<Route>();
	private List<Route> getRoutes = new ArrayList<Route>();
	private List<Route> postRoutes = new ArrayList<Route>();
	private List<Route> putRoutes = new ArrayList<Route>();
	
	private List<RouteBuilder> routeBuilders = new ArrayList<RouteBuilder>();


	// SECTION: CONSTRUCTOR

	public RouteMapping()
	{
		super();
		routes = new HashMap<HttpMethod, List<Route>>();
		routes.put(HttpMethod.DELETE, deleteRoutes);
		routes.put(HttpMethod.GET, getRoutes);
		routes.put(HttpMethod.POST, postRoutes);
		routes.put(HttpMethod.PUT, putRoutes);
		initialize();
		buildRoutes();
	}

    protected abstract void initialize();
    
    private void buildRoutes()
    {
    	for (RouteBuilder builder : routeBuilders)
    	{
    		for (Route route : builder.createRoutes())
    		{
    			addRoute(route);
    		}
    	}
    	
    	// Garbage collect the builders and blow chow if buildRoutes() gets called a second time.
    	routeBuilders.clear();
    	routeBuilders = null;
    }


	// SECTION: URL MAPPING
	
    /**
     * Map a URL pattern to a controller.
     * 
     * @param urlPattern a string specifying a URL pattern to match.
     * @param controller a pojo which contains implementations of create(), read(), update(), delete() methods.
     */
	public RouteBuilder uri(String uri, Object controller)
	{
		RouteBuilder builder = new RouteBuilder(uri, controller);
		routeBuilders.add(builder);
		return builder;
	}
	
	
	// SECTION: UTILITY - PUBLIC

	/**
	 * Return a list of Route instances for the given HTTP method.  The returned list is immutable.
	 * 
	 * @param method the HTTP method (GET, PUT, POST, DELETE) for which to retrieve the routes.
	 */
	public List<Route> getRoutesFor(HttpMethod method)
	{
		return Collections.unmodifiableList(routes.get(method));
	}


	// SECTION: UTILITY - PRIVATE

	/**
	 * @param method
	 * @param route
	 */
	private void addRoute(Route route)
	{
		routes.get(route.getMethod()).add(route);
		// TODO: call log4j for added route, method
	}
}