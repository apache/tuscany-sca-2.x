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

package org.apache.tuscany.sca.runtime.tomcat;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.catalina.core.StandardContext;

/**
 *
 * @version $Rev$ $Date$
 */
public class TuscanyContext extends StandardContext {
    private static final long serialVersionUID = 1L;

    public TuscanyContext() {
        setProcessTlds(false);
    }
    
    @Override
    public boolean getConfigured() {
        return true;
    }
    
    @Override
    public DirContext getResources() {
         return DUMMY_CONTEXT;
    }

    @Override
    public synchronized void setResources(DirContext resources) {
        setDocBase("tuscany");
        super.setResources(DUMMY_CONTEXT);
    }
    
    private static final DirContext DUMMY_CONTEXT = new DirContext(){

        public void bind(Name name, Object obj, Attributes attrs) throws NamingException {
        }

        public void bind(String name, Object obj, Attributes attrs) throws NamingException {
        }

        public DirContext createSubcontext(Name name, Attributes attrs) throws NamingException {
            return null;
        }

        public DirContext createSubcontext(String name, Attributes attrs) throws NamingException {
            return null;
        }

        public Attributes getAttributes(Name name) throws NamingException {
            return null;
        }

        public Attributes getAttributes(String name) throws NamingException {
            return null;
        }

        public Attributes getAttributes(Name name, String[] attrIds) throws NamingException {
            return null;
        }

        public Attributes getAttributes(String name, String[] attrIds) throws NamingException {
            return null;
        }

        public DirContext getSchema(Name name) throws NamingException {
            return null;
        }

        public DirContext getSchema(String name) throws NamingException {
            return null;
        }

        public DirContext getSchemaClassDefinition(Name name) throws NamingException {
            return null;
        }

        public DirContext getSchemaClassDefinition(String name) throws NamingException {
            return null;
        }

        public void modifyAttributes(Name name, ModificationItem[] mods) throws NamingException {
        }

        public void modifyAttributes(String name, ModificationItem[] mods) throws NamingException {
        }

        public void modifyAttributes(Name name, int mod_op, Attributes attrs) throws NamingException {
        }

        public void modifyAttributes(String name, int mod_op, Attributes attrs) throws NamingException {
        }

        public void rebind(Name name, Object obj, Attributes attrs) throws NamingException {
        }

        public void rebind(String name, Object obj, Attributes attrs) throws NamingException {
        }

        public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes) throws NamingException {
            return null;
        }

        public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes) throws NamingException {
            return null;
        }

        public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
            return null;
        }

        public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
            return null;
        }

        public NamingEnumeration<SearchResult> search(Name name, String filter, SearchControls cons) throws NamingException {
            return null;
        }

        public NamingEnumeration<SearchResult> search(String name, String filter, SearchControls cons) throws NamingException {
            return null;
        }

        public NamingEnumeration<SearchResult> search(Name name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
            return null;
        }

        public NamingEnumeration<SearchResult> search(String name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
            return null;
        }

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
            throw new NamingException();
        }

        public Object lookup(Name name) throws NamingException {
            return null;
        }

        public Object lookup(String name) throws NamingException {
            return null;
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
     };

}
