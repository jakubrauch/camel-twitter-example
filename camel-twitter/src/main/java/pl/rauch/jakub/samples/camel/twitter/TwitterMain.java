package pl.rauch.jakub.samples.camel.twitter;

import org.apache.camel.*;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.main.Main;
import org.apache.camel.support.TypeConverterSupport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A Camel Application
 */
public class TwitterMain extends Main {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
        Main main = new TwitterMain();
        main.addRouteBuilder(new TwitterRouteBuilder());
        main.run(args);
    }

    @Override
    protected CamelContext createContext() {
        CamelContext context = super.createContext();
        PropertiesComponent pc = new PropertiesComponent();
        pc.setLocation("classpath:twitter.properties");
        context.addComponent("properties", pc);

        return context;
    }
}

