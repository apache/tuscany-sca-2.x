/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tuscany.sca.core.runtime.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.builder.PolicyBuilder;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.runtime.EndpointReferenceBinder;
import org.apache.tuscany.sca.runtime.EndpointRegistry;

/**
 * A builder that takes endpoint references and resolves them. It either finds local
 * service endpoints if they are available or asks the domain. The main function here
 * is to perform binding and policy matching.
 * 
 * This is a separate from the builders so that the mechanism for reference/service matching 
 * can be used at runtime as well as build time and can also be replaced independently
 *
 * @version $Rev$ $Date$
 */
public class EndpointReferenceBinderImpl implements EndpointReferenceBinder {
    private static final Logger logger = Logger.getLogger(EndpointReferenceBinderImpl.class.getName());

    protected ExtensionPointRegistry extensionPoints;
    protected AssemblyFactory assemblyFactory;
    protected InterfaceContractMapper interfaceContractMapper;
    protected BuilderExtensionPoint builders;
    private Monitor monitor;


    public EndpointReferenceBinderImpl(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;

        FactoryExtensionPoint factories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.assemblyFactory = factories.getFactory(AssemblyFactory.class);

        UtilityExtensionPoint utils = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        this.interfaceContractMapper = utils.getUtility(InterfaceContractMapper.class);
        
        MonitorFactory monitorFactory = utils.getUtility(MonitorFactory.class);
        monitor = monitorFactory.createMonitor();
        
        this.builders = extensionPoints.getExtensionPoint(BuilderExtensionPoint.class);
    }
    
    /**
     * Bind a single endpoint reference at build time. Here we only expect the
     * registry to have a record of local endpoints
     *
     * @param endpointRegistry
     * @param endpointReference
     */
    public boolean bindBuildTime(EndpointRegistry endpointRegistry, 
                                 EndpointReference endpointReference) {
       return bind(endpointRegistry, endpointReference, false);
    }
    
    /**
     * Bind a single endpoint reference at build time. Here we expect the
     * registry to be populated with endpoints from across the domain
     *
     * @param endpointRegistry
     * @param endpointReference
     */
    public boolean bindRunTime(EndpointRegistry endpointRegistry,
                               EndpointReference endpointReference) {
        return bind(endpointRegistry, endpointReference, true);
    }
    
    /**
     * Bind a reference to a service endpoint
     * 
     * @param endpointRegistry
     * @param endpointReference
     * @param runtime set true if called from the runtime 
     */
    public boolean bind(EndpointRegistry endpointRegistry,  
                        EndpointReference endpointReference,
                        boolean runtime){
        
        logger.fine("Binding " + endpointReference.toString());
        
        Problem problem = null;
             
        // This logic does post build autowire matching but isn't actually used at the moment
        // as problems with dependencies mean we still do this during build
        if (endpointReference.getStatus() == EndpointReference.AUTOWIRE_PLACEHOLDER){ 
           
            // do autowire matching
            // will only be called at build time at the moment
            Multiplicity multiplicity = endpointReference.getReference().getMultiplicity();
            for (Endpoint endpoint : endpointRegistry.getEndpoints()){
//              if (endpoint is in the same composite as endpoint reference){
                    if ((multiplicity == Multiplicity.ZERO_ONE || 
                         multiplicity == Multiplicity.ONE_ONE) && 
                        (endpointReference.getReference().getEndpointReferences().size() > 1)) {
                        break;
                    }

                    // Prevent autowire connecting to self
                    if (endpointReference.getComponent() == 
                        endpoint.getComponent()) {
                        continue;
                    }
                    
                    if (haveMatchingPolicy(endpointReference, endpoint) &&
                        haveMatchingInterfaceContracts(endpointReference, endpoint)){
                        // matching service so find if this reference already has 
                        // an endpoint reference for this endpoint
                        Endpoint autowireEndpoint = null;
                        
                        for (EndpointReference epr : endpointReference.getReference().getEndpointReferences()){
                            if (epr.getTargetEndpoint() == endpoint){
                                autowireEndpoint = endpoint;
                                break;
                            }
                        }
                        
                        if (autowireEndpoint == null){
                            // create new EPR for autowire
                            EndpointReference autowireEndpointRefrence = null;
                            try {
                                autowireEndpointRefrence = (EndpointReference)endpointReference.clone();
                            } catch (Exception ex){
                                // won't happen as clone is supported
                            }
                            
                            autowireEndpointRefrence.setTargetEndpoint(endpoint);
                            autowireEndpointRefrence.setBinding(endpoint.getBinding());
                            autowireEndpointRefrence.setStatus(EndpointReference.WIRED_TARGET_FOUND_AND_MATCHED);
                            endpointReference.getReference().getEndpointReferences().add(autowireEndpointRefrence);  
                        }
                    }
//              }
            }
            
            if (multiplicity == Multiplicity.ONE_N || multiplicity == Multiplicity.ONE_ONE) {
                if (endpointReference.getReference().getEndpointReferences().size() == 1) {
                    Monitor.error(monitor,
                                  this,
                                  "endpoint-validation-messages",
                                  "NoComponentReferenceTarget",
                                  endpointReference.getReference().getName());
                }
            }
            
            setSingleAutoWireTarget(endpointReference.getReference());
            
        } else if ( endpointReference.getStatus() == EndpointReference.WIRED_TARGET_FOUND_AND_MATCHED||
                    endpointReference.getStatus() == EndpointReference.RESOLVED_BINDING ) {
            // The endpoint reference is already resolved to either
            // a service endpoint local to this composite or it has
            // a remote binding
            
            // still need to check that the callback endpoint is set correctly
            if (hasCallback(endpointReference) &&
                endpointReference.getCallbackEndpoint() != null &&
                endpointReference.getCallbackEndpoint().isUnresolved() == true ){
                problem = selectCallbackEndpoint(endpointReference,
                                                 endpointReference.getReference().getCallbackService().getEndpoints());
            } 
        } else if (endpointReference.getStatus() == EndpointReference.WIRED_TARGET_FOUND_READY_FOR_MATCHING ){
            // The endpoint reference is already resolved to either
            // a service endpoint but no binding was specified in the 
            // target URL and/or the policies have yet to be matched.
            // TODO - is this really required now
            
            problem = selectForwardEndpoint(endpointReference,
                                            endpointReference.getTargetEndpoint().getService().getEndpoints());

            if (problem == null && hasCallback(endpointReference)){
                problem = selectCallbackEndpoint(endpointReference,
                                                 endpointReference.getReference().getCallbackService().getEndpoints());
            }             
        } else if (endpointReference.getStatus() == EndpointReference.WIRED_TARGET_IN_BINDING_URI ||
                   endpointReference.getStatus() == EndpointReference.WIRED_TARGET_NOT_FOUND ||
                   endpointReference.getStatus() == EndpointReference.NOT_CONFIGURED){
            // The reference is not yet matched to a service
          
            // find the service in the endpoint registry
            List<Endpoint> endpoints = endpointRegistry.findEndpoint(endpointReference);
            
            if ((endpoints.size() == 0) && 
                (runtime == true)     ) {
                
                // tweak to test if this could be a resolve binding. This is the back end of the test
                // in the builder that pulls the URI out of the binding if there are no targets
                // on the reference. have to wait until here to see if the binding uri matches any
                // available services. If not we assume here that it's a resolved binding
                if (endpointReference.getStatus() == EndpointReference.WIRED_TARGET_IN_BINDING_URI){
                    endpointReference.getTargetEndpoint().setBinding(endpointReference.getBinding());
                    endpointReference.setRemote(true);
                    endpointReference.setStatus(EndpointReference.RESOLVED_BINDING);
                } else {
                    problem = monitor.createProblem(this.getClass().getName(), 
                                                    "endpoint-validation-messages", 
                                                    Problem.Severity.ERROR, 
                                                    this, 
                                                    "NoEndpointsFound", 
                                                    endpointReference.toString());
                }
            }            

            problem = selectForwardEndpoint(endpointReference,
                                            endpoints);

            if (problem == null && hasCallback(endpointReference)){
                problem = selectCallbackEndpoint(endpointReference,
                                                 endpointReference.getReference().getCallbackService().getEndpoints());
            }             
        } 
        
        if (problem != null){
            monitor.problem(problem);
            return false;
        }

        if (endpointReference.getStatus() != EndpointReference.WIRED_TARGET_FOUND_AND_MATCHED &&
            endpointReference.getStatus() != EndpointReference.RESOLVED_BINDING){
            
            if (runtime){
                problem = monitor.createProblem(this.getClass().getName(), 
                                                "endpoint-validation-messages", 
                                                Problem.Severity.ERROR, 
                                                this, 
                                                "EndpointReferenceCantBeMatched", 
                                                endpointReference.toString());
            } else {
                problem = monitor.createProblem(this.getClass().getName(), 
                                                "endpoint-validation-messages", 
                                                Problem.Severity.WARNING, 
                                                this, 
                                                "ComponentReferenceTargetNotFound",
                                                "NEED COMPOSITE NAME",
                                                endpointReference.toString());
            }
            
            monitor.problem(problem);
            return false;
        }
       
        return true;
    }    
   
    /**
     * Returns true if the reference has a callback
     */
    private boolean hasCallback(EndpointReference endpointReference){
        if (endpointReference.getReference().getInterfaceContract() == null ||
            endpointReference.getReference().getInterfaceContract().getCallbackInterface() == null ||
            endpointReference.getReference().getName().startsWith("$self$.")){
            return false;
        } else {
            return true;
        }
    }

    /**
     * Selects a forward endpoint from a list of possible candidates
     * 
     * @param endpointReference
     * @param endpoints
     */
    private Problem selectForwardEndpoint(EndpointReference endpointReference, List<Endpoint> endpoints) {    
             
        Endpoint matchedEndpoint = null;
        
        if (endpointReference.getReference().getName().startsWith("$self$.")){
            // just select the first one and don't do any policy matching
            matchedEndpoint = endpoints.get(0);
        } else {
            // find the first endpoint that matches this endpoint reference
            for (Endpoint endpoint : endpoints){
                if (haveMatchingPolicy(endpointReference, endpoint) &&
                    haveMatchingInterfaceContracts(endpointReference, endpoint)){
                    matchedEndpoint = endpoint;
                    break;
                }
            }
        }
        
        if (matchedEndpoint == null){
            return null;
        }
        
        endpointReference.setTargetEndpoint(matchedEndpoint);
        endpointReference.setBinding(endpointReference.getTargetEndpoint().getBinding());
        endpointReference.setStatus(EndpointReference.WIRED_TARGET_FOUND_AND_MATCHED);
        endpointReference.setUnresolved(false);
        
        return null;
    }

    /**
     * Selects a callback endpoint from a list of possible candidates
     * 
     * @param endpointReference
     * @param endpoints
     */
    private Problem selectCallbackEndpoint(EndpointReference endpointReference, List<Endpoint> endpoints) {

        Problem problem = null;
        
        // find the first callback endpoint that matches a callback endpoint reference
        // at the service
        Endpoint matchedEndpoint = null;
        match:
        for ( EndpointReference callbackEndpointReference : endpointReference.getTargetEndpoint().getCallbackEndpointReferences()){
            for (Endpoint endpoint : endpoints){
                if (haveMatchingPolicy(callbackEndpointReference, endpoint) &&
                    haveMatchingInterfaceContracts(callbackEndpointReference, endpoint)){
                    matchedEndpoint = endpoint;
                    break match;
                }
            }
        }
        
        if (matchedEndpoint == null){
            return null;
        }
        
        endpointReference.setCallbackEndpoint(matchedEndpoint);
        
        return problem;
    }

    /**
     * Determine if endpoint reference and endpoint policies match. We know by this stage
     * that 
     *   - a given policy set will only contain expressions from a single language
     *   - a given endpoint or endpoint reference's policy sets will only contain
     *     expressions from a single language
     *     
     * TODO - narative of matching algorithm
     */
    private boolean haveMatchingPolicy(EndpointReference endpointReference, Endpoint endpoint){
        logger.fine("Match policy of " + endpointReference.toString() + " to " + endpoint.toString());
        
        List<PolicySet> referencePolicySets = new ArrayList<PolicySet>();
        Binding binding = null;
        
        if (endpointReference.getBinding() == null){
            binding = endpoint.getBinding();
        } else {
            binding = endpointReference.getBinding();
        }
        
        // if there are any intents that are mutually exclusive between 
        // service and reference then they don't match
        for (Intent eprIntent : endpointReference.getRequiredIntents()){
            for (Intent epIntent : endpoint.getRequiredIntents()){ 
                if (eprIntent.getExcludedIntents().contains(epIntent) ||
                    epIntent.getExcludedIntents().contains(eprIntent)){
                    logger.fine("No match because the following intents are mutually exclusive " + 
                                eprIntent.toString() +
                                " " +
                                epIntent.toString());
                    return false;
                }
            }
        }
        
        // Find the set of policy sets from this reference. This includes 
        // the policy sets that are specific to the service binding and 
        // any policy sets that are not binding specific    
        for (PolicySet policySet : endpointReference.getPolicySets()){
                PolicyBuilder policyBuilder = null;
                
                if (policySet.getPolicies().size() > 0){
                    QName policyType = policySet.getPolicies().get(0).getName();
                    policyBuilder = builders.getPolicyBuilder(policyType);
                }
                
                if ((policyBuilder == null) ||
                    (policyBuilder != null && policyBuilder.getSupportedBindings() == null) ||
                    (policyBuilder != null && policyBuilder.getSupportedBindings().contains(binding.getType()))){
                    referencePolicySets.add(policySet);
                }
        }
        
        // run the "appliesTo" algorithm to remove any policy sets that 
        // don't apply to the service binding
        // TODO - is this done somewhere else already?
        
        // Determine of there are any reference policies
        boolean noEndpointReferencePolicies = true;
        
        for (PolicySet policySet : referencePolicySets){
            if (policySet.getPolicies().size() > 0){
                noEndpointReferencePolicies = false;
                break;
            }
        }
        
        // Determine of there are any reference policies
        boolean noEndpointPolicies = true;
        
        for (PolicySet policySet : endpoint.getPolicySets()){
            if (policySet.getPolicies().size() > 0){
                noEndpointPolicies = false;
                break;
            }
        }        
        
        // if no policy sets or intents are present then they match
        if ((endpointReference.getRequiredIntents().size() == 0) &&
            (endpoint.getRequiredIntents().size() == 0) &&
            (noEndpointReferencePolicies) &&
            (noEndpointPolicies)) {
            logger.fine("Match because there are no intents or policy sets");
            return true;
        }        
        
        // check that the intents on the reference side are resolved 
        // can't do this until this point as the service binding
        // may come into play. Intents may be satisfied by the default
        // or optional intents that the binding type provides. Failing
        // this they must be satisfied by reference policy sets
        // Failing this the intent is unresolved and the reference and 
        // service don't match
        List<Intent> eprIntents = new ArrayList<Intent>();
        eprIntents.addAll(endpointReference.getRequiredIntents());
        
        // first check the binding type
        for (Intent intent : endpointReference.getRequiredIntents()){
/* TODO 
            BindingType bindingType = null; //TODO - where to get this?
            
            if (bindingType.getAlwaysProvidedIntents().contains(intent)){
                eprIntents.remove(intent);
            } else if (bindingType.getMayProvidedIntents().contains(intent)){
                eprIntents.remove(intent);
            } else {
            
*/  
                for (PolicySet policySet : referencePolicySets){
                    if (policySet.getProvidedIntents().contains(intent)){
                        eprIntents.remove(intent);
                        break;
                    }
                }
/*          
            }                
 */
        }
        
        // if there are unresolved intents the service and reference don't match
        if (eprIntents.size() > 0){
            logger.fine("No match because there are unresolved intents " + eprIntents.toString());
            return false;
        }   
        
        // if there are no policy sets on epr or ep side then 
        // they match
        if (noEndpointPolicies && noEndpointReferencePolicies){
            logger.fine("Match because the intents are resolved and there are no policy sets");
            return true;
        }
        
        // if there are some policies on one side and not the other then 
        // the don't match
        if (noEndpointPolicies && !noEndpointReferencePolicies) {
            logger.fine("No match because there are policy sets at the endpoint reference but not at the endpoint");
            return false;
        }
        
        if (!noEndpointPolicies && noEndpointReferencePolicies){
            logger.fine("No match because there are policy sets at the endpoint but not at the endpoint reference");
            return false;
        }
        
        // If policy set QNames from epr and er match exactly then the reference and 
        // service policies are compatible
        Set<PolicySet> referencePolicySet = new HashSet<PolicySet>(referencePolicySets);
        Set<PolicySet> servicePolicySet = new HashSet<PolicySet>(endpoint.getPolicySets());
        if(referencePolicySet.equals(servicePolicySet)){
            logger.fine("Match because the policy sets on both sides are eactly the same");
            return true;
        }
        
        // if policy set language at ep and epr are not the same then there is no
        // match. We get the policy language by looking at the first expression
        // of the first policy set. By this stage we know that all the policy sets
        // in an endpoint or endpoint reference will use a single language and we know 
        // that there is at least one policy set with at least one policy
        QName eprLanguage = null;
        
        for (PolicySet policySet : referencePolicySets){
            if (policySet.getPolicies().size() > 0){
                eprLanguage = policySet.getPolicies().get(0).getName();
                break;
            }
        }
        
        QName epLanguage = null;
          
        for (PolicySet policySet : endpoint.getPolicySets()){
            if (policySet.getPolicies().size() > 0){
                epLanguage = policySet.getPolicies().get(0).getName();
                break;
            }
        }
        
        if(!eprLanguage.equals(epLanguage)){
            logger.fine("No match because the policy sets on either side have policies in differnt languages " + 
                        eprLanguage + 
                        " and " +
                        epLanguage );
            return false;
        }
        
        // now do a policy specific language match 
        PolicyBuilder builder = builders.getPolicyBuilder(eprLanguage);
        boolean match = false;
        
        // switch the derived list of policy sets into the reference
        // it will be left there if there is a match
        List<PolicySet> originalPolicySets = endpointReference.getPolicySets();
        endpointReference.getPolicySets().clear();
        endpointReference.getPolicySets().addAll(referencePolicySets);
        
        if (builder != null) {
            // TODO - where to get builder context from?
            BuilderContext builderContext = new BuilderContext(monitor);
            
            match = builder.build(endpointReference, endpoint, builderContext);
        } 
                
        if (!match){
            logger.fine("No match because the language specific matching failed");
            endpointReference.getPolicySets().clear();
            endpointReference.getPolicySets().addAll(originalPolicySets);
        } else {
            logger.fine("Match because the language specific matching succeeded");
        }
        
        return match;
    }
    
    /**
     * Determine if endpoint reference and endpoint interface contracts match 
     */
    private boolean haveMatchingInterfaceContracts(EndpointReference endpointReference, Endpoint endpoint){
        logger.fine("Match interface of " + endpointReference.toString() + " to " + endpoint.toString());
        
        if (endpointReference.getReference().getInterfaceContract() == null){
            logger.fine("Match because there is no interface contract on the reference");
            return true;
        }
        
        // TODO - is there a better test for this. Would have to cast to the
        //        correct iface type to get to the resolved flag
        if (endpoint.getComponentServiceInterfaceContract().getInterface().getOperations().size() == 0){
            // the interface contract is likely remote but unresolved
            // we discussed this on the ML and decided that we could
            // live with this for the case where there is no central matching of references
            // to services. Any errors will be detected when the message flows.
            logger.fine("Match because the endpoint is remote and we don't have a copy of it's interface contract");
            return true;
        }
             
        boolean match = false;
        match = interfaceContractMapper.isCompatible(endpointReference.getReference().getInterfaceContract(), 
                                                     endpoint.getComponentServiceInterfaceContract());
        
        if (!match){
            logger.fine("Match because the linterface contract mapper failed");
        } else {
            logger.fine("Match because the interface contract mapper succeeded");
        }
        
        return match;
    }
    
    /**
     * Checks to see if the registry has been updated since the reference was last matched
     * 
     * @return true is the registry has changed
     */
    public boolean isOutOfDate(EndpointRegistry endpointRegistry, EndpointReference endpointReference) {
        Endpoint te = endpointReference.getTargetEndpoint();
        if (te != null && !te.isUnresolved()
            && te.getURI() != null
            && endpointReference.getStatus() != EndpointReference.RESOLVED_BINDING) {
            List<Endpoint> endpoints = endpointRegistry.findEndpoint(endpointReference);
            return ! endpoints.contains(endpointReference.getTargetEndpoint());
        }
        return false;
    }

    /**
     * ASM_5021: where a <reference/> of a <component/> has @autowire=true 
     * and where the <reference/> has a <binding/> child element which 
     * declares a single target service,  the reference is wired only to 
     * the single service identified by the <wire/> element
     */    
    private void setSingleAutoWireTarget(ComponentReference reference) {
        if (reference.getEndpointReferences().size() > 1 && reference.getBindings() != null
            && reference.getBindings().size() == 1) {
            String uri = reference.getBindings().get(0).getURI();
            if (uri != null) {
                if (uri.indexOf('/') > -1) {
                    // TODO: must be a way to avoid this fiddling
                    int i = uri.indexOf('/');
                    String c = uri.substring(0, i);
                    String s = uri.substring(i + 1);
                    uri = c + "#service(" + s + ")";
                }
                for (EndpointReference er : reference.getEndpointReferences()) {
                    if (er.getTargetEndpoint() != null && uri.equals(er.getTargetEndpoint().getURI())) {
                        reference.getEndpointReferences().clear();
                        reference.getEndpointReferences().add(er);
                        return;
                    }
                }
            }
        }
    }
  
}
