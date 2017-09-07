/*
 * Copyright 2005-2016 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.atos;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.cdi.Uri;


/**
 * Configures all our Camel routes, components, endpoints and beans
 */
@ContextName("myJettyCamel")
public class MyJettyRoute extends RouteBuilder {

    @Inject @Uri("jetty:http://0.0.0.0:1080/camel/hello?matchOnUriPrefix=true")
    private Endpoint jettyEndpoint;

    @Override
    public void configure() throws Exception {
        // you can configure the route rule with Java DSL here
        //	getContext().setStreamCaching(true);

        from(jettyEndpoint)
        	.to("log:?level=INFO&showAll=true&showStreams=true")    		
        	//.log("***: ${in.body}")
        	.doTry()
            	.to("jetty:http://0.0.0.0:9999/ws/hello?bridgeEndpoint=true&eagerCheckContentAvailable=true&throwExceptionOnFailure=false")
            .doCatch(Exception.class)
            	.setHeader(Exchange.HTTP_RESPONSE_CODE)
            		.constant(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
            	.transform()
            		.simple("Exception")
            .endDoTry()
            .to("mock:result");
    }

}
