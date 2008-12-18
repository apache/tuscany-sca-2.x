
package testClient;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the test package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _InvokeTestResponseReturn_QNAME = new QName("", "return");
    private final static QName _InvokeTestArg0_QNAME = new QName("", "arg0");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: test
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link InvokeTestResponse }
     * 
     */
    public InvokeTestResponse createInvokeTestResponse() {
        return new InvokeTestResponse();
    }

    /**
     * Create an instance of {@link InvokeTest }
     * 
     */
    public InvokeTest createInvokeTest() {
        return new InvokeTest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "return", scope = InvokeTestResponse.class)
    public JAXBElement<String> createInvokeTestResponseReturn(String value) {
        return new JAXBElement<String>(_InvokeTestResponseReturn_QNAME, String.class, InvokeTestResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "arg0", scope = InvokeTest.class)
    public JAXBElement<String> createInvokeTestArg0(String value) {
        return new JAXBElement<String>(_InvokeTestArg0_QNAME, String.class, InvokeTest.class, value);
    }

}
