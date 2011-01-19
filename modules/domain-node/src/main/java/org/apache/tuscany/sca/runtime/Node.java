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

package org.apache.tuscany.sca.runtime;

import java.io.Reader;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.oasisopen.sca.NoSuchServiceException;

public interface Node {

    /**
     * Creates an installed contribution from a supplied root contribution and installed at a supplied base URI.
     * See section 10.5.1 of the Assembly Specification.
     * 
     * @param uri  the base uri of where to install the contribution. May be null in which case a URI is derived from the contribution URL
     * @param contributionURL  the URL where the contribution is located
     * @param metaDataURL  the location of an optional generated Contribution Metadata Document. See section 10.2.2
     *               of the Assembly Specification. May be null.
     * @param dependentContributionURIs  specifies the contributions that are used to resolve the dependencies of the 
     *               root contribution and other dependent contributions. May be null.
     * @param startDeployables  true if the composites defined as deployable in the contribution's sca-contribution.xml
     *               file or supplied metaData file should be started, false if they should not be. 
     * @return the URI of the installed contribution
     * 
     * @throws ContributionReadException 
     * @throws ActivationException 
     * @throws ValidationException 
     */
    String installContribution(String uri, String contributionURL, String metaDataURL, List<String> dependentContributionURIs, boolean startDeployables) throws ContributionReadException, ActivationException, ValidationException;

    /**
     * Creates an installed contribution from a supplied Contribution object.
     * See section 10.5.1 of the Assembly Specification.
     * 
     * @param contribution  the Contribution object
     * @param dependentContributionURIs  specifies the contributions that are used to resolve the dependencies of the 
     *               root contribution and other dependent contributions. May be null.
     * @param startDeployables  true if the composites defined as deployable in the contribution's sca-contribution.xml
     *               file or supplied metaData file should be started, false if they should not be. 
     * @return the URI of the installed contribution
     * 
     * @throws ContributionReadException 
     * @throws ActivationException 
     * @throws ValidationException 
     */
    String installContribution(Contribution contribution, List<String> dependentContributionURIs, boolean startDeployables) throws ContributionReadException, ActivationException, ValidationException;

    /**
     * Creates an installed contribution from a supplied root contribution URL.
     * See section 10.5.1 of the Assembly Specification. This method is the same
     * as calling installContribution(null, contributionURL, null, null, true)
     * 
     * @param contributionURL  the URL where the contribution is located
     * @return the URI of the installed contribution
     * 
     * @throws ContributionReadException 
     * @throws ActivationException 
     * @throws ValidationException 
     */
    String installContribution(String contributionURL) throws ContributionReadException, ActivationException, ValidationException;
    
    /**
     * 4599 10.5.2 add Deployment Composite & update Deployment Composite
     * 4600 Adds or updates a deployment composite using a supplied composite ("composite by value" - a data
     * 4601 structure, not an existing resource in the Domain) to the contribution identified by a supplied contribution
     * 4602 URI. The added or updated deployment composite is given a relative URI that matches the @name
     * 4603 attribute of the composite, with a ".composite" suffix. Since all composites run within the context of a
     * 4604 installed contribution (any component implementations or other definitions are resolved within that
     * 4605 contribution), this functionality makes it possible for the deployer to create a composite with final
     * 4606 configuration and wiring decisions and add it to an installed contribution without having to modify the
     * 4607 contents of the root contribution.
     * 4608 Also, in some use cases, a contribution might include only implementation code (e.g. PHP scripts). It is
     * 4609 then possible for those to be given component names by a (possibly generated) composite that is added
     * 4610 into the installed contribution, without having to modify the packaging.     * 
     * 
     * @param contributionURI the URI of the installed contribution to add the composite to
     * @param compositeXML the composite to add to the contribution
     * @return
     * @throws XMLStreamException 
     * @throws ContributionReadException 
     * @throws ActivationException 
     * @throws ValidationException 
     */
    String start(String contributionURI, Reader compositeXML) throws ContributionReadException, XMLStreamException, ActivationException, ValidationException;
    String start(String contributionURI, Composite composite) throws ActivationException, ValidationException;

    /**
     * 4611 11.4.310.5.3 remove Contribution
     * 4612 Removes the deployed contribution identified by a supplied contribution URI.
     * 
     * @param contributionURI
     * @return List of contribution URIs (includes dependent contributions) which were removed
     * @throws ActivationException 
     */
    List<String> removeContribution(String contributionURI) throws ActivationException;

    /**
     * 4677 10.7.1 add To Domain-Level Composite
     * 4678 This functionality adds the composite identified by a supplied URI to the Domain Level Composite. The
     * 4679 supplied composite URI refers to a composite within an installed contribution. The composite's installed
     * 4680 contribution determines how the composite's artifacts are resolved (directly and indirectly). The supplied
     * 4681 composite is added to the domain composite with semantics that correspond to the domain-level
     * 4683 components become top-level components and the component services become externally visible
     * 4684 services (eg. they would be present in a WSDL description of the Domain). The meaning of any promoted
     * 4685 services and references in the supplied composite is not defined; since there is no composite scope
     * 4686 outside the domain composite, the usual idea of promotion has no utility.
     *  
     * @param cotributionURI
     * @param compositeURI
     * @throws ActivationException 
     * @throws ValidationException 
     */
    void start(String contributionURI, String compositeURI) throws ActivationException, ValidationException;
    
    /**
     * 4687 10.7.2 remove From Domain-Level Composite
     * 4688 Removes from the Domain Level composite the elements corresponding to the composite identified by a
     * 4689 supplied composite URI. This means that the removal of the components, wires, services and references
     * 4690 originally added to the domain level composite by the identified composite.   
     * @param contributionURI
     * @param compositeURI
     * @throws ActivationException 
     */
    void stop(String contributionURI, String compositeURI) throws ActivationException;

    /**
     * 10.7.3 get Domain-Level Composite
     * Returns a <composite> definition that has an <include> line for each composite that had been added to
     * the domain level composite. It is important to note that, in dereferencing the included composites, any
     * referenced artifacts are resolved in terms of that installed composite.
     * 
     * @return
     */
    Composite getDomainLevelComposite();
    
    /* that previous one returns a Composte object but not sure what would be most appropriate, and having one return a string seems convenient: */
    String getDomainLevelCompositeAsString();
    
    /**
     * 4695 10.7.4 get QName Definition
     * 4696 In order to make sense of the domain-level composite (as returned by get Domain-Level Composite), it
     * 4697 needs to be possible to get the definitions for named artifacts in the included composites. This
     * 4698 functionality takes the supplied URI of an installed contribution (which provides the context), a supplied
     * 4699 qualified name of a definition to look up, and a supplied symbol space (as a QName, e.g.
     * 4700 wsdl:PortTypeportType). The result is a single definition, in whatever form is appropriate for that
     * 4701 definition type.
     * 4702 Note that this, like all the other domain-level operations, is a conceptual operation. Its capabilities need to
     * 4703 exist in some form, but not necessarily as a service operation with exactly this signature.     
     * @param contributionURI
     * @param definition
     * @param symbolSpace
     * @return
     */
    Object getQNameDefinition(String contributionURI, QName definition, QName symbolSpace);

    /**
     * Probably want to be able to stop it all at once so a method called stop or shutdown or destroy
     */
    void stop();

    /**
     * Would also be convenient to get service proxys as from SCAClientFactory
     */
    <T> T getService(Class<T> interfaze, String serviceURI) throws NoSuchServiceException;    

    /**
     * Get the URIs of any composites that have been started for a contribution
     * @param contributionURI  the contribution URI
     * @return the List of started composite URIs
     */
    List<String> getStartedCompositeURIs(String contributionURI);

    /**
     * Get the URIs of all the contributions installed on this Node
     * @return the list of installed contribution URIs
     */
    List<String> getInstalledContributionURIs();

    /**
     * Get an installed Contribution
     * @param uri  the URI of the contribution
     * @return the Contribution
     */
    Contribution getInstalledContribution(String uri);
    
    String getDomainName();

// TODO: the spec is unclear if update is different from remove/install, leave it out for now    
//    /**
//     * 4577 10.5.1 install Contribution & update Contribution
//     * 4578 Creates or updates an installed contribution with a supplied root contribution, and installed at a supplied
//     * 4579 base URI. A supplied dependent contribution list (<export/> elements) specifies the contributions that are
//     * 4580 used to resolve the dependencies of the root contribution and other dependent contributions. These
//     * 4581 override any dependent contributions explicitly listed via the @location attribute in the import statements
//     * 4582 of the contribution.
//     * 4583 SCA follows the simplifying assumption that the use of a contribution for resolving anything also means
//     * 4584 that all other exported artifacts can be used from that contribution. Because of this, the dependent
//     * 4585 contribution list is just a list of installed contribution URIs. There is no need to specify what is being used
//     * 4586 from each one.
//     * 4587 Each dependent contribution is also an installed contribution, with its own dependent contributions. By
//     * 4588 default these dependent contributions of the dependent contributions (which we will call indirect
//     * 4589 dependent contributions) are included as dependent contributions of the installed contribution. However,
//     * 4590 if a contribution in the dependent contribution list exports any conflicting definitions with an indirect
//     * 4591 dependent contribution, then the indirect dependent contribution is not included (i.e. the explicit list
//     * 4592 overrides the default inclusion of indirect dependent contributions). Also, if there is ever a conflict
//     * 4593 between two indirect dependent contributions, then the conflict MUST be resolved by an explicit entry in
//     * 4594 the dependent contribution list.
//     * 4595 [ASM12009]
//     * 4596 Note that in many cases, the dependent contribution list can be generated. In particular, if the creator of
//     * 4597 a Domain is careful to avoid creating duplicate definitions for the same qualified name, then it is easy for
//     * 4598 this list to be generated by tooling.
//     *  
//     * @param uri
//     * @param contributionURL
//     */
//    void updateContribution(String uri, String contributionURL);
//    void updateContribution(Contribution contribution);
//    /**
//     * 4599 10.5.2 add Deployment Composite & update Deployment Composite
//     * 4600 Adds or updates a deployment composite using a supplied composite ("composite by value" - a data
//     * 4601 structure, not an existing resource in the Domain) to the contribution identified by a supplied contribution
//     * 4602 URI. The added or updated deployment composite is given a relative URI that matches the @name
//     * 4603 attribute of the composite, with a ".composite" suffix. Since all composites run within the context of a
//     * 4604 installed contribution (any component implementations or other definitions are resolved within that
//     * 4605 contribution), this functionality makes it possible for the deployer to create a composite with final
//     * 4606 configuration and wiring decisions and add it to an installed contribution without having to modify the
//     * 4607 contents of the root contribution.
//     * 4608 Also, in some use cases, a contribution might include only implementation code (e.g. PHP scripts). It is
//     * 4609 then possible for those to be given component names by a (possibly generated) composite that is added
//     * 4610 into the installed contribution, without having to modify the packaging.     * 
//     * 
//     * @param uri
//     * @param compositeXML
//     * @return
//     */
//    String updateDeploymentComposite(String uri, Reader compositeXML);
//    String updateDeploymentComposite(String uri, Composite composite);

}
