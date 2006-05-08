/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.tuscany.binding.axis2.entrypoint;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis2.description.AxisService;
import org.apache.axis2.transport.http.AxisServlet;
import org.apache.tuscany.binding.axis2.util.ClassLoaderHelper;

/**
 * @version $Rev: 383148 $ $Date: 2006-03-04 08:07:17 -0800 (Sat, 04 Mar 2006) $
 */
public class WebServiceEntryPointServlet extends AxisServlet {

    private static final long serialVersionUID = 1L;

    private AxisService axisService;

    public WebServiceEntryPointServlet(AxisService axisService) {
        this.axisService = axisService;
    }

    public void init(final ServletConfig config) throws ServletException {
        ClassLoaderHelper.initApplicationClassLoader();
        try {
            ClassLoaderHelper.setSystemClassLoader();
            try {

                super.init(config);

                configContext.getAxisConfiguration().addService(axisService);

            } catch (Exception e) {
                throw new ServletException(e);
            }
        } finally {
            ClassLoaderHelper.setApplicationClassLoader();
        }
    }

    @Override
    protected void doGet(final HttpServletRequest arg0, final HttpServletResponse arg1) throws ServletException, IOException {
        ClassLoaderHelper.initApplicationClassLoader();
        try {
            ClassLoaderHelper.setSystemClassLoader();
            super.doGet(arg0, arg1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        } finally {
            ClassLoaderHelper.setApplicationClassLoader();
        }
    }

    @Override
    protected void doPost(final HttpServletRequest arg0, final HttpServletResponse arg1) throws ServletException, IOException {
        ClassLoaderHelper.initApplicationClassLoader();
        try {
            ClassLoaderHelper.setSystemClassLoader();
            super.doPost(arg0, arg1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        } finally {
            ClassLoaderHelper.setApplicationClassLoader();
        }
    }
}
