/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.apache.tuscany.model.assembly.scdl;

import commonj.sdo.Sequence;

import java.util.Map;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Document Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getBinding <em>Binding</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getBindingSca <em>Binding Sca</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getBindingWs <em>Binding Ws</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getComponentType <em>Component Type</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getImplementation <em>Implementation</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getImplementationJava <em>Implementation Java</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getInterface <em>Interface</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getInterfaceJava <em>Interface Java</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getInterfaceWsdl <em>Interface Wsdl</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getModule <em>Module</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getModuleFragment <em>Module Fragment</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getSource <em>Source</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getSourceEpr <em>Source Epr</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getSourceUri <em>Source Uri</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getSubsystem <em>Subsystem</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getTarget <em>Target</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getTargetEpr <em>Target Epr</em>}</li>
 *   <li>{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getTargetUri <em>Target Uri</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public interface DocumentRoot
{
  /**
   * Returns the value of the '<em><b>Mixed</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Mixed</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Mixed</em>' attribute list.
   * @generated
   */
  Sequence getMixed();

  /**
   * Returns the value of the '<em><b>XMLNS Prefix Map</b></em>' map.
   * The key is of type {@link java.lang.String},
   * and the value is of type {@link java.lang.String},
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>XMLNS Prefix Map</em>' map isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>XMLNS Prefix Map</em>' map.
   * @generated
   */
  Map getXMLNSPrefixMap();

  /**
   * Returns the value of the '<em><b>XSI Schema Location</b></em>' map.
   * The key is of type {@link java.lang.String},
   * and the value is of type {@link java.lang.String},
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>XSI Schema Location</em>' map isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>XSI Schema Location</em>' map.
   * @generated
   */
  Map getXSISchemaLocation();

  /**
   * Returns the value of the '<em><b>Binding</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Binding</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Binding</em>' containment reference.
   * @see #setBinding(Binding)
   * @generated
   */
  Binding getBinding();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getBinding <em>Binding</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Binding</em>' containment reference.
   * @see #getBinding()
   * @generated
   */
  void setBinding(Binding value);

  /**
   * Returns the value of the '<em><b>Binding Sca</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Binding Sca</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Binding Sca</em>' containment reference.
   * @see #setBindingSca(SCABinding)
   * @generated
   */
  SCABinding getBindingSca();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getBindingSca <em>Binding Sca</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Binding Sca</em>' containment reference.
   * @see #getBindingSca()
   * @generated
   */
  void setBindingSca(SCABinding value);

  /**
   * Returns the value of the '<em><b>Binding Ws</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Binding Ws</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Binding Ws</em>' containment reference.
   * @see #setBindingWs(WebServiceBinding)
   * @generated
   */
  WebServiceBinding getBindingWs();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getBindingWs <em>Binding Ws</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Binding Ws</em>' containment reference.
   * @see #getBindingWs()
   * @generated
   */
  void setBindingWs(WebServiceBinding value);

  /**
   * Returns the value of the '<em><b>Component Type</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Component Type</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Component Type</em>' containment reference.
   * @see #setComponentType(ComponentType)
   * @generated
   */
  ComponentType getComponentType();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getComponentType <em>Component Type</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Component Type</em>' containment reference.
   * @see #getComponentType()
   * @generated
   */
  void setComponentType(ComponentType value);

  /**
   * Returns the value of the '<em><b>Implementation</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Implementation</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Implementation</em>' containment reference.
   * @see #setImplementation(Implementation)
   * @generated
   */
  Implementation getImplementation();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getImplementation <em>Implementation</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Implementation</em>' containment reference.
   * @see #getImplementation()
   * @generated
   */
  void setImplementation(Implementation value);

  /**
   * Returns the value of the '<em><b>Implementation Java</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Implementation Java</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Implementation Java</em>' containment reference.
   * @see #setImplementationJava(JavaImplementation)
   * @generated
   */
  JavaImplementation getImplementationJava();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getImplementationJava <em>Implementation Java</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Implementation Java</em>' containment reference.
   * @see #getImplementationJava()
   * @generated
   */
  void setImplementationJava(JavaImplementation value);

  /**
   * Returns the value of the '<em><b>Interface</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Interface</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Interface</em>' containment reference.
   * @see #setInterface(Interface)
   * @generated
   */
  Interface getInterface();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getInterface <em>Interface</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Interface</em>' containment reference.
   * @see #getInterface()
   * @generated
   */
  void setInterface(Interface value);

  /**
   * Returns the value of the '<em><b>Interface Java</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Interface Java</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Interface Java</em>' containment reference.
   * @see #setInterfaceJava(JavaInterface)
   * @generated
   */
  JavaInterface getInterfaceJava();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getInterfaceJava <em>Interface Java</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Interface Java</em>' containment reference.
   * @see #getInterfaceJava()
   * @generated
   */
  void setInterfaceJava(JavaInterface value);

  /**
   * Returns the value of the '<em><b>Interface Wsdl</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Interface Wsdl</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Interface Wsdl</em>' containment reference.
   * @see #setInterfaceWsdl(WSDLPortType)
   * @generated
   */
  WSDLPortType getInterfaceWsdl();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getInterfaceWsdl <em>Interface Wsdl</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Interface Wsdl</em>' containment reference.
   * @see #getInterfaceWsdl()
   * @generated
   */
  void setInterfaceWsdl(WSDLPortType value);

  /**
   * Returns the value of the '<em><b>Module</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Module</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Module</em>' containment reference.
   * @see #setModule(Module)
   * @generated
   */
  Module getModule();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getModule <em>Module</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Module</em>' containment reference.
   * @see #getModule()
   * @generated
   */
  void setModule(Module value);

  /**
   * Returns the value of the '<em><b>Module Fragment</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Module Fragment</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Module Fragment</em>' containment reference.
   * @see #setModuleFragment(ModuleFragment)
   * @generated
   */
  ModuleFragment getModuleFragment();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getModuleFragment <em>Module Fragment</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Module Fragment</em>' containment reference.
   * @see #getModuleFragment()
   * @generated
   */
  void setModuleFragment(ModuleFragment value);

  /**
   * Returns the value of the '<em><b>Source</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Source</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source</em>' containment reference.
   * @see #setSource(Object)
   * @generated
   */
  Object getSource();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getSource <em>Source</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Source</em>' containment reference.
   * @see #getSource()
   * @generated
   */
  void setSource(Object value);

  /**
   * Returns the value of the '<em><b>Source Epr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Source Epr</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source Epr</em>' containment reference.
   * @see #setSourceEpr(Object)
   * @generated
   */
  Object getSourceEpr();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getSourceEpr <em>Source Epr</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Source Epr</em>' containment reference.
   * @see #getSourceEpr()
   * @generated
   */
  void setSourceEpr(Object value);

  /**
   * Returns the value of the '<em><b>Source Uri</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Source Uri</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source Uri</em>' attribute.
   * @see #setSourceUri(String)
   * @generated
   */
  String getSourceUri();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getSourceUri <em>Source Uri</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Source Uri</em>' attribute.
   * @see #getSourceUri()
   * @generated
   */
  void setSourceUri(String value);

  /**
   * Returns the value of the '<em><b>Subsystem</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Subsystem</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Subsystem</em>' containment reference.
   * @see #setSubsystem(Subsystem)
   * @generated
   */
  Subsystem getSubsystem();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getSubsystem <em>Subsystem</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Subsystem</em>' containment reference.
   * @see #getSubsystem()
   * @generated
   */
  void setSubsystem(Subsystem value);

  /**
   * Returns the value of the '<em><b>Target</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Target</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Target</em>' containment reference.
   * @see #setTarget(Object)
   * @generated
   */
  Object getTarget();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getTarget <em>Target</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Target</em>' containment reference.
   * @see #getTarget()
   * @generated
   */
  void setTarget(Object value);

  /**
   * Returns the value of the '<em><b>Target Epr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Target Epr</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Target Epr</em>' containment reference.
   * @see #setTargetEpr(Object)
   * @generated
   */
  Object getTargetEpr();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getTargetEpr <em>Target Epr</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Target Epr</em>' containment reference.
   * @see #getTargetEpr()
   * @generated
   */
  void setTargetEpr(Object value);

  /**
   * Returns the value of the '<em><b>Target Uri</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Target Uri</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Target Uri</em>' attribute.
   * @see #setTargetUri(String)
   * @generated
   */
  String getTargetUri();

  /**
   * Sets the value of the '{@link org.apache.tuscany.model.assembly.scdl.DocumentRoot#getTargetUri <em>Target Uri</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Target Uri</em>' attribute.
   * @see #getTargetUri()
   * @generated
   */
  void setTargetUri(String value);

} // DocumentRoot
