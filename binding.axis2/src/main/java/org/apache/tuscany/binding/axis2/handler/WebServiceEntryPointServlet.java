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

package org.apache.tuscany.binding.axis2.handler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.SessionContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.InOutAxisOperation;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.AxisEngine;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HTTPTransportUtils;
import org.apache.axis2.transport.http.ListingAgent;
import org.apache.axis2.transport.http.ServletBasedOutTransportInfo;
import org.apache.tuscany.binding.axis2.assembly.WebServiceBinding;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Module;

/**
 * Class AxisServlet
 */
public class WebServiceEntryPointServlet
    extends HttpServlet
{

    private static final long serialVersionUID = -2085869393709833372L;

    private static final String CONFIGURATION_CONTEXT = "CONFIGURATION_CONTEXT";

    public static final String SESSION_ID = "SessionId";

    private ConfigurationContext configContext;

    private AxisConfiguration axisConfiguration;

    private ListingAgent lister;

    private MessageContext createAndSetInitialParamsToMsgCtxt( Object sessionContext, MessageContext msgContext,
                                                              HttpServletResponse httpServletResponse,
                                                              HttpServletRequest httpServletRequest )
        throws AxisFault
    {
        msgContext = new MessageContext();
        msgContext.setConfigurationContext( configContext );
        msgContext.setSessionContext( (SessionContext) sessionContext );
        msgContext.setTransportIn( axisConfiguration.getTransportIn( new QName( Constants.TRANSPORT_HTTP ) ) );
        msgContext.setTransportOut( axisConfiguration.getTransportOut( new QName( Constants.TRANSPORT_HTTP ) ) );

        msgContext.setProperty( Constants.OUT_TRANSPORT_INFO, new ServletBasedOutTransportInfo( httpServletResponse ) );
        msgContext.setProperty( MessageContext.TRANSPORT_HEADERS, getTransportHeaders( httpServletRequest ) );
        msgContext.setProperty( SESSION_ID, httpServletRequest.getSession().getId() );

        return msgContext;
    }

    public void destroy()
    {
        super.destroy();
    }

    /**
     * Method doGet
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet( HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse )
        throws ServletException, IOException
    {
        MessageContext msgContext = null;
        OutputStream out = null;

        try
        {
            Object sessionContext = getSessionContext( httpServletRequest );
            HashMap map = getHTTPParameters( httpServletRequest );

            msgContext = createAndSetInitialParamsToMsgCtxt( sessionContext, msgContext, httpServletResponse,
                                                             httpServletRequest );
            msgContext.setDoingREST( true );
            msgContext.setServerSide( true );
            out = httpServletResponse.getOutputStream();

            boolean processed = HTTPTransportUtils.processHTTPGetRequest( msgContext, httpServletRequest
                .getInputStream(), out, httpServletRequest.getContentType(), httpServletRequest
                .getHeader( HTTPConstants.HEADER_SOAP_ACTION ), httpServletRequest.getRequestURL().toString(),
                                                                          configContext, map );

            if ( !processed )
            {
                lister.handle( httpServletRequest, httpServletResponse, out );
            }
        }
        catch ( AxisFault e )
        {
            if ( msgContext != null )
            {
                handleFault( msgContext, out, e );
            }
            else
            {
                throw new ServletException( e );
            }
        }
        catch ( Exception e )
        {
            throw new ServletException( e );
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */

    /**
     * Method doPost
     *
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException
    {
        MessageContext msgContext = null;
        OutputStream out = null;

        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        ClassLoader mycl = getClass().getClassLoader();
        try
        {
            if ( tccl != mycl )
            {
                Thread.currentThread().setContextClassLoader( mycl );
            }
            Object sessionContext = getSessionContext( req );

            msgContext = createAndSetInitialParamsToMsgCtxt( sessionContext, msgContext, res, req );

            // adding ServletContext into msgContext;
            msgContext.setProperty( Constants.SERVLET_CONTEXT, sessionContext );
            out = res.getOutputStream();
            HTTPTransportUtils.processHTTPPostRequest( msgContext, req.getInputStream(), out, req.getContentType(), req
                .getHeader( HTTPConstants.HEADER_SOAP_ACTION ), req.getRequestURL().toString() );

            Object contextWritten = msgContext.getOperationContext().getProperty( Constants.RESPONSE_WRITTEN );

            res.setContentType( "text/xml; charset=" + msgContext.getProperty( MessageContext.CHARACTER_SET_ENCODING ) );

            if ( ( contextWritten == null ) || !Constants.VALUE_TRUE.equals( contextWritten ) )
            {
                res.setStatus( HttpServletResponse.SC_ACCEPTED );
            }
        }
        catch ( AxisFault e )
        {
            if ( msgContext != null )
            {
                res.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
                handleFault( msgContext, out, e );
            }
            else
            {
                throw new ServletException( e );
            }
        }
        finally
        {
            if ( tccl != mycl )
            {
                Thread.currentThread().setContextClassLoader( tccl );
            }
        }
    }

    private void handleFault( MessageContext msgContext, OutputStream out, AxisFault e )
        throws AxisFault
    {
        msgContext.setProperty( MessageContext.TRANSPORT_OUT, out );

        AxisEngine engine = new AxisEngine( configContext );
        MessageContext faultContext = engine.createFaultMessageContext( msgContext, e );

        engine.sendFault( faultContext );
    }

    /**
     * Method init
     *
     * @param config
     * @throws ServletException
     */
    public void init( ServletConfig config )
        throws ServletException
    {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        ClassLoader mycl = getClass().getClassLoader();
        try
        {
            if ( tccl != mycl )
            {
                Thread.currentThread().setContextClassLoader( mycl );
            }
            configContext = initConfigContext( config );
            initTuscany( configContext.getAxisConfiguration(), config );
            lister = new ListingAgent( configContext );
            axisConfiguration = configContext.getAxisConfiguration();
            config.getServletContext().setAttribute( CONFIGURATION_CONTEXT, configContext );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            throw new ServletException( e );
        }
        finally
        {
            if ( tccl != mycl )
            {
                Thread.currentThread().setContextClassLoader( tccl );
            }
        }
    }

    RuntimeContext getTuscanyWebAppRuntime( ServletConfig config )
    {

        Object ret = (RuntimeContext) ( config ).getServletContext()
            .getAttribute( "org.apache.tuscany.core.runtime.RuntimeContext" );
        if ( !( ret instanceof RuntimeContext ) )
        {
            RuntimeException rete = new RuntimeException( "Tuscany configuration not found! "
                + ( ( ret == null ) ? "" : "unexpected class" + ret.getClass() ) );
            rete.printStackTrace();//pretty majar make sure something gets logged.
            throw rete;

        }
        return (RuntimeContext) ret;
    }

    void initTuscany( final AxisConfiguration axisConfig, ServletConfig config )
        throws AxisFault
    {

        // Register all the Web service entry points
        RuntimeContext tuscanyRuntime = getTuscanyWebAppRuntime( config );

        // Get the current SCA module context

        // AggregateContext moduleContext = (AggregateContext) tuscanyRuntime.getAggregate().getAssemblyModelContext();//getRootContext();//.getModuleComponentContext();
        try
        {
            tuscanyRuntime.start();
            ServletContext servletContext = config.getServletContext();
            AggregateContext moduleContext = (AggregateContext) servletContext.getAttribute("org.apache.tuscany.core.webapp.ModuleComponentContext" );
            Module module = (Module) moduleContext.getAggregate();

                for ( Iterator i = module.getEntryPoints().iterator(); i.hasNext(); )
                {
                    EntryPoint entryPoint = (EntryPoint) i.next();
                    final String epName = entryPoint.getName();

                    InstanceContext entryPointContext = moduleContext.getContext( epName );

                    Binding binding = (Binding) entryPoint.getBindings().get( 0 );
                    if ( binding instanceof WebServiceBinding )
                    {

                        WebServiceBinding wsBinding = (WebServiceBinding) binding;
                        Definition definition = wsBinding.getWSDLDefinition();
                        Port port = wsBinding.getWSDLPort();
                        QName qname = new QName( definition.getTargetNamespace(), port.getName() );
                        if ( qname != null )
                        {

                            WebServicePortMetaData wsdlPortInfo = new WebServicePortMetaData( definition, port, null,
                                                                                              false );

                            WebServiceEntryPointInOutSyncMessageReceiver msgrec = new WebServiceEntryPointInOutSyncMessageReceiver(
                                                                                                                                    moduleContext,
                                                                                                                                    entryPoint,
                                                                                                                                    (EntryPointContext) entryPointContext,
                                                                                                                                    wsdlPortInfo );

                            AxisServiceGroup serviceGroup = new AxisServiceGroup( axisConfig );
                            axisConfig
                                .addMessageReceiver( WebServiceEntryPointInOutSyncMessageReceiver.MEP_URL, msgrec );
                            serviceGroup.setServiceGroupName( wsdlPortInfo.getServiceName().getLocalPart() );

                            // to create service from wsdl stream --->
                            // AxisServiceBuilder axisServiceBuilder = new AxisServiceBuilder();
                            // return axisServiceBuilder.getAxisService(in);

                            AxisService axisService = new AxisService( epName );
                            axisService.setParent( serviceGroup );
                            axisService.setServiceDescription( "Tuscany configured service EntryPoint name '" + epName
                                + "'" );
                            // axisService.setTargetNamespace(wsdlPortInfo.getPortName().getNamespaceURI());
                            axisService.addMessageReceiver( WebServiceEntryPointInOutSyncMessageReceiver.MEP_URL,
                                                            msgrec );

                            // Create operation descriptions for all the operations
                            PortType wsdlPortType = wsdlPortInfo.getPortType();
                            for ( Iterator j = wsdlPortType.getOperations().iterator(); j.hasNext(); )
                            {
                                Operation wsdlOperation = (Operation) j.next();
                                String operationName = wsdlOperation.getName();
                                AxisOperation axisOp = new InOutAxisOperation( new javax.xml.namespace.QName( qname
                                    .getNamespaceURI(), operationName ) );
                                axisOp.setMessageReceiver( msgrec );
                                axisService.addOperation( axisOp );
                                axisOp.setMessageExchangePattern( WebServiceEntryPointInOutSyncMessageReceiver.MEP_URL );

                                axisConfig.addService( axisService );

                            }
                            axisConfig.addServiceGroup( serviceGroup );

                        }

                    }
                }
        }
        finally
        {
            // tuscanyRuntime.stop();
        }
    }

    /**
     * Initialize the Axis configuration context
     *
     * @param config Servlet configuration
     * @throws ServletException
     */
    protected ConfigurationContext initConfigContext( ServletConfig config )
        throws ServletException
    {
        try
        {
            ServletContext context = config.getServletContext();
            String repoDir = context.getRealPath( "/WEB-INF" );
            ConfigurationContextFactory erfac = new ConfigurationContextFactory();
            ConfigurationContext configContext = erfac.createConfigurationContextFromFileSystem( repoDir );
            configContext.setProperty( Constants.CONTAINER_MANAGED, Constants.VALUE_TRUE );
            configContext.setRootDir( new File( context.getRealPath( "/WEB-INF" ) ) );
            return configContext;
        }
        catch ( Exception e )
        {
            throw new ServletException( e );
        }
    }

    private HashMap getHTTPParameters( HttpServletRequest httpServletRequest )
    {
        HashMap map = new HashMap();
        Enumeration enu = httpServletRequest.getParameterNames();

        while ( enu.hasMoreElements() )
        {
            String name = (String) enu.nextElement();
            String value = httpServletRequest.getParameter( name );

            map.put( name, value );
        }

        return map;
    }

    private Object getSessionContext( HttpServletRequest httpServletRequest )
    {
        Object sessionContext = httpServletRequest.getSession( true ).getAttribute( Constants.SESSION_CONTEXT_PROPERTY );

        if ( sessionContext == null )
        {
            sessionContext = new SessionContext( null );
            httpServletRequest.getSession().setAttribute( Constants.SESSION_CONTEXT_PROPERTY, sessionContext );
        }

        return sessionContext;
    }

    private Map getTransportHeaders( HttpServletRequest req )
    {
        HashMap headerMap = new HashMap();
        Enumeration headerNames = req.getHeaderNames();

        while ( headerNames.hasMoreElements() )
        {
            String key = (String) headerNames.nextElement();
            String value = req.getHeader( key );

            headerMap.put( key, value );
        }

        return headerMap;
    }
    //RRFOO
    //TODO get axis2.xml in
    /*
     *
     // Get the current SCA module context
     AggregateContext moduleContext = tuscanyRuntime.getModuleComponentContext();
     tuscanyRuntime.start();
     try {

     Module module = (Module)moduleContext.getAggregate();
     AssemblyModelContext modelContext = module.getAssemblyModelContext();

     // Load the .wsdd configuration
     ResourceLoader bundleContext = modelContext.getResourceLoader();
     InputStream wsdd;
     try {
     URL url = bundleContext.getResource("org/apache/tuscany/binding/axis/engine/config/server-config.wsdd");
     wsdd = url.openStream();
     } catch (IOException e1) {
     throw new ServiceRuntimeException(e1);
     }

     *
     */
}
