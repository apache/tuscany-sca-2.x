/*
 *
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

package org.apache.tuscany.sca.osgi.service.remoteadmin;

import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;

/**
 * A bundle’s authority to register or get a service.
 * <ul>
 * <li>The register action allows a bundle to register a service on the
 * specified names.
 * <li>The get action allows a bundle to detect a service and get it.
 * EndpointPermission to get the specific service.
 * </ul>
 * 
 * @ThreadSafe
 */
public final class EndpointPermission extends BasicPermission {
    private static final long serialVersionUID = 577543263888050488L;
    /**
     * The action string get.
     */
    public static final String EXPORT = "export";
    /**
     * The action string register.
     */
    public static final String IMPORT = "import";

    private String actions;

    /**
     *Create a new EndpointPermission.
     * 
     * @param name The service class name The name of the service is specified
     *            as a fully qualified class name. Wildcards may be used.
     *            <p>
     *            name ::= <class name> | <class name ending in ".*"> | *
     *            <p>
     *            Examples:
     *            <ul>
     *            <li>org.osgi.service.http.HttpService
     *            <li>org.osgi.service.http.*
     *            <li>*
     *            </ul>
     *            For the get action, the name can also be a filter expression.
     *            The filter gives access to the service properties as well as
     *            the following attributes:
     *            <p>
     *            <ul>
     *            <li>signer - A Distinguished Name chain used to sign the
     *            bundle publishing the service. Wildcards in a DN are not
     *            matched according to the filter string rules, but according to
     *            the rules defined for a DN chain.
     *            <li>location - The location of the bundle publishing the
     *            service.
     *            <li>id - The bundle ID of the bundle publishing the service.
     *            <li>name - The symbolic name of the bundle publishing the
     *            service.
     *            </ul>
     *            Since the above attribute names may conflict with service
     *            property names used by a service, you can prefix an attribute
     *            name with '@' in the filter expression to match against the
     *            service property and not one of the above attributes. Filter
     *            attribute names are processed in a case sensitive manner
     *            unless the attribute references a service property. Service
     *            properties names are case insensitive.
     *            <p>
     *            There are two possible actions: get and register. The get
     *            permission allows the owner of this permission to obtain a
     *            service with this name. The register permission allows the
     *            bundle to register a service under that name.
     * @param actions actions get,register (canonical order)
     * @throws IllegalArgumentException – If the specified name is a filter
     *             expression and either the specified action is not get or the
     *             filter has an invalid syntax.
     */
    public EndpointPermission(String name, String actions) {
        super(name);
        this.actions = actions;
    }

    /**
     * Creates a new requested EndpointPermission object to be used by code that
     * must perform checkPermission for the get action. EndpointPermission
     * objects created with this constructor cannot be added to a
     * EndpointPermission permission collection.
     * 
     * @param endpoint The requested service.
     * @param actions The action get.
     * @throws IllegalArgumentException – If the specified action is not get or
     *             reference is null.
     */
    public EndpointPermission(EndpointDescription endpoint, String actions) {
        super(null);
        this.actions = actions;
    }

    /**
     * Determines the equality of two EndpointPermission objects. Checks that
     * specified object has the same class name and action as this
     * EndpointPermission. obj The object to test for equality.
     * 
     * @return true if obj is a EndpointPermission, and has the same class name
     *         and actions as this EndpointPermission object; false otherwise.
     */
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Returns the canonical string representation of the actions. Always
     * returns present actions in the following order: get, register.
     * 
     * @return The canonical string representation of the actions.
     */
    public String getActions() {
        return actions;
    }

    /**
     * Returns the hash code value for this object. Returns Hash code value for
     * this object.
     * 
     * @return
     */
    public int hashCode() {
        return super.hashCode();
    }

    /**
     *Determines if a EndpointPermission object "implies" the specified
     * permission.
     * 
     * @param p The target permission to check.
     * @return true if the specified permission is implied by this object; false
     *         otherwise. newPermissionCollection()
     */
    public boolean implies(Permission p) {
        return super.implies(p);
    }

    /**
     * Returns a new PermissionCollection object for storing EndpointPermission
     * objects.
     * 
     * @return A new PermissionCollection object suitable for storing
     *         EndpointPermission objects.
     */
    public PermissionCollection newPermissionCollection() {
        return super.newPermissionCollection();
    }
}
