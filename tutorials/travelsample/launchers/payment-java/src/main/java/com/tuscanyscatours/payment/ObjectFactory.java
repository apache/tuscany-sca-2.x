
package com.tuscanyscatours.payment;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.tuscanyscatours.payment package. 
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

    private final static QName _MakePaymentMember_QNAME = new QName("http://www.tuscanyscatours.com/Payment/", "makePaymentMember");
    private final static QName _MakePaymentMemberResponse_QNAME = new QName("http://www.tuscanyscatours.com/Payment/", "makePaymentMemberResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.tuscanyscatours.payment
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MakePaymentMemberResponseType }
     * 
     */
    public MakePaymentMemberResponseType createMakePaymentMemberResponseType() {
        return new MakePaymentMemberResponseType();
    }

    /**
     * Create an instance of {@link MakePaymentMemberType }
     * 
     */
    public MakePaymentMemberType createMakePaymentMemberType() {
        return new MakePaymentMemberType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MakePaymentMemberType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.tuscanyscatours.com/Payment/", name = "makePaymentMember")
    public JAXBElement<MakePaymentMemberType> createMakePaymentMember(MakePaymentMemberType value) {
        return new JAXBElement<MakePaymentMemberType>(_MakePaymentMember_QNAME, MakePaymentMemberType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MakePaymentMemberResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.tuscanyscatours.com/Payment/", name = "makePaymentMemberResponse")
    public JAXBElement<MakePaymentMemberResponseType> createMakePaymentMemberResponse(MakePaymentMemberResponseType value) {
        return new JAXBElement<MakePaymentMemberResponseType>(_MakePaymentMemberResponse_QNAME, MakePaymentMemberResponseType.class, null, value);
    }

}
