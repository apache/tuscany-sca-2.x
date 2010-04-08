/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca;

/**
 * The SCA Constants interface defines a number of constant values
 * that are used in the SCA Java APIs and Annotations.
 * 
 * <p> The serialized QNames are used with the @Requires annotation 
 * to specify a policy intent. The policy intent strings in this
 * interface do not have a corresponding Java annotation, so these
 * policy intents have ot be specified through the use of the
 * @Requires annotation.
 */
public interface Constants {

	/**
	 * The SCA V1.1 namespace.
	 */
    String SCA_NS = "http://docs.oasis-open.org/ns/opencsa/sca/200912";

	/**
	 * The serialized form of the SCA namespace for construction of QNames.
	 */
    String SCA_PREFIX = "{"+SCA_NS+"}";
    
	/**
	 * The serialized QName of the serverAuthentication policy intent.
	 */
    String SERVERAUTHENTICATION = SCA_PREFIX + "serverAuthentication";
	/**
	 * The serialized QName of the clientAuthentication policy intent.
	 */
    String CLIENTAUTHENTICATION = SCA_PREFIX + "clientAuthentication";
	/**
	 * The serialized QName of the atleastOnce policy intent.
	 */
    String ATLEASTONCE = SCA_PREFIX + "atLeastOnce";
	/**
	 * The serialized QName of the atMostOnce policy intent.
	 */
    String ATMOSTONCE = SCA_PREFIX + "atMostOnce";
	/**
	 * The serialized QName of the exactlyOnce policy intent.
	 */
    String EXACTLYONCE = SCA_PREFIX + "exactlyOnce";
	/**
	 * The serialized QName of the ordered policy intent.
	 */
    String ORDERED = SCA_PREFIX + "ordered";
	/**
	 * The serialized QName of the transactedOneWay policy intent.
	 */
    String TRANSACTEDONEWAY = SCA_PREFIX + "transactedOneWay";
	/**
	 * The serialized QName of the immediateOneWay policy intent.
	 */
    String IMMEDIATEONEWAY = SCA_PREFIX + "immediateOneWay";
	/**
	 * The serialized QName of the propagatesTransaction policy intent.
	 */
    String PROPAGATESTRANSACTION = SCA_PREFIX + "propagatesTransaction";
	/**
	 * The serialized QName of the suspendsTransaction policy intent.
	 */
    String SUSPENDSTRANSACTION = SCA_PREFIX + "suspendsTransaction";
	/**
	 * The serialized QName of the asyncInvocation policy intent.
	 */
    String ASYNCINVOCATION = SCA_PREFIX + "asyncInvocation";
	/**
	 * The serialized QName of the SOAP policy intent.
	 */
    String SOAP = SCA_PREFIX + "SOAP";
	/**
	 * The serialized QName of the JMS policy intent.
	 */
    String JMS = SCA_PREFIX + "JMS";
	/**
	 * The serialized QName of the noListener policy intent.
	 */
    String NOLISTENER = SCA_PREFIX + "noListener";
	/**
	 * The serialized QName of the EJB policy intent.
	 */
    String EJB = SCA_PREFIX + "EJB";
    
}
