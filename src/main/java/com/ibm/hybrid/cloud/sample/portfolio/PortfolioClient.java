/*
       Copyright 2017 IBM Corp All Rights Reserved

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

package com.ibm.hybrid.cloud.sample.portfolio;


import javax.enterprise.context.Dependent;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@ApplicationPath("/")
@Path("/")
@Dependent
@RegisterRestClient
/** mpRestClient "remote" interface for the Portfolio microservice */
public interface PortfolioClient {
	@GET
	@Path("/")
	@Produces("application/json")
	public JsonArray getPortfolios(@HeaderParam("Authorization") String jwt);

	@POST
	@Path("/{owner}")
	@Produces("application/json")
	public JsonObject createPortfolio(@HeaderParam("Authorization") String jwt, @PathParam("owner") String owner);

	@GET
	@Path("/{owner}")
	@Produces("application/json")
	public JsonObject getPortfolio(@HeaderParam("Authorization") String jwt, @PathParam("owner") String owner);

	@PUT
	@Path("/{owner}")
	@Produces("application/json")
	public JsonObject updatePortfolio(@HeaderParam("Authorization") String jwt, @PathParam("owner") String owner, @QueryParam("symbol") String symbol, @QueryParam("shares") int shares);

	@DELETE
	@Path("/{owner}")
	@Produces("application/json")
	public JsonObject deletePortfolio(@HeaderParam("Authorization") String jwt, @PathParam("owner") String owner);

	@POST
	@Path("/{owner}/feedback")
	@Consumes("application/json")
	@Produces("application/json")
	public JsonObject submitFeedback(@HeaderParam("Authorization") String jwt, @PathParam("owner") String owner, JsonObject input);
}
