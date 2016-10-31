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

    public static final String INBOUND_URI = "cxf://http://0.0.0.0:{{local.port}}";
    public static final String TWITTER_URI = "http4:{{env:TWITTER_HOST:localhost}}:{{twitter.port}}";

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {
        from(INBOUND_URI + "?serviceClass=" + WsTwitterService.class.getName())
                .marshal().xmljson() // convert xml to json
                .transform().jsonpath("$.query") // extract arg0 contents
                .removeHeaders("*")
                .setHeader(Exchange.HTTP_METHOD, new SimpleExpression("GET"))
                // Note, prefer HTTP_PATH + to(<uri>) instead of toD(<uri with path>) for easier unit testing
                .setHeader(Exchange.HTTP_PATH, new SimpleExpression("/twitter/${body}"))
                .to(TWITTER_URI)
                .removeHeader(Exchange.HTTP_PATH)
                .convertBodyTo(byte[].class) // load input stream to memory
                .setHeader(Exchange.HTTP_METHOD, new SimpleExpression("POST"))
                .setHeader("recipientList", new SimpleExpression("{{env:WS_RECIPIENT_LIST:http4://localhost:8882/filesystem/}}"))
                .recipientList().header("recipientList")
                .transform().constant(null);
    }
}
