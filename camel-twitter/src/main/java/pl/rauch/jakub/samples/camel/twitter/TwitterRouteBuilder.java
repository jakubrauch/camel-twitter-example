package pl.rauch.jakub.samples.camel.twitter;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.DataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.util.toolbox.FlexibleAggregationStrategy;
import org.restlet.resource.Result;
import twitter4j.Status;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A Camel Java DSL Router
 */
public class TwitterRouteBuilder extends RouteBuilder {

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {
        restConfiguration().component("restlet").port("{{local.port}}").bindingMode(RestBindingMode.json);
        rest("/twitter/")
                .get("/{keywords}").produces("application/json")
                    .to("direct:callTwitter");

        from("direct:callTwitter")
                .toD("twitter://search" +
                        "?keywords=${header.keywords}" +
                        "&consumerKey={{twitter.consumerKey}}" +
                        "&consumerSecret={{twitter.consumerSecret}}" +
                        "&accessToken={{twitter.accessToken}}" +
                        "&accessTokenSecret={{twitter.accessTokenSecret}}");
    }
}
