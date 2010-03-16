
package com.tuscanyscatours.emailgateway;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.tuscanyscatours.emailgateway package. 
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

    private final static QName _SendEmailResponse_QNAME = new QName("http://www.tuscanyscatours.com/EmailGateway/", "sendEmailResponse");
    private final static QName _SendEmail_QNAME = new QName("http://www.tuscanyscatours.com/EmailGateway/", "sendEmail");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.tuscanyscatours.emailgateway
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SendEmailType }
     * 
     */
    public SendEmailType createSendEmailType() {
        return new SendEmailType();
    }

    /**
     * Create an instance of {@link EmailType }
     * 
     */
    public EmailType createEmailType() {
        return new EmailType();
    }

    /**
     * Create an instance of {@link SendEmailResponseType }
     * 
     */
    public SendEmailResponseType createSendEmailResponseType() {
        return new SendEmailResponseType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendEmailResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.tuscanyscatours.com/EmailGateway/", name = "sendEmailResponse")
    public JAXBElement<SendEmailResponseType> createSendEmailResponse(SendEmailResponseType value) {
        return new JAXBElement<SendEmailResponseType>(_SendEmailResponse_QNAME, SendEmailResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendEmailType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.tuscanyscatours.com/EmailGateway/", name = "sendEmail")
    public JAXBElement<SendEmailType> createSendEmail(SendEmailType value) {
        return new JAXBElement<SendEmailType>(_SendEmail_QNAME, SendEmailType.class, null, value);
    }

}
