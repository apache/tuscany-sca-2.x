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
package org.apache.tuscany.sca.databinding.impl;

import static org.apache.tuscany.sca.databinding.DataBinding.IDL_FAULT;
import static org.apache.tuscany.sca.databinding.DataBinding.IDL_OUTPUT;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.databinding.DataBinding;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.DataPipe;
import org.apache.tuscany.sca.databinding.DataPipeTransformer;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.PushTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.Transformer;
import org.apache.tuscany.sca.databinding.TransformerExtensionPoint;
import org.apache.tuscany.sca.databinding.javabeans.JavaBeansDataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.FaultExceptionMapper;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.oasisopen.sca.ServiceRuntimeException;


/**
 * Default Mediator implementation
 *
 * @version $Rev$ $Date$
 */
public class MediatorImpl implements Mediator {
    private static final String TARGET_OPERATION = "target.operation";
    private static final String SOURCE_OPERATION = "source.operation";
    private ExtensionPointRegistry registry;
    private DataBindingExtensionPoint dataBindings;
    private TransformerExtensionPoint transformers;
    private FaultExceptionMapper faultExceptionMapper;

    MediatorImpl(DataBindingExtensionPoint dataBindings, TransformerExtensionPoint transformers) {
        this.dataBindings = dataBindings;
        this.transformers = transformers;
    }

    public MediatorImpl(ExtensionPointRegistry registry) {
        this.registry = registry;
        this.dataBindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        this.transformers = registry.getExtensionPoint(TransformerExtensionPoint.class);
        this.faultExceptionMapper =
            registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(FaultExceptionMapper.class);

    }

    @SuppressWarnings("unchecked")
    public Object mediate(Object source, DataType sourceDataType, DataType targetDataType, Map<String, Object> metadata) {
        if (sourceDataType == null || sourceDataType.getDataBinding() == null) {
            if (source != null) {
                Operation operation = (Operation)metadata.get(SOURCE_OPERATION);
                sourceDataType = dataBindings.introspectType(source, operation);
            }
        }
        if (sourceDataType == null || targetDataType == null) {
            return source;
        } else if (sourceDataType.equals(targetDataType)) {
            return source;
        }

        List<Transformer> path = getTransformerChain(sourceDataType, targetDataType);

        Object result = source;
        int size = path.size();
        int i = 0;
        while (i < size) {
            Transformer transformer = path.get(i);
            TransformationContext context =
                createTransformationContext(sourceDataType, targetDataType, size, i, transformer, metadata);
            // the source and target type
            if (transformer instanceof PullTransformer) {
                // For intermediate node, set data type to null
                result = ((PullTransformer)transformer).transform(result, context);
            } else if (transformer instanceof PushTransformer) {
                DataPipeTransformer dataPipeFactory = (i < size - 1) ? (DataPipeTransformer)path.get(++i) : null;
                DataPipe dataPipe = dataPipeFactory == null ? null : dataPipeFactory.newInstance();
                ((PushTransformer)transformer).transform(result, dataPipe.getSink(), context);
                result = dataPipe.getResult();
            }
            i++;
        }

        return result;
    }

    private TransformationContext createTransformationContext(DataType sourceDataType,
                                                              DataType targetDataType,
                                                              int size,
                                                              int index,
                                                              Transformer transformer,
                                                              Map<String, Object> metadata) {
        DataType sourceType =
            (index == 0) ? sourceDataType : new DataTypeImpl<Object>(transformer.getSourceDataBinding(), Object.class,
                                                                     sourceDataType.getLogical());
        DataType targetType =
            (index == size - 1) ? targetDataType : new DataTypeImpl<Object>(transformer.getTargetDataBinding(),
                                                                            Object.class, targetDataType.getLogical());

         Map<String, Object> copy = new HashMap<String, Object>();
        if (metadata != null) {
            copy.putAll(metadata);
        }
        copy.put(ExtensionPointRegistry.class.getName(), registry);

        TransformationContext context = new TransformationContextImpl(sourceType, targetType, copy);
        return context;
    }

    @SuppressWarnings("unchecked")
    public void mediate(Object source,
                        Object target,
                        DataType sourceDataType,
                        DataType targetDataType,
                        Map<String, Object> metadata) {
        if (source == null) {
            // Shortcut for null value
            return;
        }
        if (sourceDataType == null || sourceDataType.getDataBinding() == null) {
            Operation operation = (Operation)metadata.get(SOURCE_OPERATION);
            sourceDataType = dataBindings.introspectType(source, operation);
        }
        if (sourceDataType == null) {
            return;
        } else if (sourceDataType.equals(targetDataType)) {
            return;
        }

        List<Transformer> path = getTransformerChain(sourceDataType, targetDataType);
        Object result = source;
        int size = path.size();
        for (int i = 0; i < size; i++) {
            Transformer transformer = path.get(i);
            TransformationContext context =
                createTransformationContext(sourceDataType, targetDataType, size, i, transformer, metadata);

            if (transformer instanceof PullTransformer) {
                result = ((PullTransformer)transformer).transform(result, context);
            } else if (transformer instanceof PushTransformer) {
                DataPipeTransformer dataPipeFactory = (i < size - 1) ? (DataPipeTransformer)path.get(++i) : null;
                DataPipe dataPipe = dataPipeFactory == null ? null : dataPipeFactory.newInstance();
                Object sink = dataPipe != null ? dataPipe.getSink() : target;
                ((PushTransformer)transformer).transform(result, sink, context);
                result = (dataPipe != null) ? dataPipe.getResult() : null;
            }
        }
    }

    private List<Transformer> getTransformerChain(DataType sourceDataType, DataType targetDataType) {
        String sourceId = sourceDataType.getDataBinding();
        String targetId = targetDataType.getDataBinding();
        List<Transformer> path = transformers.getTransformerChain(sourceId, targetId);
        if (path == null) {
            TransformationException ex =
                new TransformationException("No path found for the transformation: " + sourceId + "->" + targetId);
            ex.setSourceDataBinding(sourceId);
            ex.setTargetDataBinding(targetId);
            throw ex;
        }
        return path;
    }

    public DataBindingExtensionPoint getDataBindings() {
        return dataBindings;
    }

    public TransformerExtensionPoint getTransformers() {
        return transformers;
    }

    private DataType getFaultType(DataType exceptionType) {
        return exceptionType == null ? null : (DataType)exceptionType.getLogical();
    }

    /**
     * @param qn1
     * @param qn2
     */
    private boolean matches(QName qn1, QName qn2) {
        if (qn1 == qn2) {
            return true;
        }
        if (qn1 == null || qn2 == null) {
            return false;
        }
        String ns1 = qn1.getNamespaceURI();
        String ns2 = qn2.getNamespaceURI();
        String e1 = qn1.getLocalPart();
        String e2 = qn2.getLocalPart();
        if (e1.equals(e2) && (ns1.equals(ns2) || ns1.equals(ns2 + "/") || ns2.equals(ns1 + "/"))) {
            // Tolerating the trailing / which is required by JAX-WS java package --> xml ns mapping
            return true;
        }
        return false;
    }

    /**
     * @param source The source exception
     * @param sourceExType The data type for the source exception
     * @param targetExType The data type for the target exception
     * @param sourceType The fault type for the source
     * @param targetType The fault type for the target
     * @return
     */
    private Object transformException(Object source,
                                      DataType sourceExType,
                                      DataType targetExType,
                                      DataType sourceType,
                                      DataType targetType,
                                      Map<String, Object> metadata) {

        if (sourceType == targetType || (sourceType != null && sourceType.equals(targetType))) {
            return source;
        }

        DataType<DataType> eSourceDataType =
            new DataTypeImpl<DataType>(IDL_FAULT, sourceExType.getPhysical(), sourceType);
        DataType<DataType> eTargetDataType =
            new DataTypeImpl<DataType>(IDL_FAULT, targetExType.getPhysical(), targetType);

        return mediate(source, eSourceDataType, eTargetDataType, metadata);
    }

    //
    // Assumes we're going from target->source, knowing that we're throwing BACK an exception, rather than the more
    // obvious source->target
    //
    public Object mediateFault(Object result,
                               Operation sourceOperation,
                               Operation targetOperation,
                               Map<String, Object> metadata) {

        // FIXME: How to match fault data to a fault type for the
        // operation?

        // If the result is from an InvocationTargetException look at
        // the actual cause.
        if (result instanceof InvocationTargetException) {
            result = ((InvocationTargetException)result).getCause();
        }
        DataType targetDataType = null;
        for (DataType exType : targetOperation.getFaultTypes()) {
            if (((Class)exType.getPhysical()).isInstance(result)) {
                if (result instanceof FaultException) {
                    DataType faultType = (DataType)exType.getLogical();
                    if (((FaultException)result).isMatchingType(faultType.getLogical())) {
                        targetDataType = exType;
                        break;
                    }
                } else {
                    targetDataType = exType;
                    break;
                }
            }
        }

        /*
        if (targetDataType == null) {
            // Not a business exception
            return resultMsg;
        }
        */

        DataType targetFaultType = getFaultType(targetDataType);
        if (targetFaultType == null) {
            // No matching fault type, it's a system exception
            Throwable cause = (Throwable)result;
            throw new ServiceRuntimeException(cause);
        }

        // FIXME: How to match a source fault type to a target fault
        // type?
        DataType sourceDataType = null;
        DataType sourceFaultType = null;
        for (DataType exType : sourceOperation.getFaultTypes()) {
            DataType faultType = getFaultType(exType);
            // Match by the QName (XSD element) of the fault type
            if (faultType != null && typesMatch(targetFaultType.getLogical(), faultType.getLogical())) {
                sourceDataType = exType;
                sourceFaultType = faultType;
                break;
            }
        }

        if (sourceFaultType == null) {
            // No matching fault type, it's a system exception
            Throwable cause = (Throwable)result;
            throw new ServiceRuntimeException(cause);
        }

        Map<String, Object> context = new HashMap<String, Object>();
        if (metadata != null) {
            context.putAll(metadata);
        }
        if (targetOperation != null) {
            context.put(SOURCE_OPERATION, targetOperation);
        }
        if (sourceOperation != null) {
            context.put(TARGET_OPERATION, sourceOperation);
        }

        Object newResult =
            transformException(result, targetDataType, sourceDataType, targetFaultType, sourceFaultType, context);

        return newResult;

    }

    private boolean typesMatch(Object first, Object second) {
        if (first.equals(second)) {
            return true;
        }
        if (first instanceof XMLType && second instanceof XMLType) {
            XMLType t1 = (XMLType)first;
            XMLType t2 = (XMLType)second;
            // TUSCANY-2113, we should compare element names only
            return matches(t1.getElementName(), t2.getElementName());
        }
        return false;
    }

    /**
     * Assumes we're going from target-to-source, knowing that we're sending BACK an output response, rather than the more
     * obvious source-to-target.
     *
     * @param output
     * @param sourceOperation
     * @param targetOperation
     * @return
     */
    public Object mediateOutput(Object output,
                                Operation sourceOperation,
                                Operation targetOperation,
                                Map<String, Object> metadata) {
        // Create a data type to represent the ouput produced by the target operation
        DataType<DataType> targetType =
            new DataTypeImpl<DataType>(IDL_OUTPUT, Object.class, targetOperation.getOutputType());

        // Create a data type to represent the ouput expected by the source operation
        DataType<DataType> sourceType =
            new DataTypeImpl<DataType>(IDL_OUTPUT, Object.class, sourceOperation.getOutputType());

        if (sourceType == targetType || (sourceType != null && sourceType.equals(targetType))) {
            return output;
        }
        Map<String, Object> context = new HashMap<String, Object>();
        if (metadata != null) {
            context.putAll(metadata);
        }
        if (targetOperation != null) {
            context.put(SOURCE_OPERATION, targetOperation);
        }
        if (sourceOperation != null) {
            context.put(TARGET_OPERATION, sourceOperation);
        }
        return mediate(output, targetType, sourceType, context);
    }

    public Object mediateInput(Object input,
                               Operation sourceOperation,
                               Operation targetOperation,
                               Map<String, Object> metadata) {
        // Get the data type to represent the input passed in by the source operation
        DataType sourceType = sourceOperation.getInputType();

        // Get the data type to represent the input expected by the target operation
        DataType targetType = targetOperation.getInputType();

        if (sourceType == targetType || (sourceType != null && sourceType.equals(targetType))) {
            return input;
        }
        Map<String, Object> context = new HashMap<String, Object>();
        if (metadata != null) {
            context.putAll(metadata);
        }
        if (sourceOperation != null) {
            context.put(SOURCE_OPERATION, sourceOperation);
        }
        if (targetOperation != null) {
            context.put(TARGET_OPERATION, targetOperation);
        }
        return mediate(input, sourceType, targetType, context);
    }

    public TransformationContext createTransformationContext() {
        return new TransformationContextImpl();
    }

    public TransformationContext createTransformationContext(DataType sourceDataType,
                                                             DataType targetDataType,
                                                             Map<String, Object> metadata) {
        return new TransformationContextImpl(sourceDataType, targetDataType, metadata);
    }

    public Object copy(Object data, DataType dataType) {
        return copy(data, dataType, dataType, null, null);
    }

    public Object copy(Object data, DataType sourceDataType, DataType targetDataType) {
        return copy(data, sourceDataType, targetDataType, null, null);
    }

    /**
     * Copy data using the specified databinding.
     * @param data input data
     * @param sourceDataType
     * @return a copy of the data
     */
    public Object copy(Object data,
                       DataType sourceDataType,
                       DataType targetDataType,
                       Operation sourceOperation,
                       Operation targetOperation) {
        if (data == null) {
            return null;
        }
        Class<?> clazz = data.getClass();
        if (String.class == clazz || clazz.isPrimitive()
            || Number.class.isAssignableFrom(clazz)
            || Boolean.class.isAssignableFrom(clazz)
            || Character.class.isAssignableFrom(clazz)
            || Byte.class.isAssignableFrom(clazz)
            || URI.class == clazz
            || UUID.class == clazz
            || QName.class == clazz) {
            // Immutable classes
            return data;
        }

        DataBinding javaBeansDataBinding = dataBindings.getDataBinding(JavaBeansDataBinding.NAME);
        // FIXME: The JAXB databinding is hard-coded here
        DataBinding jaxbDataBinding = dataBindings.getDataBinding("javax.xml.bind.JAXBElement");
        DataBinding dataBinding = dataBindings.getDataBinding(sourceDataType.getDataBinding());
        // If no databinding was specified, introspect the given arg to
        // determine its databinding
        if (dataBinding == null) {
            if (!"java:array".equals(sourceDataType.getDataBinding())) {
                sourceDataType = dataBindings.introspectType(data, sourceOperation);
                if (sourceDataType != null) {
                    String db = sourceDataType.getDataBinding();
                    dataBinding = dataBindings.getDataBinding(db);
                    if (dataBinding == null && db != null) {
                        return data;
                    }
                }
            }
            if (dataBinding == null) {

                // Default to the JavaBean databinding
                dataBinding = dataBindings.getDataBinding(JavaBeansDataBinding.NAME);
            }
        }

        // Use the JAXB databinding to copy non-Serializable data
        if (dataBinding == javaBeansDataBinding) {

            // If the input data is an array containing non Serializable elements
            // use JAXB
            clazz = data.getClass();
            if (clazz.isArray()) {
                if (Array.getLength(data) != 0) {
                    Object element = Array.get(data, 0);
                    if (element != null && !(element instanceof Serializable)) {
                        dataBinding = jaxbDataBinding;
                    }
                }
            } else {

                // If the input data is not Serializable use JAXB
                if (!((data instanceof Serializable) || (data instanceof Cloneable))) {
                    dataBinding = jaxbDataBinding;
                }
            }
        }

        if (dataBinding == null) {
            return data;
        }

        return dataBinding.copy(data, sourceDataType, targetDataType, sourceOperation, targetOperation);
    }

    /**
     * Copy an array of data objects passed to an operation
     * @param data array of objects to copy
     * @return the copy
     */
    public Object copyInput(Object input, Operation operation) {
        return copyInput(input, operation, null);
    }
    public Object copyInput(Object input, Operation operation, Operation targetOperation) {
        if (input == null) {
            return null;
        }
        Object[] data = (input instanceof Object[]) ? (Object[])input : new Object[] {input};
        List<DataType> inputTypes = operation.getInputType().getLogical();
        List<DataType> inputTypesTarget = targetOperation == null ? null : targetOperation.getInputType().getLogical();
        Object[] copy = new Object[data.length];
        Map<Object, Object> map = new IdentityHashMap<Object, Object>();
        for (int i = 0, size = inputTypes.size(); i < size; i++) {
            Object arg = data[i];
            if (arg == null) {
                copy[i] = null;
            } else {
                Object copiedArg = map.get(arg);
                if (copiedArg != null) {
                    copy[i] = copiedArg;
                } else {
                    copiedArg = copy(arg, inputTypes.get(i), inputTypesTarget == null ? null : inputTypesTarget.get(i));
                    map.put(arg, copiedArg);
                    copy[i] = copiedArg;
                }
            }
        }
        return copy;
    }

    public Object copyOutput(Object data, Operation operation) {
        return copyOutput(data, operation, null);
    }
    public Object copyOutput(Object data, Operation operation, Operation targetOperation) {
        return copy(data, operation.getOutputType(), targetOperation.getOutputType(), operation, targetOperation);
    }

    public Object copyFault(Object fault, Operation operation) {
        return copyFault(fault, operation, null);
    }
    public Object copyFault(Object fault, Operation operation, Operation targetOperation) {
        if (faultExceptionMapper == null) {
            return fault;
        }
        List<DataType> fts = operation.getFaultTypes();
        for (int i=0; i<fts.size(); i++) {
            DataType et = fts.get(i); 
            if (et.getPhysical().isInstance(fault)) {
                Throwable ex = (Throwable)fault;
                DataType<DataType> exType =
                    new DataTypeImpl<DataType>(ex.getClass(), new DataTypeImpl<XMLType>(ex.getClass(), XMLType.UNKNOWN));
                faultExceptionMapper.introspectFaultDataType(exType, operation, false);
                DataType faultType = exType.getLogical();
                Object faultInfo = faultExceptionMapper.getFaultInfo(ex, faultType.getPhysical(), operation);
                DataType targetFaultType;
                try {
                    targetFaultType = (DataType)faultType.clone();
                } catch (CloneNotSupportedException e) {
                    throw new IllegalStateException(e);
                }
                targetFaultType.setPhysical(targetOperation.getFaultTypes().get(i).getPhysical());
                faultInfo = copy(faultInfo, faultType, targetFaultType);
                fault = faultExceptionMapper.wrapFaultInfo(exType, ex.getMessage(), faultInfo, ex.getCause(), operation);
                return fault;
            }
        }
        return fault;
    }

}
