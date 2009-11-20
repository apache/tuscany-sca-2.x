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

package org.apache.tuscany.sca.implementation.web.runtime.jsp;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.oasisopen.sca.ServiceReference;

/**
 * Tag to handle SCA references
 * <sca:reference name="service" type="test.MyService" scope="1" />
 *
 * @version $Rev$ $Date$
 */
public class ReferenceTag extends TagSupport {
    private static final long serialVersionUID = 1L;

    protected String name;
    protected String type;
    protected Integer scope = PageContext.PAGE_SCOPE;

    @Override
    public int doStartTag() throws JspException {
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {

        ServletContext servletContext = pageContext.getServletContext();
        RuntimeComponent component = (RuntimeComponent)servletContext.getAttribute("org.apache.tuscany.sca.implementation.web.RuntimeComponent");
        if (component == null) {
            throw new JspException("No Web component found. Is there an <implementation.web> missing?");
        }
        
        Class<?> typeClass;
        try {
            typeClass = Class.forName(type, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new JspException("Reference '" + name + "' type class not found: " + type);
        }
        
        ServiceReference<?> sr = component.getComponentContext().getServiceReference(typeClass, name);
        if (sr == null) {
            throw new JspException("Reference '" + name + "' undefined");
        }

        pageContext.setAttribute(name, sr.getService(), scope);

        return EVAL_PAGE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
