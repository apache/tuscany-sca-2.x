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

    public String getName() {
        return name;
    }

    @Context
    protected ModuleContext context;

    public ModuleContext getModuleContext() {
        return context;
    }

    Map testMap;

    public Map getTestMap() {
        return testMap;
    }

    public void setTestMap(Map testMap) {
        this.testMap = testMap;
    }

    List testList;

    public List getTestList() {
        return testList;
    }

    public void setTestList(List testList) {
        this.testList = testList;
    }

    private int[] arrayInt;

    public int[] getArrayInt() {
        return arrayInt;
    }

    public void setArrayInt(int[] arrayInt) {
        this.arrayInt = arrayInt;
    }

    private float[] arrayFloat;

    public float[] getArrayFloat() {
        return arrayFloat;
    }

    public void setArrayFloat(float[] pArrayFloat) {
        arrayFloat = pArrayFloat;
    }

    private double[] arrayDouble;

    public double[] getArrayDouble() {
        return arrayDouble;
    }

    public void setArrayDouble(double[] pArrayDouble) {
        arrayDouble = pArrayDouble;
    }

    private long[] arrayLong;

    public long[] getArrayLong() {
        return arrayLong;
    }

    public void setArrayLong(long[] arrayLong) {
        this.arrayLong = arrayLong;
    }

    private short[] arrayShort;

    public short[] getArrayShort() {
        return arrayShort;
    }

    public void setArrayShort(short[] arrayShort) {
        this.arrayShort = arrayShort;
    }

    private boolean[] arrayBoolean;

    public boolean[] getArrayBoolean() {
        return arrayBoolean;
    }

    public void setArrayBoolean(boolean[] arrayBoolean) {
        this.arrayBoolean = arrayBoolean;
    }

    private String[] arrayString;

    public String[] getArrayString() {
        return arrayString;
    }

    public void setArrayString(String[] arrayString) {
        this.arrayString = arrayString;
    }

    private boolean mBoolean;

    public boolean getBoolean() {
        return mBoolean;
    }

    public void setBoolean(boolean pBoolean) {
        mBoolean = pBoolean;
    }

    private short mShort;

    public short getShort() {
        return mShort;
    }

    public void setShort(short pShort) {
        mShort = pShort;
    }

    private int mInt;

    public int getInt() {
        return mInt;
    }

    public void setInt(int pInt) {
        mInt = pInt;
    }

    private long mLong;

    public long getLong() {
        return mLong;
    }

    public void setLong(long pLong) {
        mLong = pLong;
    }

    private double mDouble;

    public double getDouble() {
        return mDouble;
    }

    public void setDouble(double pDouble) {
        mDouble = pDouble;
    }

    private float mFloat;

    public float getFloat() {
        return mFloat;
    }

    public void setFloat(float pFloat) {
        mFloat = pFloat;
    }

    private char mChar;

    public char getChar() {
        return mChar;
    }

    public void setChar(char pChar) {
        mChar = pChar;
    }

    private String mString;

    public String getString() {
        return mString;
    }

    public void setString(String pString) {
        mString = pString;
    }

    private Short mOShort;

    public Short getOShort() {
        return mOShort;
    }

    public void setOShort(Short pOShort) {
        mOShort = pOShort;
    }

    private Integer mOInteger;

    public Integer getOInteger() {
        return mOInteger;
    }

    public void setOInteger(Integer pOInteger) {
        mOInteger = pOInteger;
    }

    private Long mOLong;

    public Long getOLong() {
        return mOLong;
    }

    public void setOLong(Long pOLong) {
        mOLong = pOLong;
    }

    private Float mOFloat;

    public Float getOFloat() {
        return mOFloat;
    }

    public void setOFloat(Float pOFloat) {
        mOFloat = pOFloat;
    }

    private Double mODouble;

    public Double getODouble() {
        return mODouble;
    }

    public void setODouble(Double pODouble) {
        mODouble = pODouble;
    }

    @Property(name = "genericComponent")
    private GenericComponent mGenericComponent;

    public GenericComponent getGenericComponent() {
        return mGenericComponent;
    }

    public void setGenericComponent(GenericComponent pGenericComponent) {
        mGenericComponent = pGenericComponent;
    }


}
