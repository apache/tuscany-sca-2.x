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
/**
 * @version $Rev$ $Date$
 */

package org.apache.tuscany.sca.host.corba.naming;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.BindingTypeHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExtPOA;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.NotFoundReason;
import org.omg.PortableServer.POA;

public abstract class NamingContextBase extends NamingContextExtPOA {
    // the real logger backing instance.  We use the interface class as the locator
    protected static final Logger logger = Logger.getLogger(NamingContext.class.getName());

    // set of URL characters that don't require escaping when encoded.
    protected final String nonEscaped = ";/?:@&=+$;-_.!~* ()";
    // the orb we're attached to
    protected ORB orb;
    // the poa we're associated with
    protected POA poa;

    /**
     * Create a new base NamingContext (super class constructor
     * for the derived classes).
     *
     * @param orb    The ORB this is hosted on.
     *
     * @exception java.lang.Exception
     */
    public NamingContextBase(ORB orb, POA poa) throws java.lang.Exception {
        super();
        this.orb = orb;
        this.poa = poa;
    }

    /**
     * Bind an object to a given name.
     *
     * @param n      An array of NameComponents that are the target name.
     *               The last element in the array is binding name for the
     *               object.  The remainder of the array is the path
     *               for resolving the naming context, relative to the
     *               current context.  All path contexts must already be
     *               bound in the context tree.
     * @param obj    The object to be bound.
     *
     * @exception org.omg.CosNaming.NamingContextPackage.NotFound
     * @exception org.omg.CosNaming.NamingContextPackage.CannotProceed
     * @exception org.omg.CosNaming.NamingContextPackage.InvalidName
     * @exception org.omg.CosNaming.NamingContextPackage.AlreadyBound
     */
    public void bind(org.omg.CosNaming.NameComponent[] n, org.omg.CORBA.Object obj)
        throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed,
        org.omg.CosNaming.NamingContextPackage.InvalidName, org.omg.CosNaming.NamingContextPackage.AlreadyBound {
        // perform various name validations
        validateName(n);

        logNameComponent("bind() name", n);

        // do we need to push through to a deeper naming context first?
        if (n.length > 1) {
            // resolve the top level name to a context, and have that context
            // resolve the rest.
            NamingContext context = resolveContext(n[0]);
            NameComponent[] subName = extractSubName(n);

            // now pass this along to the next context for the real bind operation.
            context.bind(subName, obj);
        } else {
            NameComponent name = n[0];
            // we need the resolveObject() and bindObject() calls to be consistent, so
            // synchronize on this
            synchronized (this) {
                // see if we have this bound already...can't replace these.
                BindingTypeHolder type = new BindingTypeHolder();
                if (resolveObject(name, type) != null) {
                    throw new AlreadyBound();
                }
                type.value = BindingType.nobject;
                // ok, this is a new binding, go do it.
                bindObject(name, obj, type);
            }
        }
    }

    /**
     * Rebind an object to a given name.  If an object is
     * already bound with this name, the new object replaces
     * the bound object's value.  If no object has been
     * bound already, this is the same as a bind operation.
     *
     * @param n      An array of NameComponents that are the target name.
     *               The last element in the array is binding name for the
     *               object.  The remainder of the array is the path
     *               for resolving the naming context, relative to the
     *               current context.  All path contexts must already be
     *               bound in the context tree.
     * @param obj    The new value for this binding.
     *
     * @exception org.omg.CosNaming.NamingContextPackage.NotFound
     * @exception org.omg.CosNaming.NamingContextPackage.CannotProceed
     * @exception org.omg.CosNaming.NamingContextPackage.InvalidName
     * @exception org.omg.CosNaming.NamingContextPackage.AlreadyBound
     */
    public void rebind(org.omg.CosNaming.NameComponent[] n, org.omg.CORBA.Object obj)
        throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed,
        org.omg.CosNaming.NamingContextPackage.InvalidName {
        // perform various name validations
        validateName(n);

        logNameComponent("rebind() name", n);

        // do we need to push through to a deeper naming context first?
        if (n.length > 1) {
            // resolve the top level name to a context, and have that context
            // resolve the rest.
            NamingContext context = resolveContext(n[0]);
            NameComponent[] subName = extractSubName(n);

            // now pass this along to the next context for the real bind operation.
            context.rebind(subName, obj);
        } else {
            NameComponent name = n[0];
            // we need the resolveObject() and bindObject() calls to be consistent, so
            // synchronize on this
            synchronized (this) {
                // see if we have this bound already...can't replace these.
                BindingTypeHolder type = new BindingTypeHolder();
                // for a rebind, we must have an object, and it must be a real object
                if (resolveObject(name, type) != null) {
                    // it has to resolve to a real object.  If it is a naming context,
                    // then this is the wrong binding operation.
                    if (type.value.value() == BindingType._ncontext) {
                        throw new NotFound(NotFoundReason.not_object, n);
                    }
                    // safe to unbind
                    unbindObject(name);
                }
                type.value = BindingType.nobject;
                // now bind this object
                bindObject(name, obj, type);
            }
        }
    }

    /**
     * Bind a new context to a given name.
     *
     * @param n      An array of NameComponents that are the target name.
     *               The last element in the array is binding name for the
     *               object.  The remainder of the array is the path
     *               for resolving the naming context, relative to the
     *               current context.  All path contexts must already be
     *               bound in the context tree.
     * @param nc     The new naming context added to the tree.
     *
     * @exception org.omg.CosNaming.NamingContextPackage.NotFound
     * @exception org.omg.CosNaming.NamingContextPackage.CannotProceed
     * @exception org.omg.CosNaming.NamingContextPackage.InvalidName
     * @exception org.omg.CosNaming.NamingContextPackage.AlreadyBound
     */
    public void bind_context(org.omg.CosNaming.NameComponent[] n, org.omg.CosNaming.NamingContext nc)
        throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed,
        org.omg.CosNaming.NamingContextPackage.InvalidName, org.omg.CosNaming.NamingContextPackage.AlreadyBound {
        // perform various name validations
        validateName(n);

        logNameComponent("bind_context() name", n);

        // do we need to push through to a deeper naming context first?
        if (n.length > 1) {
            // resolve the top level name to a context, and have that context
            // resolve the rest.
            NamingContext context = resolveContext(n[0]);
            NameComponent[] subName = extractSubName(n);

            // now pass this along to the next context for the real bind operation.
            context.bind_context(subName, nc);
        } else {
            NameComponent name = n[0];
            // we need the resolveObject() and bindObject() calls to be consistent, so
            // synchronize on this
            synchronized (this) {
                // see if we have this bound already...can't replace these.
                BindingTypeHolder type = new BindingTypeHolder();
                if (resolveObject(name, type) != null) {
                    throw new AlreadyBound();
                }
                type.value = BindingType.ncontext;
                // ok, this is a new binding, go do it.
                bindObject(name, nc, type);
            }
        }
    }

    /**
     * Rebind a context to a given name.  If a context is
     * already bound with this name, the new context replaces
     * the existing context.  If no context has been
     * bound already, this is the same as a bind operation.
     *
     * @param n      An array of NameComponents that are the target name.
     *               The last element in the array is binding name for the
     *               object.  The remainder of the array is the path
     *               for resolving the naming context, relative to the
     *               current context.  All path contexts must already be
     *               bound in the context tree.
     * @param nc     The new context to be bound with the name.
     *
     * @exception org.omg.CosNaming.NamingContextPackage.NotFound
     * @exception org.omg.CosNaming.NamingContextPackage.CannotProceed
     * @exception org.omg.CosNaming.NamingContextPackage.InvalidName
     * @exception org.omg.CosNaming.NamingContextPackage.AlreadyBound
     */
    public void rebind_context(org.omg.CosNaming.NameComponent[] n, org.omg.CosNaming.NamingContext nc)
        throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed,
        org.omg.CosNaming.NamingContextPackage.InvalidName {
        // perform various name validations
        validateName(n);

        logNameComponent("rebind_context() name", n);

        // do we need to push through to a deeper naming context first?
        if (n.length > 1) {
            // resolve the top level name to a context, and have that context
            // resolve the rest.
            NamingContext context = resolveContext(n[0]);
            NameComponent[] subName = extractSubName(n);

            // now pass this along to the next context for the real bind operation.
            context.rebind_context(subName, nc);
        } else {
            NameComponent name = n[0];
            // we need the resolveObject() and bindObject() calls to be consistent, so
            // synchronize on this
            synchronized (this) {
                // see if we have this bound already...can't replace these.
                BindingTypeHolder type = new BindingTypeHolder();
                // for a rebind, we must have an object, and it must be a real object
                if (resolveObject(name, type) != null) {
                    // it has to resolve to a real object.  If it is a naming context,
                    // then this is the wrong binding operation.
                    if (type.value.value() != BindingType._ncontext) {
                        throw new NotFound(NotFoundReason.not_context, n);
                    }
                    // safe to unbind
                    unbindObject(name);
                }
                type.value = BindingType.ncontext;
                // now bind this object
                bindObject(name, nc, type);
            }
        }
    }

    /**
     * Resolve an an entry in the context tree.  The
     * resolved object may be a bound object or another
     * NamingContext.  If the named entry is not found,
     * a NotFound exception is thrown.
     *
     * @param n      An array of NameComponents that are the target name.
     *               The last element in the array is binding name for the
     *               object.  The remainder of the array is the path
     *               for resolving the naming context, relative to the
     *               current context.  All path contexts must already be
     *               bound in the context tree.
     *
     * @return The object bound at the indicated location.
     * @exception org.omg.CosNaming.NamingContextPackage.NotFound
     * @exception org.omg.CosNaming.NamingContextPackage.CannotProceed
     * @exception org.omg.CosNaming.NamingContextPackage.InvalidName
     * @exception org.omg.CosNaming.NamingContextPackage.AlreadyBound
     */
    public org.omg.CORBA.Object resolve(org.omg.CosNaming.NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.CannotProceed,
        org.omg.CosNaming.NamingContextPackage.InvalidName {
        // perform various name validations
        validateName(n);

        logNameComponent("resolve() name", n);

        // do we need to push through to a deeper naming context first?
        if (n.length > 1) {
            // resolve the top level name to a context, and have that context
            // resolve the rest.
            NamingContext context = resolveContext(n[0]);
            NameComponent[] subName = extractSubName(n);

            // now pass this along to the next context for the real bind operation.
            return context.resolve(subName);
        } else {
            NameComponent name = n[0];
            // see if we have this bound already...can't replace these.
            BindingTypeHolder type = new BindingTypeHolder();
            org.omg.CORBA.Object obj = resolveObject(name, type);
            if (obj == null) {
                // Object was not found
                throw new NotFound(NotFoundReason.missing_node, n);
            }
            return obj;
        }
    }

    /**
     * Remove an entry from the context tree.  The
     * target object may be a bound object or another
     * NamingContext.  If the named entry is not found,
     * a NotFound exception is thrown.
     *
     * @param n      An array of NameComponents that are the target name.
     *               The last element in the array is binding name for the
     *               object.  The remainder of the array is the path
     *               for resolving the naming context, relative to the
     *               current context.  All path contexts must already be
     *               bound in the context tree.
     *
     * @exception org.omg.CosNaming.NamingContextPackage.NotFound
     * @exception org.omg.CosNaming.NamingContextPackage.CannotProceed
     * @exception org.omg.CosNaming.NamingContextPackage.InvalidName
     * @exception org.omg.CosNaming.NamingContextPackage.AlreadyBound
     */
    public void unbind(org.omg.CosNaming.NameComponent[] n) throws org.omg.CosNaming.NamingContextPackage.NotFound,
        org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName {
        // perform various name validations
        validateName(n);

        logNameComponent("unbind() name", n);

        // do we need to push through to a deeper naming context first?
        if (n.length > 1) {
            // resolve the top level name to a context, and have that context
            // resolve the rest.
            NamingContext context = resolveContext(n[0]);
            NameComponent[] subName = extractSubName(n);

            // now pass this along to the next context for the real bind operation.
            context.unbind(subName);
        } else {
            NameComponent name = n[0];
            synchronized (this) {
                // see if we have this bound already...can't replace these.
                BindingTypeHolder type = new BindingTypeHolder();
                org.omg.CORBA.Object obj = unbindObject(name);
                if (obj == null) {
                    // Object was not found
                    throw new NotFound(NotFoundReason.missing_node, n);
                }
            }
        }
    }

    /**
     * Create a new context and bind it in at the target
     * location.
     *
     * @param n      An array of NameComponents that are the target name.
     *               The last element in the array is binding name for the
     *               object.  The remainder of the array is the path
     *               for resolving the naming context, relative to the
     *               current context.  All path contexts must already be
     *               bound in the context tree.
     *
     * @return The newly created context.
     * @exception org.omg.CosNaming.NamingContextPackage.NotFound
     * @exception org.omg.CosNaming.NamingContextPackage.CannotProceed
     * @exception org.omg.CosNaming.NamingContextPackage.InvalidName
     * @exception org.omg.CosNaming.NamingContextPackage.AlreadyBound
     */
    public synchronized org.omg.CosNaming.NamingContext bind_new_context(org.omg.CosNaming.NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.NotFound, org.omg.CosNaming.NamingContextPackage.AlreadyBound,
        org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName {
        logNameComponent("bind_new_context() name", n);

        NamingContext context = new_context();
        try {
            bind_context(n, context);
            NamingContext returnContext = context;
            // transfer this to another variable so the finally block doesn't try to destroy this.
            context = null;
            return returnContext;
        } finally {
            // if there is a bind failure on this, we need to ensure the context has
            // an opportunity to clean up any of its resources.
            if (context != null) {
                try {
                    context.destroy();
                } catch (org.omg.CosNaming.NamingContextPackage.NotEmpty e) {
                    // new contexts should be empty.
                }
            }
        }
    }

    /**
     * Convert an array of NameComponents into the string
     * form of a context name.
     *
     * @param n      The array of NameComponents to convert.
     *
     * @return The context name, in string form.
     * @exception org.omg.CosNaming.NamingContextPackage.InvalidName
     */
    public String to_string(org.omg.CosNaming.NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.InvalidName {
        validateName(n);

        logNameComponent("to_string() name", n);

        // convert the first part of the name
        StringBuffer value = new StringBuffer();
        ;
        // convert the first component, then build up from there.
        nameToString(n[0], value);

        // the remainder need to get a separator
        for (int i = 1; i < n.length; i++) {
            value.append('/');
            nameToString(n[i], value);
        }
        return value.toString();
    }

    /**
     * Perform the reverse operation of the to_string() method,
     * parsing a String context name into an array of
     * NameComponents.
     *
     * @param sn     The string form of the name.
     *
     * @return An array of NameComponents parsed from the String name.
     * @exception org.omg.CosNaming.NamingContextPackage.InvalidName
     */
    public org.omg.CosNaming.NameComponent[] to_name(String sn)
        throws org.omg.CosNaming.NamingContextPackage.InvalidName {
        // must have a argument to parse
        if (sn == null || sn.length() == 0) {
            throw new InvalidName();
        }

        List components = new ArrayList();

        StringBuffer component = new StringBuffer();

        int index = 0;
        String id = null;
        String kind = null;
        while (index < sn.length()) {
            char ch = sn.charAt(index++);

            // found an escape character or a delimiter?
            if (ch == '\\') {
                // nothing after the escape?  Trouble
                if (index >= sn.length()) {
                    throw new InvalidName();
                }
                // get the next character
                ch = sn.charAt(index++);
                component.append(ch);
            }
            // we need to process the periods here, to avoid getting
            // mixed up with unescaped periods.
            else if (ch == '.') {
                // already seen a period while scanning?  That's not allowed
                if (id != null) {
                    throw new InvalidName();
                }
                // pull off the id piece and reset the buffer
                id = component.toString();
                component.setLength(0);
            }
            // found a component delimiter?
            else if (ch == '/') {
                // not seen a id/kind separator yet?  This is an id with no kind
                if (id == null) {
                    id = component.toString();
                    kind = "";
                } else {
                    // we have an id already, pull off the kind
                    kind = component.toString();
                }
                // add the parsed name component
                components.add(new NameComponent(id, kind));
                // make sure these are all reset after pulling off a component
                component.setLength(0);
                id = null;
                kind = null;
            } else {
                component.append(ch);
            }
        }

        // parse the last section
        // not seen a id/kind separator yet?  This is an id with no kind
        if (id == null) {
            id = component.toString();
            kind = "";
        } else {
            // we have an id already, pull off the kind
            kind = component.toString();
        }
        // add the parsed name component
        components.add(new NameComponent(id, kind));

        // and turn this into a component array
        return (NameComponent[])components.toArray(new NameComponent[components.size()]);
    }

    /**
     * Create a URL name for accessing a component by name.  The
     * URL will have a corbaname: protocol.
     *
     * @param addr   The address location for the naming service used
     *               to resolve the object.  This is in "host:port" form,
     *               just line a corbaloc: URL.
     * @param sn     The string mae of the target object.
     *
     * @return A URL for accessing this object, in String form.
     * @exception org.omg.CosNaming.NamingContextExtPackage.InvalidAddress
     * @exception org.omg.CosNaming.NamingContextPackage.InvalidName
     */
    public String to_url(String addr, String sn) throws org.omg.CosNaming.NamingContextExtPackage.InvalidAddress,
        org.omg.CosNaming.NamingContextPackage.InvalidName {
        // basic validation
        if (addr == null || addr.length() == 0) {
            throw new InvalidAddress();
        }

        if (sn == null || sn.length() == 0) {
            throw new InvalidName();
        }

        // TODO:  What validation, if any, needs to be done here?
        return "corbaname:" + addr + "#" + encodeRFC2396Name(sn);
    }

    /**
     * Resolve a bound object or context using a name
     * in String form.
     *
     * @param n      The string name of the object context.  This must
     *               be a form parseable by to_name().
     *
     * @return The bound object or context.
     * @exception org.omg.CosNaming.NamingContextPackage.NotFound
     * @exception org.omg.CosNaming.NamingContextPackage.CannotProceed
     * @exception org.omg.CosNaming.NamingContextPackage.InvalidName
     */
    public org.omg.CORBA.Object resolve_str(String n) throws org.omg.CosNaming.NamingContextPackage.NotFound,
        org.omg.CosNaming.NamingContextPackage.CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName {
        // this is just a simple convenience method
        return resolve(to_name(n));
    }

    // abstract methods that are part of the NamingContext interface that need to be
    // implemented by the subclasses.

    /**
     * Create a new context of the same type as the
     * calling context.
     *
     * @return A new NamingContext item.
     * @exception org.omg.CosNaming.NamingContextPackage.NotFound
     * @exception SystemException
     */
    public abstract org.omg.CosNaming.NamingContext new_context() throws SystemException;

    /**
     * Destroy a context.  This method should clean up
     * any backing resources associated with the context.
     *
     * @exception org.omg.CosNaming.NamingContextPackage.NotEmpty
     */
    public abstract void destroy() throws org.omg.CosNaming.NamingContextPackage.NotEmpty;

    /**
     * Create a list of bound objects an contexts contained
     * within this context.
     *
     * @param how_many The count of elements to return as a BindingList.
     * @param bl       A holder element for returning the source binding list.
     * @param bi       A holder for returning a BindingIterator.  Any extra
     *                 elements not returned in the BindingList are returned
     *                 in the BindingIterator.
     *
     * @exception SystemException
     */
    public abstract void list(int how_many,
                              org.omg.CosNaming.BindingListHolder bl,
                              org.omg.CosNaming.BindingIteratorHolder bi) throws SystemException;

    // abstract methods for the sub class to implement

    /**
     * Resolve an object in this context (single level
     * resolution).
     *
     * @param n      The name of the target object.
     * @param type   A type holder for returning the bound object type
     *               information.
     *
     * @return The bound object.  Returns null if the object does not
     *         exist in the context.
     * @exception SystemException
     */
    protected abstract org.omg.CORBA.Object resolveObject(NameComponent n, BindingTypeHolder type)
        throws SystemException;

    /**
     * Bind an object into the current context.  This can
     * be either an object or a naming context.
     *
     * @param n      The single-level name of the target object.
     * @param obj    The object or context to be bound.
     * @param type
     *
     * @exception SystemException
     */
    protected abstract void bindObject(NameComponent n, org.omg.CORBA.Object obj, BindingTypeHolder type)
        throws SystemException;

    /**
     * Unbind an object from the current context.
     *
     * @param n      The name of the target object (single level).
     *
     * @return The object associated with the binding.  Returns null
     *         if there was no binding currently associated with this
     *         name.
     * @exception SystemException
     */
    protected abstract org.omg.CORBA.Object unbindObject(NameComponent n) throws SystemException;

    // implementation specific routines

    /**
     * Resolve a name to a context object stored that has
     * already been stored in this context.  Throws an exception
     * if the name cannot be resolved or if the resolved
     * object is not a naming context.
     *
     * @param name   The target name.
     *
     * @return The resolved NamingContext object.
     * @exception org.omg.CosNaming.NamingContextPackage.NotFound
     */
    protected synchronized NamingContext resolveContext(NameComponent name)
        throws org.omg.CosNaming.NamingContextPackage.NotFound {
        BindingTypeHolder type = new BindingTypeHolder();
        // Resolve this to an object.  We must be able to resolve this.
        org.omg.CORBA.Object resolvedReference = resolveObject(name, type);
        if (resolvedReference == null) {
            throw new NotFound(NotFoundReason.missing_node, new NameComponent[] {name});
        }

        // it has to resolve to a naming context
        if (type.value.value() != BindingType._ncontext) {
            throw new NotFound(NotFoundReason.not_context, new NameComponent[] {name});
        }

        // in theory, this is a naming context.  Narrow it an return.  Any
        // errors just become a NotFound exception
        try {
            return NamingContextHelper.narrow(resolvedReference);
        } catch (org.omg.CORBA.BAD_PARAM ex) {
            throw new NotFound(NotFoundReason.not_context, new NameComponent[] {name});
        }
    }

    /**
     * Extract the tail portion of a name.  This is used
     * to strip off the first name element so we can recurse
     * on the name resolutions with a resolved context.
     *
     * @param name   The current name array (this MUST have 2 or more
     *               elements).
     *
     * @return An array of NameComponent items that is one element
     *         smaller than the argument array, with the elements
     *         shifted over.
     */
    protected NameComponent[] extractSubName(NameComponent[] name) {
        NameComponent[] subName = new NameComponent[name.length - 1];
        System.arraycopy(name, 1, subName, 0, name.length - 1);
        return subName;
    }

    /**
     * Perform common name validity checking.
     *
     * @param n      The NameComponent array to check.
     *
     * @exception InvalidName
     */
    protected void validateName(NameComponent[] n) throws InvalidName {
        // perform various name validations
        if (n == null) {
            throw new BAD_PARAM(27 | org.omg.CORBA.OMGVMCID.value, CompletionStatus.COMPLETED_NO);
        }

        // Valid name?
        if (n.length < 1) {
            throw new InvalidName();
        }

        // we have at least one name, so validate the toplevel item
        NameComponent name = n[0];

        // more name validation
        if (name.id.length() == 0 && name.kind.length() == 0) {
            throw new InvalidName();
        }
    }

    /**
     * Convert a NameComponent item into a string form,
     * appending it to a StringBuffer.
     *
     * @param name   The source NameComponent.
     * @param out    The StringBuffer location used to store the name
     *               value (appended to the end).
     */
    protected void nameToString(NameComponent name, StringBuffer out) {
        // if the id is null, then we base off of the kind.
        if (name.id == null || name.id.length() == 0) {
            out.append(".");
            // true null name element?  That displays as a "."
            if (name.kind != null && name.kind.length() != 0) {
                escapeName(name.kind, out);
            }
        } else {
            // escape the name
            escapeName(name.id, out);
            // have a kind qualifier to add on?
            if (name.kind != null && name.kind.length() != 0) {
                out.append(".");
                escapeName(name.kind, out);
            }
        }
    }

    /**
     * Process a name or kind element of a NameComponent,
     * adding escape characters for '.' or '/' characters
     * that might appear in the name.
     *
     * @param name   The name element to process.
     * @param out    The StringBuffer to copy the escaped name into.
     */
    protected void escapeName(String name, StringBuffer out) {
        // no characters requiring escapes (common)?
        // use this directly
        if (name.indexOf('.') == -1 && name.indexOf('/') == -1) {
            out.append(name);
        } else {
            // scan the string adding the escapes
            for (int i = 0; i < name.length(); i++) {
                char ch = name.charAt(i);
                if (ch == '.' || ch == '/') {
                    out.append('/');
                }
                out.append(ch);
            }
        }
    }

    /**
     * Perform RFC 2396 escape encoding of a name value.
     *
     * @param name   The input name value.
     *
     * @return An encoded name, with special characters converted
     *         into a hex encoded value.
     */
    protected String encodeRFC2396Name(String name) {
        StringBuffer value = new StringBuffer();

        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);

            // Alphanumerics and the "acceptable" set of special characters just get copied
            // without encoding.
            if (Character.isLetterOrDigit(ch) || nonEscaped.indexOf(ch) != -1) {
                value.append(ch);
            } else {
                // this gets converted into a hex value, marked by "%".
                value.append('%');
                value.append(Integer.toHexString((int)ch));
            }
        }
        return value.toString();
    }

    /**
     * Test if debug logging is currently available.
     *
     * @return True if debug level (FINE) logging is currently turned on.
     */
    protected boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    /**
     * Log a line of debug output
     *
     * @param message The message to log
     */
    protected void debug(String message) {
        logger.fine(message);
    }

    /**
     * Log the name components passed in for a request.
     *
     * @param message A message describing the request context.
     * @param n       The array of name components.
     */
    protected void logNameComponent(String message, NameComponent[] n) {
        if (isDebugEnabled()) {
            debug(message);
            for (int i = 0; i < n.length; i++) {
                debug("   NameComponent " + i + " id=" + n[i].id + " kind=" + n[i].kind);
            }
        }
    }

}
