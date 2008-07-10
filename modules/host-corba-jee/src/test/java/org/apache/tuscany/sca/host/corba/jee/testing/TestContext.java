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

package org.apache.tuscany.sca.host.corba.jee.testing;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.apache.tuscany.sca.host.corba.jee.JEECorbaHost;
import org.omg.CORBA.ORB;

/**
 * Mock implementation of javax.naming.Context interface.
 */
public class TestContext implements Context {

    private ORB orb;

    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        return null;
    }

    public void bind(Name name, Object obj) throws NamingException {

    }

    public void bind(String name, Object obj) throws NamingException {

    }

    public void close() throws NamingException {

    }

    public Name composeName(Name name, Name prefix) throws NamingException {
        return null;
    }

    public String composeName(String name, String prefix) throws NamingException {
        return null;
    }

    public Context createSubcontext(Name name) throws NamingException {
        return null;
    }

    public Context createSubcontext(String name) throws NamingException {
        return null;
    }

    public void destroySubcontext(Name name) throws NamingException {

    }

    public void destroySubcontext(String name) throws NamingException {

    }

    public Hashtable<?, ?> getEnvironment() throws NamingException {
        return null;
    }

    public String getNameInNamespace() throws NamingException {
        return null;
    }

    public NameParser getNameParser(Name name) throws NamingException {
        return null;
    }

    public NameParser getNameParser(String name) throws NamingException {
        return null;
    }

    public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
        return null;
    }

    public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
        return null;
    }

    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        return null;
    }

    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        return null;
    }

    public Object lookup(Name name) throws NamingException {
        return null;
    }

    public Object lookup(String name) throws NamingException {
        try {
            if (name.equals(JEECorbaHost.ORB_NAME)) {
                if (orb == null) {
                    // get ORB which was spawned under host and port declared in
                    // test class
                    String[] args =
                        {"-ORBInitialHost", JEECorbaHostTestCase.LOCALHOST, "-ORBInitialPort",
                         "" + JEECorbaHostTestCase.DEFAULT_PORT};
                    orb = ORB.init(args, null);
                }
            } else {
                throw new NamingException("Unknown name: " + name);
            }
        } catch (Exception e) {
            throw new NamingException(e.getMessage());
        }
        return orb;
    }

    public Object lookupLink(Name name) throws NamingException {
        return null;
    }

    public Object lookupLink(String name) throws NamingException {
        return null;
    }

    public void rebind(Name name, Object obj) throws NamingException {

    }

    public void rebind(String name, Object obj) throws NamingException {

    }

    public Object removeFromEnvironment(String propName) throws NamingException {
        return null;
    }

    public void rename(Name oldName, Name newName) throws NamingException {

    }

    public void rename(String oldName, String newName) throws NamingException {

    }

    public void unbind(Name name) throws NamingException {

    }

    public void unbind(String name) throws NamingException {

    }

}
