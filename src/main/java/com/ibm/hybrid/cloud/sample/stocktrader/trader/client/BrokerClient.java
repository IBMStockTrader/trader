/*
       Copyright 2017-2021 IBM Corp All Rights Reserved
       Copyright 2022-2024 Kyndryl, All Rights Reserved

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

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;


@ApplicationPath("/")
@Path("/")
@RegisterRestClient
@RegisterClientHeaders //To enable JWT propagation
/** mpRestClient "remote" interface for the Broker microservice */
public interface BrokerClient {
//	@GET
//	@Path("/")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Broker[] getBrokers(@HeaderParam("Authorization") String jwt);

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@WithSpan(kind = SpanKind.CLIENT, value="BrokerClient.getBrokers")
	// NOTE: you need to include the JWT here because we're calling from a Servlet, not a JAX-RS resource
	// JWT is only propagate if a REST Service calls a REST Client.
	// See: https://download.eclipse.org/microprofile/microprofile-rest-client-1.4.1/microprofile-rest-client-1.4.1.html#_specifying_additional_client_headers
	// See: https://quarkus.io/guides/rest-client#default-header-factory

	public List<Broker> getBrokers(@HeaderParam("Authorization") String jwt, @QueryParam("page") @DefaultValue("1") int pageNumber, @QueryParam("pageSize") @DefaultValue("10") int pageSize);

	@POST
	@Path("/{owner}")
	@Produces(MediaType.APPLICATION_JSON)
	@WithSpan(kind = SpanKind.CLIENT, value="BrokerClient.createBroker")
	public Broker createBroker(@HeaderParam("Authorization") String jwt, @PathParam("owner") String owner, @QueryParam("balance") double balance, @QueryParam("currency") String currency);

	@GET
	@Path("/{owner}")
	@Produces(MediaType.APPLICATION_JSON)
	@WithSpan(kind = SpanKind.CLIENT, value="BrokerClient.getBroker")
	public Broker getBroker(@HeaderParam("Authorization") String jwt, @PathParam("owner") String owner);

	@PUT
	@Path("/{owner}")
	@Produces(MediaType.APPLICATION_JSON)
	@WithSpan(kind = SpanKind.CLIENT, value="BrokerClient.updateBroker")
	public Broker updateBroker(@HeaderParam("Authorization") String jwt, @PathParam("owner") String owner, @QueryParam("symbol") String symbol, @QueryParam("shares") int shares);

	@DELETE
	@Path("/{owner}")
	@Produces(MediaType.APPLICATION_JSON)
	@WithSpan(kind = SpanKind.CLIENT, value="BrokerClient.deleteBroker")
	public Broker deleteBroker(@HeaderParam("Authorization") String jwt, @PathParam("owner") String owner);

	@GET
	@Path("/{owner}/returns")
	@Produces(MediaType.TEXT_PLAIN)
	@WithSpan(kind = SpanKind.CLIENT, value="BrokerClient.getReturnOnInvestment")
	public String getReturnOnInvestment(@HeaderParam("Authorization") String jwt, @PathParam("owner") String owner);

	@GET
	@Path("/sentiment/{symbol}")
	@Produces(MediaType.APPLICATION_JSON)
	@WithSpan(kind = SpanKind.CLIENT, value="BrokerClient.getSentiment")
	public com.ibm.hybrid.cloud.sample.stocktrader.trader.json.Sentiment getSentiment(@HeaderParam("Authorization") String jwt, @PathParam("symbol") String symbol);

	@POST
	@Path("/{owner}/feedback")
	@Consumes("application/json")
	@Produces(MediaType.APPLICATION_JSON)
	@WithSpan(kind = SpanKind.CLIENT, value="BrokerClient.submitFeedback")
	public Feedback submitFeedback(@HeaderParam("Authorization") String jwt, @PathParam("owner") String owner, WatsonInput input);
}
