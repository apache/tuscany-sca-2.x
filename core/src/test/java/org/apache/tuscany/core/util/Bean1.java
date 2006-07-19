package org.apache.tuscany.core.util;


public class Bean1 extends SuperBean {

    public static final int ALL_BEAN1_FIELDS = 6 + ALL_SUPER_FIELDS;
    public static final int ALL_BEAN1_PUBLIC_PROTECTED_FIELDS = 5 + ALL_SUPER_PUBLIC_PROTECTED_FIELDS;
    public static final int ALL_BEAN1_METHODS = 4 + ALL_SUPER_METHODS - 1;
    public String field3;
    protected String field2;
    private String field1;

    public void setMethod1(String param) {
    }

    public void setMethod1(int param) {
    }

    public void override(String param) throws Exception {
    }


    public void noOverride(String param) throws Exception {
    }


}
