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
package org.apache.tuscany.core.pipeline.impl;

import org.eclipse.emf.common.notify.impl.AdapterImpl;

import org.apache.tuscany.core.addressing.sdo.EndpointReferenceElement;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.handler.MessageHandler;


/**
 */
public class PipelineExtensionImplementationHandlerImpl extends AdapterImpl implements MessageHandler {

    /**
     * Constructor.
     */
    public PipelineExtensionImplementationHandlerImpl() {
        super();
    }

    /**
     * @see org.apache.tuscany.core.message.handler.MessageHandler#processMessage(org.apache.tuscany.core.client.runtime.message.sdo.Message)
     */
    public boolean processMessage(Message message) {

        return false;

        /*
          // Get the endpoint reference of the target service and the service model element
          EndpointReferenceElement endpointReference=message.getEndpointReference();
          TConfiguredPort configuredPort=endpointReference.getTConfiguredPort();

          // Return immediately if the target is not a service
          if (!(configuredPort instanceof TConfiguredService))
              return;

          // Return immediately if the target is not an extension component
          TConfiguredService configuredService=(TConfiguredService)configuredPort;
          final TPart part=configuredService.getTPart();
          if (!(part instanceof TComponent))
              return;
          TComponent component=(TComponent)part;
          final TConfiguredImplementation implementation=component.getTConfiguredImplementation();
          if (!(implementation instanceof ExtensionImplementation))
              return;

          // Create action, return a service reference for the specified extension
          if (message.getAction()==ServiceResourcePackage.CREATE_ACTION_URI) {
              CoreModuleContext moduleContext=(CoreModuleContext)message.getModuleContext();

              // The extension message handler is cached in the implementation model, get it from there
              MessageHandler extensionMessageHandler=(MessageHandler)implementation.getRuntimeRepresentation();
              if (extensionMessageHandler==null) {

                  // Start the component
                  final CoreComponentContext componentContext=moduleContext.startComponent((org.apache.tuscany.model.assembly.TComponent)component, message);

                  // Load the component implementation class. The component implementation class must
                  // implement the MessageHandler interface
                  try {

                      final ResourceLoader bundleContext=moduleContext.getResourceLoader();
                      final String className=((ExtensionImplementation)implementation).getClass_();
                      Class extensionClass;
                      try {
                          // SECURITY
                          extensionClass = (Class) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                              public Object run() throws ClassNotFoundException {
                                  return bundleContext.loadClass(className);
                              }
                          });
                      } catch (PrivilegedActionException e1) {
                          throw new ServiceRuntimeException(e1.getException());
                      }

                      // Create a new instance of the extension implementation class
                      Object instance;
                      try {
                          instance = extensionClass.newInstance();
                      } catch (InstantiationException e1) {
                          throw new ServiceRuntimeException(e1);
                      } catch (IllegalAccessException e1) {
                          throw new ServiceRuntimeException(e1);
                      }

                      // Cache it in the implementation model object
                      implementation.setRuntimeRepresentation(instance);

                      // Inject the reference properties
                      for (Iterator i=component.getTConfiguredReferences().iterator(); i.hasNext(); ) {
                          Map.Entry entry=(Map.Entry)i.next();
                          String referenceName=(String)entry.getKey();
                          TConfiguredReference referenceValue=(TConfiguredReference)entry.getValue();

                          // Look for a setter method for each reference
                          String propertyName;
                          if (referenceName.length()==1)
                              propertyName="set"+Character.toUpperCase(referenceName.charAt(0));
                          else
                              propertyName="set"+Character.toUpperCase(referenceName.charAt(0))+referenceName.substring(1);
                          Method setter;
                          try {
                              if (referenceValue.getTReference().isMultiplicityN()) {
                                  setter=extensionClass.getMethod(propertyName, SERVICE_REFERENCE_ARRAY_PARAMETER);

                                  // Set a list of service endpoint references, convert the list to an array
                                  List eprs=componentContext.getEndpointReferences(referenceName);
                                  int n=eprs.size();
                                  EndpointReferenceElement[] array=new EndpointReferenceElement[n];
                                  for (int h=0; h<n; h++) {
                                      array[h]=(EndpointReferenceElement)eprs.get(h);

                                      // We want endpoint references to be resolved right away here
                                      array[h].getMessageHandler();
                                  }

                                  // Invoke the setter
                                  setter.invoke(instance, new Object[]{array});

                              } else {

                                  // Set a single endpoint reference
                                  EndpointReferenceElement epr=componentContext.getEndpointReference(referenceName);

                                  // We want the endpoint reference to be resolved right away here
                                  epr.getMessageHandler();

                                  // Invoke the setter, set a single service endpoint reference
                                  setter=extensionClass.getMethod(propertyName, SERVICE_REFERENCE_PARAMETER);
                                  setter.invoke(instance, new Object[]{epr});
                              }
                          } catch (NoSuchMethodException e) {
                              continue;
                          } catch (IllegalArgumentException e) {
                              throw new ServiceRuntimeException(e);
                          } catch (IllegalAccessException e) {
                              throw new ServiceRuntimeException(e);
                          } catch (InvocationTargetException e) {
                              throw new ServiceRuntimeException(e);
                          }
                      }

                  } finally {

                      // Stop the component
                      componentContext.stop();
                  }
              }

              // Create an endpoint reference for the extension component
              EndpointReferenceElement newEndpointReference=(EndpointReferenceElement)AddressingElementFactory.eINSTANCE.createEndpointReference();
              newEndpointReference.setModuleContext(moduleContext);
              newEndpointReference.setAddress(endpointReference.getAddress());

              // Set the extension message handler on the service reference
              newEndpointReference.setMessageHandler(extensionMessageHandler);
              newEndpointReference.setServiceImplementationResource(extensionMessageHandler);

              // Set the service model object on the service reference
              newEndpointReference.setTConfiguredPort(configuredService);

              // Create the response message
              message.setRelatesTo(message.getMessageID());
              message.setMessageID(EcoreUtil.generateUUID());

              // Return the new service reference in the body of the message
              CreateResponseType createResponse=ServiceResourceFactory.eINSTANCE.createCreateResponseType();
              createResponse.setResourceCreated(newEndpointReference);
              message.setBody(createResponse);

              // Send the response message
              EndpointReferenceElement replyTo=message.getReplyTo();
              message.setEndpointReference(replyTo);
              MessageChannel callbackChannel=message.getCallbackChannel();
              callbackChannel.send(message);
              return;
          }
          */
    }

}
