package pl.rauch.jakub.samples.camel.ws;

import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.main.Main;

/**
 * A Camel Application
 */
public class WsMain extends Main {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
        Main main = new WsMain();
        main.addRouteBuilder(new WsRouteBuilder());
        main.run(args);
    }

    @Override
    protected CamelContext createContext() {
        CamelContext context = super.createContext();
        PropertiesComponent pc = new PropertiesComponent();
        pc.setLocation("classpath:ws.properties");
        context.addComponent("properties", pc);
        return context;
    }
}

