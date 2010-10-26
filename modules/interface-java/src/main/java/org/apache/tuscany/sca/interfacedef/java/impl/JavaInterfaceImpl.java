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
package org.apache.tuscany.sca.interfacedef.java.impl;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.ParameterizedType;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceImpl;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sca.policy.Intent;

import org.oasisopen.sca.ResponseDispatch;

/**
 * Represents a Java interface.
 * 
 * @version $Rev$ $Date$
 */
public class JavaInterfaceImpl extends InterfaceImpl implements JavaInterface {

    private String className;
    private WeakReference<Class<?>> javaClass;
    private Class<?> callbackClass;
    private QName qname;
    private String jaxwsWSDLLocation;
    private String jaxwsJavaInterfaceName;
    private Contribution contributionContainingClass;
    
    protected JavaInterfaceImpl() {
    	super();
    	// Mark the interface as unresolved until all the basic processing is complete
    	// including Intent & Policy introspection
    	this.setUnresolved(true);
    }

    public String getName() {
        if (isUnresolved()) {
            return className;
        } else if (javaClass != null) {
            return javaClass.get().getName();
        } else {
            return null;
        }
    }

    public void setName(String className) {
        if (!isUnresolved()) {
            throw new IllegalStateException();
        }
        this.className = className;
    }

    public QName getQName() {
        return qname;
    }

    public void setQName(QName interfacename) {
        qname = interfacename;
    }

    public Class<?> getJavaClass() {
        if (javaClass != null){
            return javaClass.get();
        } else {
            return null;
        }
    }

    public void setJavaClass(Class<?> javaClass) {
        this.javaClass = new WeakReference<Class<?>>(javaClass);
        if (javaClass != null) {
            this.className = javaClass.getName();
        }
    }
    
    public Class<?> getCallbackClass() {
        return callbackClass;
    }
    
    public void setCallbackClass(Class<?> callbackClass) {
        this.callbackClass = callbackClass;
    }
    
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JavaInterfaceImpl other = (JavaInterfaceImpl)obj;
        if (isUnresolved() || other.isUnresolved()) {
            if (className == null) {
                if (other.className != null)
                    return false;
            } else if (!className.equals(other.className))
                return false;
        } else {
            if (javaClass == null) {
                if (other.javaClass != null)
                    return false;
            } else if (!javaClass.get().equals(other.javaClass.get()))
                return false;
            if (callbackClass == null) {
                if (other.callbackClass != null)
                    return false;
            } else if (!callbackClass.equals(other.callbackClass))
                return false;
        }

        return true;
    }
    
    public List<Operation> getOperations() {
    	if( !isUnresolved() && isAsyncServer() ) {
    		return equivalentSyncOperations();
    	} else {
    		return super.getOperations();
    	}
    } // end method getOperations
    

    private List<Operation> syncOperations = null;
    private List<Operation> equivalentSyncOperations() {
    	if( syncOperations != null ) return syncOperations;
    	List<Operation> allOperations = super.getOperations();
    	syncOperations = new ArrayList<Operation>();
    	for( Operation operation: allOperations) {
    		syncOperations.add( getSyncFormOfOperation( (JavaOperation) operation ) );
    	// Store the actual async operations under the attribute "ASYNC-SERVER-OPERATIONS"
    	this.getAttributes().put("ASYNC-SERVER-OPERATIONS", allOperations);
    	} // end for
    	
    	return syncOperations;
    } // end method equivalentSyncOperations
    
    private static final String UNKNOWN_DATABINDING = null;
    /**
     * Prepares the synchronous form of an asynchronous operation
     * - async form:      void someOperationAsync( FooType inputParam, DispatchResponse<BarType> )
     * - sync form:       BarType someOperation( FooType inputParam )
     * @param operation - the operation to convert
     * @return - the synchronous form of the operation - for an input operation that is not async server in form, this 
     *           method simply returns the original operation unchanged
     */
    private Operation getSyncFormOfOperation( JavaOperation operation ) {
    	if( isAsyncServerOperation( operation ) ) {
            JavaOperation syncOperation = new JavaOperationImpl();
            String opName = operation.getName().substring(0, operation.getName().length() - 5 );
        	
            // Prepare the list of equivalent input parameters, which simply excludes the (final) DispatchResponse object
            // and the equivalent return parameter, which is the (generic) type from the DispatchResponse object
            DataType<List<DataType>> requestParams = operation.getInputType();

        	DataType<List<DataType>> inputType = prepareSyncInputParams( requestParams );
            DataType<XMLType> returnDataType = prepareSyncReturnParam( requestParams );
            List<DataType> faultDataTypes = prepareSyncFaults( operation );
        	
        	syncOperation.setName(opName);
        	syncOperation.setAsyncServer(true);
            syncOperation.setInputType(inputType);
            syncOperation.setOutputType(returnDataType);
            syncOperation.setFaultTypes(faultDataTypes);
            syncOperation.setNonBlocking(operation.isNonBlocking());
            syncOperation.setJavaMethod(operation.getJavaMethod());
            syncOperation.setInterface(this);
    		return syncOperation;
    	} else {
    		// If it's not Async form, then it's a synchronous operation
    		return operation;
    	} // end if
    } // end getSyncFormOfOperation
    
    /**
     * Produce the equivalent sync method input parameters from the input parameters of the async method
     * @param requestParams - async method input parameters
     * @return - the equivalent sync method input parameters
     */
    private DataType<List<DataType>> prepareSyncInputParams( DataType<List<DataType>> requestParams ) {
        List<DataType> requestLogical = requestParams.getLogical();
    	int paramCount = requestLogical.size();
    	
    	// Copy the list of async parameters, removing the final DispatchResponse
    	List<DataType> asyncParams = new ArrayList<DataType>( paramCount - 1);
    	for( int i = 0 ; i < (paramCount - 1) ; i++ ) {
    		asyncParams.add( requestLogical.get(i) );
    	} // end for
    	
    	DataType<List<DataType>> inputType =
            new DataTypeImpl<List<DataType>>(requestParams.getDataBinding(),
            		                         requestParams.getPhysical(), asyncParams);
    	return inputType;
    } // end method prepareSyncInputParams
    
    /**
     * Prepare the return data type of the equivalent sync operation, based on the parameterization of the ResponseDispatch object
     * of the async operation - the return data type is the Generic type of the final DispatchResponse<?>
     * @param requestParams - - async method input parameters
     * @return - the sync method return parameter
     */
    private DataType<XMLType> prepareSyncReturnParam( DataType<List<DataType>> requestParams ) {
    	List<DataType> requestLogical = requestParams.getLogical();
    	int paramCount = requestLogical.size();
    	
    	DataType<?> finalParam = requestLogical.get( paramCount - 1 );
    	ParameterizedType t = (ParameterizedType)finalParam.getGenericType();
    	XMLType returnXMLType = (XMLType)finalParam.getLogical();
    	
    	String namespace = null;
    	if( returnXMLType.isElement() ) {
    		namespace = returnXMLType.getElementName().getNamespaceURI();
    	} else {
    		namespace = returnXMLType.getTypeName().getNamespaceURI();
    	}
    	
    	Type[] typeArgs = t.getActualTypeArguments();
    	if( typeArgs.length != 1 ) throw new IllegalArgumentException( "ResponseDispatch parameter is not parameterized correctly");
    	
    	Class<?> returnType = (Class<?>)typeArgs[0];
        
    	// Set outputType to null for void
        XMLType xmlReturnType = new XMLType(new QName(namespace, "return"), null);
        DataType<XMLType> returnDataType =
            returnType == void.class ? null : new DataTypeImpl<XMLType>(UNKNOWN_DATABINDING, returnType, xmlReturnType);
        
        return returnDataType;
    } // end method prepareSyncReturnParam
    
    /**
     * Prepare the set of equivalent sync faults for a given async operation
     * @return - the list of faults
     */
    private List<DataType> prepareSyncFaults( JavaOperation operation ) {
    	//TODO - deal with Faults - for now just copy through whatever is associated with the async operation
    	return operation.getFaultTypes();
    }
    
    /**
     * Determines if an interface operation has the form of an async server operation
     * - async form:      void someOperationAsync( FooType inputParam, ...., DispatchResponse<BarType> )
     * @param operation - the operation to examine
     * @return - true if the operation has the form of an async operation, false otherwise
     */
    private boolean isAsyncServerOperation( Operation operation ) {
    	// Async form operations have:
    	// 1) void return type
    	// 2) name ending in "Async"
    	// 3) final parameter which is of ResponseDispatch<?> type
    	DataType<?> response = operation.getOutputType();
    	if( response != null ) {
    	   if ( response.getPhysical() != void.class ) return false;
    	} // end if
    	
    	if ( !operation.getName().endsWith("Async") ) return false;
    	
    	DataType<List<DataType>> requestParams = operation.getInputType();
    	int paramCount = requestParams.getLogical().size();
    	if( paramCount < 1 ) return false;
    	DataType<?> finalParam = requestParams.getLogical().get( paramCount - 1 );
    	if ( finalParam.getPhysical() != ResponseDispatch.class ) return false;
    	
    	return true;
    } // end method isAsyncServerOperation
    
    static QName ASYNC_INVOCATION = new QName(Constants.SCA11_NS, "asyncInvocation");
    /**
     * Indicates if this interface is an Async Server interface
     * @return true if the interface is Async Server, false otherwise
     */
    private boolean isAsyncServer() {
    	
    	List<Intent> intents = getRequiredIntents();
    	for( Intent intent: intents ) {
    		if ( intent.getName().equals(ASYNC_INVOCATION) ) {
    			return true;
    		}
    	} // end for
    	return false;
    } // end method isAsyncServer
    
    public String getJAXWSWSDLLocation() {
        return jaxwsWSDLLocation;
    }
    
    public void setJAXWSWSDLLocation(String wsdlLocation) {
        this.jaxwsWSDLLocation = wsdlLocation;
    }
    
    public String getJAXWSJavaInterfaceName() {
        return jaxwsJavaInterfaceName;
    }
    
    public void setJAXWSJavaInterfaceName(String javaInterfaceName) {
        this.jaxwsJavaInterfaceName = javaInterfaceName;
    }
    
    /**
     * A Java class may reference a WSDL file via a JAXWS annotation. We need to resolve
     * the WSDL file location in the context of the same contribution that holds the 
     * Java file. In order to do this we need to remember the actual contribution that
     * was used to resolve a Java class. 
     * 
     * @return
     */
    public Contribution getContributionContainingClass() {
        return contributionContainingClass;
    }
    
    public void setContributionContainingClass(Contribution contributionContainingClass) {
        this.contributionContainingClass = contributionContainingClass;
    }
}
