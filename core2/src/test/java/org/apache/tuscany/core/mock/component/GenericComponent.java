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
package org.apache.tuscany.core.mock.component;

import java.util.List;
import java.util.Map;

import org.osoa.sca.ModuleContext;

/**
 * Generic test component
 *
 * @version $Rev$ $Date$
 */
public interface GenericComponent {

    public String getName();

    public ModuleContext getModuleContext();

    // ----------------------------------
    // Collections
    // ----------------------------------

    public Map getTestMap();

    public void setTestMap(Map pTestMap);

    public List getTestList();

    public void setTestList(List pTestList);

    // ----------------------------------
    // Arrays
    // ----------------------------------

    public int[] getArrayInt();

    public void setArrayInt(int[] pArrayInt);

    public float[] getArrayFloat();

    public void setArrayFloat(float[] pArrayFloat);

    public double[] getArrayDouble();

    public void setArrayDouble(double[] pArrayDouble);

    public long[] getArrayLong();

    public void setArrayLong(long[] pArrayLong);

    public short[] getArrayShort();

    public void setArrayShort(short[] pArrayShort);

    public boolean[] getArrayBoolean();

    public void setArrayBoolean(boolean[] pArrayBoolean);

    public String[] getArrayString();

    public void setArrayString(String[] pArrayString);

    // ----------------------------------
    // Primitives
    // ----------------------------------

    public boolean getBoolean();

    public void setBoolean(boolean pBoolean);

    public short getShort();

    public void setShort(short pShort);

    public int getInt();

    public void setInt(int pInt);

    public long getLong();

    public void setLong(long pLong);

    public double getDouble();

    public void setDouble(double pDouble);

    public float getFloat();

    public void setFloat(float pFloat);

    public char getChar();

    public void setChar(char pChar);

    // ----------------------------------
    // Object types
    // ----------------------------------

    public String getString();

    public void setString(String pString);

    public Short getOShort();

    public void setOShort(Short pOShort);

    public Integer getOInteger();

    public void setOInteger(Integer pOInteger);

    public Long getOLong();

    public void setOLong(Long pOLong);

    public Float getOFloat();

    public void setOFloat(Float pOFloat);

    public Double getODouble();

    public void setODouble(Double pODouble);

    public GenericComponent getGenericComponent();

    public void setGenericComponent(GenericComponent pGenericComponent);
}
