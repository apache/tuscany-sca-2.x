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
import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Property;

/**
 * Base test component implementation
 *
 * @version $Rev$ $Date$
 */
public class AbstractGenericComponent implements GenericComponent {

    @ComponentName
    protected String name;
    @Context
    protected ModuleContext context;
    Map testMap;
    List testList;
    private int[] arrayInt;
    private float[] arrayFloat;
    private double[] arrayDouble;
    private long[] arrayLong;
    private short[] arrayShort;
    private boolean[] arrayBoolean;
    private String[] arrayString;
    private boolean mBoolean;
    private short mShort;
    private int mInt;
    private long mLong;
    private double mDouble;
    private float mFloat;
    private char mChar;
    private String mString;
    private Short mOShort;
    private Integer mOInteger;
    private Long mOLong;
    private Float mOFloat;
    private Double mODouble;
    @Property(name = "genericComponent")
    private GenericComponent mGenericComponent;

    public String getName() {
        return name;
    }

    public ModuleContext getModuleContext() {
        return context;
    }

    public Map getTestMap() {
        return testMap;
    }

    public void setTestMap(Map testMap) {
        this.testMap = testMap;
    }

    public List getTestList() {
        return testList;
    }

    public void setTestList(List testList) {
        this.testList = testList;
    }

    public int[] getArrayInt() {
        return arrayInt;
    }

    public void setArrayInt(int[] arrayInt) {
        this.arrayInt = arrayInt;
    }

    public float[] getArrayFloat() {
        return arrayFloat;
    }

    public void setArrayFloat(float[] pArrayFloat) {
        arrayFloat = pArrayFloat;
    }

    public double[] getArrayDouble() {
        return arrayDouble;
    }

    public void setArrayDouble(double[] pArrayDouble) {
        arrayDouble = pArrayDouble;
    }

    public long[] getArrayLong() {
        return arrayLong;
    }

    public void setArrayLong(long[] arrayLong) {
        this.arrayLong = arrayLong;
    }

    public short[] getArrayShort() {
        return arrayShort;
    }

    public void setArrayShort(short[] arrayShort) {
        this.arrayShort = arrayShort;
    }

    public boolean[] getArrayBoolean() {
        return arrayBoolean;
    }

    public void setArrayBoolean(boolean[] arrayBoolean) {
        this.arrayBoolean = arrayBoolean;
    }

    public String[] getArrayString() {
        return arrayString;
    }

    public void setArrayString(String[] arrayString) {
        this.arrayString = arrayString;
    }

    public boolean getBoolean() {
        return mBoolean;
    }

    public void setBoolean(boolean pBoolean) {
        mBoolean = pBoolean;
    }

    public short getShort() {
        return mShort;
    }

    public void setShort(short pShort) {
        mShort = pShort;
    }

    public int getInt() {
        return mInt;
    }

    public void setInt(int pInt) {
        mInt = pInt;
    }

    public long getLong() {
        return mLong;
    }

    public void setLong(long pLong) {
        mLong = pLong;
    }

    public double getDouble() {
        return mDouble;
    }

    public void setDouble(double pDouble) {
        mDouble = pDouble;
    }

    public float getFloat() {
        return mFloat;
    }

    public void setFloat(float pFloat) {
        mFloat = pFloat;
    }

    public char getChar() {
        return mChar;
    }

    public void setChar(char pChar) {
        mChar = pChar;
    }

    public String getString() {
        return mString;
    }

    public void setString(String pString) {
        mString = pString;
    }

    public Short getOShort() {
        return mOShort;
    }

    public void setOShort(Short pOShort) {
        mOShort = pOShort;
    }

    public Integer getOInteger() {
        return mOInteger;
    }

    public void setOInteger(Integer pOInteger) {
        mOInteger = pOInteger;
    }

    public Long getOLong() {
        return mOLong;
    }

    public void setOLong(Long pOLong) {
        mOLong = pOLong;
    }

    public Float getOFloat() {
        return mOFloat;
    }

    public void setOFloat(Float pOFloat) {
        mOFloat = pOFloat;
    }

    public Double getODouble() {
        return mODouble;
    }

    public void setODouble(Double pODouble) {
        mODouble = pODouble;
    }

    public GenericComponent getGenericComponent() {
        return mGenericComponent;
    }

    public void setGenericComponent(GenericComponent pGenericComponent) {
        mGenericComponent = pGenericComponent;
    }


}
