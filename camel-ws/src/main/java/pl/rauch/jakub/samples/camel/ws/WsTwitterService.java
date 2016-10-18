package pl.rauch.jakub.samples.camel.ws;

import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceProvider;

/**
 * Created by jakubrauch on 10.10.2016.
 */
@WebServiceProvider(
        wsdlLocation = "TwitterService.wsdl",
        serviceName = "TwitterServiceService",
        portName = "TwitterServicePort",
        targetNamespace = "http://samples.jakub.rauch.pl/")
public interface WsTwitterService extends Provider<SOAPMessage> {
    @Override
    SOAPMessage invoke(SOAPMessage request);
}
