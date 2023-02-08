/*
       Copyright 2017-2021 IBM Corp All Rights Reserved

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

package com.ibm.hybrid.cloud.sample.stocktrader.trader.client;

import com.ibm.hybrid.cloud.sample.stocktrader.trader.json.Feedback;
import com.ibm.hybrid.cloud.sample.stocktrader.trader.json.Broker;
import com.ibm.hybrid.cloud.sample.stocktrader.trader.json.WatsonInput;

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
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


@ApplicationPath("/")
@Path("/")
@RegisterRestClient
/** mpRestClient "remote" interface for the Broker microservice */
public interface BrokerClient {
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Broker[] getBrokers(@HeaderParam("Authorization") String jwt);

	@POST
	@Path("/{owner}")
	@Produces(MediaType.APPLICATION_JSON)
	public Broker createBroker(@HeaderParam("Authorization") String jwt, @PathParam("owner") String owner, @QueryParam("balance") double balance, @QueryParam("currency") String currency);

	@GET
	@Path("/{owner}")
	@Produces(MediaType.APPLICATION_JSON)
	public Broker getBroker(@HeaderParam("Authorization") String jwt, @PathParam("owner") String owner);

	@PUT
	@Path("/{owner}")
	@Produces(MediaType.APPLICATION_JSON)
	public Broker updateBroker(@HeaderParam("Authorization") String jwt, @PathParam("owner") String owner, @QueryParam("symbol") String symbol, @QueryParam("shares") int shares);

	@DELETE
	@Path("/{owner}")
	@Produces(MediaType.APPLICATION_JSON)
	public Broker deleteBroker(@HeaderParam("Authorization") String jwt, @PathParam("owner") String owner);

	@GET
	@Path("/{owner}/returns")
	@Produces(MediaType.TEXT_PLAIN)
	public String getReturnOnInvestment(@HeaderParam("Authorization") String jwt, @PathParam("owner") String owner);

	@POST
	@Path("/{owner}/feedback")
	@Consumes("application/json")
	@Produces(MediaType.APPLICATION_JSON)
	public Feedback submitFeedback(@HeaderParam("Authorization") String jwt, @PathParam("owner") String owner, WatsonInput input);
}
