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
interface GenericComponent {

    String getName();

    ModuleContext getModuleContext();

    Map getTestMap();

    void setTestMap(Map pTestMap);

    List getTestList();

    void setTestList(List pTestList);

    int[] getArrayInt();

    void setArrayInt(int[] pArrayInt);

    float[] getArrayFloat();

    void setArrayFloat(float[] pArrayFloat);

    double[] getArrayDouble();

    void setArrayDouble(double[] pArrayDouble);

    long[] getArrayLong();

    void setArrayLong(long[] pArrayLong);

    short[] getArrayShort();

    void setArrayShort(short[] pArrayShort);

    boolean[] getArrayBoolean();

    void setArrayBoolean(boolean[] pArrayBoolean);

    String[] getArrayString();

    void setArrayString(String[] pArrayString);

    boolean getBoolean();

    void setBoolean(boolean pBoolean);

    short getShort();

    void setShort(short pShort);

    int getInt();

    void setInt(int pInt);

    long getLong();

    void setLong(long pLong);

    double getDouble();

    void setDouble(double pDouble);

    float getFloat();

    void setFloat(float pFloat);

    char getChar();

    void setChar(char pChar);

    String getString();

    void setString(String pString);

    Short getOShort();

    void setOShort(Short pOShort);

    Integer getOInteger();

    void setOInteger(Integer pOInteger);

    Long getOLong();

    void setOLong(Long pOLong);

    Float getOFloat();

    void setOFloat(Float pOFloat);

    Double getODouble();

    void setODouble(Double pODouble);

    GenericComponent getGenericComponent();

    void setGenericComponent(GenericComponent pGenericComponent);
}
