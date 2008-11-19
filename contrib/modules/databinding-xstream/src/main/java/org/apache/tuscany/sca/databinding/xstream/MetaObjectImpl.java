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
package org.apache.tuscany.sca.databinding.xstream;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Implementation of a wrapper for XObject.
 *
 * @version $Rev$ $Date$
 */
public class MetaObjectImpl implements MetaObject {
    private String obID;
    private String name;
    private XObject obInstance;

    //private byte[] bytecode = null;

    public MetaObjectImpl() {
        this.obID = Utils.uniqueID();
    }

    public MetaObjectImpl(XObject x) {
        //this.name = path;
        this.obID = Utils.uniqueID();
        this.obInstance = x;

        try {
            initMetaOb(this.obInstance.getClass());

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void initMetaOb(Class<?> class1) throws FileNotFoundException, IOException {
        /*
        File file = new File(cname);
        DataInputStream cstream = new DataInputStream(new FileInputStream(file));
        bytecode = new byte[(int)file.length()];
        cstream.readFully(bytecode);
        */
        name = class1.getName();

    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return obID;
    }

    public Class<?> getType() {

        return this.obInstance.getClass();
    }

    public XObject getInstance() {
        return this.obInstance;
    }
    /*
     * TODO Add bytecode with Serialization
    public byte[] getByteCode() {
    	return bytecode;
    }
    */

}
