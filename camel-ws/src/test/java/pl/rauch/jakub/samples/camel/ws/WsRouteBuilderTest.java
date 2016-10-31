package pl.rauch.jakub.samples.camel.ws;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.cxf.CxfPayload;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.model.language.SimpleExpression;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

/**
 * Class for testing WsRouteBuilder
 */
public class WsRouteBuilderTest extends CamelTestSupport {
    private static final Logger LOG = Logger.getLogger(WsRouteBuilderTest.class);

    /** Specify routes to be used in this test. */
    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new WsRouteBuilder();
    }

    /** Set up properties component. */
    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();
        PropertiesComponent pc = new PropertiesComponent();
        pc.setLocation("classpath:ws.properties");
        context.addComponent("properties", pc);
        return context;
    }

    /** List endpoints that should be replaced with mocks. */
    @Override
    public String isMockEndpointsAndSkip() {
        return "http4://localhost:*";
    }

    /** Tell Camel that we will start the context manually in each test. */
    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    /** Utility for replacing env variables in unit tests. */
    @Rule
    public final EnvironmentVariables environmentVariables
            = new EnvironmentVariables();

    @Before
    public void listEndpoints() {
        StringBuilder sb = new StringBuilder("List of endpoints:");
        context.getEndpoints().forEach(endpoint -> sb.append("\n" + endpoint.getEndpointUri() + " -> " + endpoint));
        LOG.info(sb);
    }

    @Test
    public void shouldHaveRoutingSlipWhenDefinedByMockEnvVars() {
        environmentVariables.set("TEST_ENV_VAR", "val");
        assertEquals("val", System.getenv("TEST_ENV_VAR"));
    }

    @Test
    public void shouldSendTo2EndpointsWhen2RoutingSlipsTargetsProvided() throws Exception {
        try {
            // given
            environmentVariables.set("WS_RECIPIENT_LIST", "mock:target,mock:target");
            context.start();
            MockEndpoint twitterMock = resolveMockEndpoint(WsRouteBuilder.TWITTER_URI, false);
            twitterMock.whenAnyExchangeReceived(exchange -> new SimpleExpression("test"));
            MockEndpoint targetMock = getMockEndpoint("mock:target");
            // Note: Short messages - place inline for clarity. Large messages - place in a file.
            CxfPayload request = context.getTypeConverter().convertTo(CxfPayload.class,
                    "<findTweets xmlns=\"http://samples.jakub.rauch.pl/\"><query>query text</query></findTweets>");

            // when
            template.sendBodyAndHeader(WsRouteBuilder.INBOUND_URI +
                            "?serviceClass=" + WsTwitterService.class.getCanonicalName() +
                            "&dataFormat=PAYLOAD", request, CxfConstants.OPERATION_NAME, "findTweets");

            // then
            twitterMock.expectedMessageCount(1);
            twitterMock.assertIsSatisfied();
            targetMock.expectedMessageCount(2);
            targetMock.assertIsSatisfied();
        } finally {
            // cleanup
            System.clearProperty("WS_ROUTING_SLIP");
            context.stop();
        }
    }

    private MockEndpoint resolveMockEndpoint(String uri, boolean create) throws Exception {
        String uriResolved = context.resolvePropertyPlaceholders("mock://" + uri);
        return getMockEndpoint(uriResolved, create);
    }
}
