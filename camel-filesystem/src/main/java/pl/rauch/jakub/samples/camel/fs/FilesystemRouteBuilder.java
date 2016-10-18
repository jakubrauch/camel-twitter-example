package pl.rauch.jakub.samples.camel.fs;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

/**
 * A Camel Java DSL Router
 */
public class FilesystemRouteBuilder extends RouteBuilder {

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {
        restConfiguration().component("restlet").port("{{local.port}}").bindingMode(RestBindingMode.off);
        rest("/")
                .post("/filesystem")
                    .to("direct:store");

        from("direct:store")
                .to("file:target/fs");
    }
}
