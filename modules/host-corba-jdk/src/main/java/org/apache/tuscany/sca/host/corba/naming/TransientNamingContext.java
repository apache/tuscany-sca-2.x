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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingHolder;
import org.omg.CosNaming.BindingIteratorHelper;
import org.omg.CosNaming.BindingIteratorPOA;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.BindingTypeHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.PortableServer.POA;

public class TransientNamingContext extends NamingContextBase {
    // the bindings maintained by this context
    protected HashMap bindings = new HashMap();
    // the root context object
    protected org.omg.CORBA.Object rootContext = null;

    /**
     * Create a top-level naming context.
     *
     * @param orb    The orb hosting this context.
     * @param poa    The POA used to activate the object.
     *
     * @exception Exception
     */
    public TransientNamingContext(ORB orb, POA poa) throws Exception {
        this(orb, poa, null);
        // now get the initial root context as a corba object.

        byte[] objectId = poa.activate_object(this);
        rootContext = poa.id_to_reference(objectId);

    }

    /**
     * Construct a TransientNamingContext subcontext.
     *
     * @param orb    The orb this context is associated with.
     * @param poa    The POA the root context is activated under.
     * @param root   The root context.
     *
     * @exception Exception
     */
    public TransientNamingContext(ORB orb, POA poa, org.omg.CORBA.Object root) throws Exception {
        super(orb, poa);
        // save the root context link.
        rootContext = root;
    }

    // abstract methods part of the interface contract that the implementation is required
    // to supply.

    /**
     * Create a new context of the same type as the
     * calling context.
     *
     * @return A new NamingContext item.
     * @exception org.omg.CosNaming.NamingContextPackage.NotFound
     * @exception SystemException
     */
    public NamingContext new_context() throws SystemException {
        try {
            // create a new context.  Then we need to register this with the POA and activate it.
            TransientNamingContext newContext = new TransientNamingContext(orb, poa, rootContext);

            byte[] objectId = poa.activate_object(newContext);
            org.omg.CORBA.Object obj = poa.id_to_reference(objectId);
            return NamingContextHelper.narrow(obj);
        } catch (SystemException e) {
            // just propagate system exceptions
            throw e;
        } catch (Exception e) {
            throw (INTERNAL)(new INTERNAL("Unable to create new naming context").initCause(e));
        }
    }

    /**
     * Destroy a context.  This method should clean up
     * any backing resources associated with the context.
     *
     * @exception org.omg.CosNaming.NamingContextPackage.NotEmpty
     */
    public synchronized void destroy() throws org.omg.CosNaming.NamingContextPackage.NotEmpty {
        // still holding bound objects?  Not allowed to destroy
        if (!bindings.isEmpty()) {
            throw new NotEmpty();
        }

        try {
            // now detach ourselves from the POA
            byte[] objectId = poa.servant_to_id(this);
            if (objectId != null) {
                poa.deactivate_object(objectId);
            }
        } catch (Exception e) {
            // ignore
        }
    }

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
    public synchronized void list(int how_many,
                                  org.omg.CosNaming.BindingListHolder bl,
                                  org.omg.CosNaming.BindingIteratorHolder bi) throws SystemException {
        TransientBindingIterator iterator = new TransientBindingIterator(poa, (HashMap)bindings.clone());
        // have the iterator fill in the entries here
        iterator.next_n(how_many, bl);

        // now it's necessary to activate this iterator with the poa.  The value we pass
        // back is the narrowed activated object
        try {
            byte[] objectId = poa.activate_object(iterator);
            org.omg.CORBA.Object obj = poa.id_to_reference(objectId);

            bi.value = BindingIteratorHelper.narrow(obj);
        } catch (SystemException e) {
            // just propagate system exceptions
            throw e;
        } catch (Exception e) {
            throw (INTERNAL)(new INTERNAL("Unable to activate BindingIterator").initCause(e));
        }
    }

    // lower level functions that are used by the base class

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
    protected org.omg.CORBA.Object resolveObject(NameComponent n, BindingTypeHolder type) throws SystemException {
        // special call to resolve the root context.  This is the only one that goes backwards.
        if (n.id.length() == 0 && n.kind.length() == 0) {
            // this is a name context item, so set it properly.
            type.value = BindingType.ncontext;
            return rootContext;
        }

        BindingKey key = new BindingKey(n);
        BoundObject obj = (BoundObject)bindings.get(key);
        // if not in the table, just return null
        if (obj == null) {
            return null;
        }
        // update the type information and return the bound object reference.
        type.value = obj.type;
        return obj.boundObject;
    }

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
    protected void bindObject(NameComponent n, org.omg.CORBA.Object obj, BindingTypeHolder type) throws SystemException {
        // fairly simple table put...
        bindings.put(new BindingKey(n), new BoundObject(n, obj, type.value));
    }

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
    protected org.omg.CORBA.Object unbindObject(NameComponent n) throws SystemException {
        //remove the object from the hash table, returning the bound object if it exists.
        BindingKey key = new BindingKey(n);
        BoundObject obj = (BoundObject)bindings.remove(key);

        if (obj != null) {
            return obj.boundObject;
        }
        return null;
    }

    /**
     * Retrieve the rootContext for this NamingContext.
     *
     * @return The rootContext CORBA object associated with this context.
     */
    public org.omg.CORBA.Object getRootContext() {
        return rootContext;
    }

    /**
     * Internal class used for HashMap lookup keys.
     */
    class BindingKey {
        // the name component this is a HashMap key for.
        public NameComponent name;
        private int hashval = 0;

        /**
         * Create a new BindingKey for a NameComponent.
         *
         * @param n      The lookup name.
         */
        public BindingKey(NameComponent n) {
            name = n;
            // create a hash value used for lookups
            if (name.id != null) {
                hashval += name.id.hashCode();
            }
            if (name.kind != null) {
                hashval += name.kind.hashCode();
            }
        }

        /**
         * Return the hashcode associated with this binding key.  The
         * hashcode is created using the NameComponent id and
         * kind fields.
         *
         * @return The lookup hashvalue associated with this key.
         */
        public int hashCode() {
            return hashval;
        }

        /**
         * Compare two BindingKeys for equality (used for HashMap
         * lookups).
         *
         * @param other  The comparison partner.
         *
         * @return True if the keys are equivalent, false otherwise.
         */
        public boolean equals(Object other) {
            // if not given or the wrong type, this is false.
            if (other == null || !(other instanceof BindingKey)) {
                return false;
            }

            BindingKey otherKey = (BindingKey)other;

            // verify first on the id name.
            if (name.id != null) {
                if (otherKey.name.id == null) {
                    return false;
                }
                if (!name.id.equals(otherKey.name.id)) {
                    return false;
                }
            } else {
                if (otherKey.name.id != null) {
                    return false;
                }
            }
            // this is a match so far...now compare the kinds
            if (name.kind != null) {
                if (otherKey.name.kind == null) {
                    return false;
                }
                if (!name.kind.equals(otherKey.name.kind)) {
                    return false;
                }
            } else {
                if (otherKey.name.kind != null) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Internal class used to store bound objects in the HashMap.
     */
    public class BoundObject {
        // the name this object is bound under.
        public NameComponent name;
        // the type of binding (either nobject or ncontext).
        public BindingType type;
        // the actual bound object.
        public org.omg.CORBA.Object boundObject;

        /**
         * Create a new object binding for our HashMap.
         *
         * @param name   The bound object's name.
         * @param boundObject
         *               The bound object (real object or NamingContext).
         * @param type   The type information associated with this binding.
         */
        public BoundObject(NameComponent name, org.omg.CORBA.Object boundObject, BindingType type) {
            this.name = name;
            this.boundObject = boundObject;
            this.type = type;
        }
    }

    /**
     * Context implementation version of the BindingIterator
     * object used to return list items.
     */
    public class TransientBindingIterator extends BindingIteratorPOA {
        // the POA used to activate this object (required for destroy();
        private POA poa;
        // the binding set we're iterating over (this must be a snapshot copy)
        private HashMap bindings;
        // the iterator use to access the bindings
        private Iterator iterator;

        /**
         * Create a new BindingIterator hosted by the given POA and
         * iterating over the map of items.
         *
         * @param poa      The hosting POA.
         * @param bindings The HashMap of bound objects.
         */
        public TransientBindingIterator(POA poa, HashMap bindings) {
            this.poa = poa;
            this.bindings = bindings;
            this.iterator = bindings.values().iterator();
        }

        /**
         * Return the next object in the iteration sequence.
         *
         * @param b      The BindingHolder used to return the next item.  If
         *               we've reached the end of the sequence, an item
         *               with an empty name is returned.
         *
         * @return true if there is another item, false otherwise.
         */
        public boolean next_one(org.omg.CosNaming.BindingHolder b) {
            if (iterator.hasNext()) {
                // return this as a Binding value.
                BoundObject obj = (BoundObject)iterator.next();
                b.value = new Binding(new NameComponent[] {obj.name}, obj.type);
                return true;
            } else {
                // return an empty element
                b.value = new Binding(new NameComponent[0], BindingType.nobject);
                return false;
            }
        }

        /**
         * Retrieve the next "n" items from the list, returned
         * as a BindingList.
         *
         * @param how_many The count of items to retrieve.
         * @param bl       A holder for returning an array of Bindings for
         *                 the returned items.
         *
         * @return true if any items were returned, false if there's
         *         nothing left to return.
         */
        public boolean next_n(int how_many, org.omg.CosNaming.BindingListHolder bl) {
            List accum = new ArrayList();
            BindingHolder holder = new BindingHolder();
            int i = 0;
            // Keep iterating as long as there are entries
            while (i < how_many && next_one(holder)) {
                accum.add(holder.value);
                i++;
            }

            // convert to an array and return whether we found anything.
            bl.value = (Binding[])accum.toArray(new Binding[accum.size()]);
            return accum.isEmpty();
        }

        /**
         * Destory this BindingIterator instance, which deativates
         * it from the hosting POA.
         */
        public void destroy() {
            try {
                // we need to deactivate this from the POA.
                byte[] objectId = poa.servant_to_id(this);
                if (objectId != null) {
                    poa.deactivate_object(objectId);
                }
            } catch (Exception e) {
            }
        }
    }
}
