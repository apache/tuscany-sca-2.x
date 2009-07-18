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

package org.apache.tuscany.sca.implementation.osgi;

import javax.xml.namespace.QName;

/**
 * <tuscany:osgi.property> 
 */
public interface OSGiProperty {
    String NAME = "name";
    QName PROPERTY_QNAME = new QName(OSGiImplementation.SCA11_TUSCANY_NS, "osgi.property");

    String SCA_BINDINGS = "sca.bindings";
    String SCA_REFERENCE = "sca.reference";
    String SCA_SERVICE = "sca.service";
    String SCA_REFERENCE_BINDING = "sca.reference.binding";
    String SCA_SERVICE_BINDING = "sca.service.binding";
    String REMOTE_CONFIG_SCA = "sca";
    
    /**
     * The configuration types supported by this Distribution Provider. Services
     * that are suitable for distribution list the configuration types that
     * describe the configuration information for that service in the
     * SERVICE_EXPORTED_CONFIGS or SERVICE_IMPORTED_CONFIGS property. A
     * distribution provider must register a service that has this property and
     * enumerate all configuration types that it supports. The type of this
     * property String+
     * 
     * @see SERVICE_EXPORTED_CONFIGS
     * @see SERVICE_IMPORTED_CONFIGS
     */
    String REMOTE_CONFIGS_SUPPORTED = "remote.configs.supported";
    /**
     * Service property that lists the intents supported by the distribution
     * provider. Each distribution provider must register a service that has
     * this property and enumerate all the supported intents, having any
     * qualified intents expanded. The value of this property is of type
     * String+. 
     * 
     * @see SERVICE_INTENTS
     * @see SERVICE_EXPORTED_INTENTS
     * @see SERVICE_EXPORTED_INTENTS_EXTRA
     */
    String REMOTE_INTENTS_SUPPORTED = "remote.intents.supported";
    /**
     * A list of configuration types that should be used to export the service.
     * Configuration types can be synonymous or alternatives. In principle, a
     * distribution provider should create an endpoint for each recognized
     * configuration type, the deployer is responsible that synonyms do not
     * clash. Each configuration type has an associated specification that
     * describes how the configuration data for the exported service is
     * represented in an OSGi framework. The value of this property is of type
     * String+.
     */
    String SERVICE_EXPORTED_CONFIGS = "service.exported.configs";
    /**
     * A list of intents that the distribution provider must implement to
     * distribute the service. Intents listed in this property are reserved for
     * intents that are critical for the code to function correctly, for
     * example, ordering of messages. These intents should not be configurable.
     * The value of this property is of type String+.
     */
    String SERVICE_EXPORTED_INTENTS = "service.exported.intents";
    /**
     * Extra intents configured in addition to the the intents specified in
     * SERVICE_EXPORTED_INTENTS. These intents are merged with the service.
     * exported.intents and therefore have the same semantics. They are extra,
     * so that the SERVICE_EXPORTED_INTENTS can be set by the bundle developer
     * and this property is then set by the administrator/deployer. Bundles
     * should make this property configurable, for example through the
     * Configuration Admin service. The value of this property is of type
     * String+.
     */
    String SERVICE_EXPORTED_INTENTS_EXTRA = "service.exported.intents.extra";
    /**
     * Defines the interfaces under which this service can be exported. This
     * list must be a subset of the types listed in the objectClass service
     * property. The single value of an asterisk ('*' *) indicates all
     * interfaces in the registration's objectClass property (not classes). It
     * is highly recommended to only export interfaces and not concrete classes
     * due to the complexity of creating proxies for some type of classes. The
     * value of this property is of type String+.
     */
    String SERVICE_EXPORTED_INTERFACES = "service.exported.interfaces";
    /**
     * Must be set by a distribution provider to true when it registers the
     * end-point proxy as an imported service. Can be used by a bundle to
     * prevent it from getting an imported service. The value of this property
     * is not defined, setting it is sufficient.
     */
    String SERVICE_IMPORTED = "service.imported";
    /**
     * A list of intents that this service implements. This property has dual
     * purpose. A bundle can use this service property to notify the
     * distribution provider that these intents are already implemented by the
     * exported service object. For an imported service, a distribution provider
     * must use this property to convey the combined intents of the exporting
     * service and the intents that the distribution providers add. To export a
     * service, a distribution provider must recognize all these intents and
     * expand any qualified intents. The value of this property is of type
     * String+.
     */
    String SERVICE_INTENTS = "service.intents";
    /**
     * The configuration type used to import this services, as described in
     * SERVICE_EXPORTED_CONFIGS. Any associated properties for this
     * configuration types must be properly mapped to the importing system. For
     * example, a URL in these properties must point to a valid resource when
     * used in the importing framework. Configuration types in this property
     * must be synonymous. The value of this property is of type String+.
     */
    String SERVICE_IMPORTED_CONFIGS = "service.imported.configs";

    String getValue();

    void setValue(String value);

    String getName();

    void setName(String name);
}
