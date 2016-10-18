package pl.rauch.jakub.samples.camel.ws;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.DataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.language.SimpleExpression;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.util.toolbox.FlexibleAggregationStrategy;
import twitter4j.Status;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A Camel Java DSL Router
 */
public class WsRouteBuilder extends RouteBuilder {

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {
        from("cxf://http://0.0.0.0:{{local.port}}?serviceClass=" + WsTwitterService.class.getName())
                .marshal().xmljson() // convert xml to json
                .transform().jsonpath("$.arg0") // extract arg0 contents
                .removeHeaders("*")
                .setHeader(Exchange.HTTP_METHOD, new SimpleExpression("GET"))
                .toD("http4://{{env:TWITTER_HOST}}:{{twitter.port}}/twitter/${body}")
                .convertBodyTo(byte[].class) // load input stream to memory
                .setHeader(Exchange.HTTP_METHOD, new SimpleExpression("POST"))
                .multicast().to(
                    "http4://{{env:FILESYSTEM1_HOST}}:{{filesystem1.port}}/filesystem/",
                    "http4://{{env:FILESYSTEM2_HOST}}:{{filesystem2.port}}/filesystem/")
                .end()
                .transform().constant(null);
    }
}
