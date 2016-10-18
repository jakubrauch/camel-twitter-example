package pl.rauch.jakub.samples.camel.twitter;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;


public class TwitterRouteBuilderTest extends CamelTestSupport {
    // Route builder we are going to test
    @Override
    protected RoutesBuilder[] createRouteBuilders() throws Exception {
        return new RouteBuilder[]{
                new TwitterRouteBuilder()
        };
    }

    @Override
    public String isMockEndpointsAndSkip() {
        return "twitter://search.*";
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();
        PropertiesComponent pc = new PropertiesComponent();
        pc.setLocation("classpath:twitter.properties");
        context.addComponent("properties", pc);
        return context;
    }

    @Test
    public void testSearch() throws Exception {
        // Prepare
        MockEndpoint twitterSearch = getMockEndpoint("mock:twitter:search");
        twitterSearch.returnReplyBody(new Expression() {
            @Override
            public <T> T evaluate(Exchange exchange, Class<T> type) {
                try {
                    return loadTweets("tweet-1.json", "tweet-2.json");
                } catch (TwitterException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        twitterSearch.expectedHeaderReceived("keywords", "camel twitter plugin");

        // Invoke
        List<Status> result = (List<Status>) template.requestBodyAndHeader("direct:callTwitter", null, "keywords", "camel twitter plugin");

        // Verify
        assertThat(result.size(), equalTo(2));
        assertThat(result.get(0).getText(), startsWith("RT @juleshyman: Still trying to understand"));
        assertThat(result.get(1).getText(), startsWith("RT @antimickey_: #camel + hands & caress"));
        twitterSearch.assertIsSatisfied();
    }

    private <T> T loadTweets(String... files) throws TwitterException {
        List<Status> tweets = Arrays.asList(files).stream().map(file -> {
            try {
                String tweet = new Scanner(Thread.currentThread().getContextClassLoader().getResourceAsStream(file), "UTF-8").useDelimiter("\\A").next();
                return (Status) TwitterObjectFactory.createObject(tweet);
            } catch (TwitterException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        return (T) tweets;
    }
}
