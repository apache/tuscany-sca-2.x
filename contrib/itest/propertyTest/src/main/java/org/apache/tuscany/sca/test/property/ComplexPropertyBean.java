package org.apache.tuscany.sca.test.property;

public class ComplexPropertyBean {

    protected int integerNumber = 25;
    public float floatNumber = 50;
    public double doubleNumber = 75;
    public int[] intArray = null;
    public double[] doubleArray = null;
    protected String[] stringArray = null;
    
    ComplexPropertyBean numberSet;
    public ComplexPropertyBean[] numberSetArray = null;
    
    public ComplexPropertyBean() {
        
    }

    public double getDoubleNumber() {
        return doubleNumber;
    }

    public void setDoubleNumber(double doubleNumber) {
        this.doubleNumber = doubleNumber;
    }

    public float getFloatNumber() {
        return floatNumber;
    }

    public void setFloatNumber(float floatNumber) {
        this.floatNumber = floatNumber;
    }

    public int getIntegerNumber() {
        return integerNumber;
    }

    public void setIntegerNumber(int integerNumber) {
        this.integerNumber = integerNumber;
    }

    public ComplexPropertyBean getNumberSet() {
        return numberSet;
    }

    public void setNumberSet(ComplexPropertyBean numberSet) {
        this.numberSet = numberSet;
    }
    
    public String toString() {
        return Double.toString(integerNumber) + " - " + 
                Double.toString(floatNumber) + " - " + 
                Double.toString(doubleNumber) + " \n" + 
                ((intArray == null ) ? "no int array" : intArray[0] + " - " + intArray[1] + " \n " ) +
                ((doubleArray == null ) ? "no double array" : doubleArray[0] + " - " + doubleArray[1] + " \n " ) +
                ((stringArray == null ) ? "no string array" : stringArray[0] + " - " + stringArray[1] + " \n " ) +
                ((numberSetArray == null ) ? "no numberset array" : numberSetArray[0] + " - " + numberSetArray[1] + " \n " ) +
                ((numberSet == null ) ? "" : numberSet.toString());
    }

    public String[] getStringArray() {
        return stringArray;
    }

    public void setStringArray(String[] stringArray) {
        this.stringArray = stringArray;
    }
}
