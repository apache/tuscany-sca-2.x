package org.apache.tuscany.core.config;


public class Bean1 extends SuperBean {

    public static final int ALL_BEAN1_FIELDS = 3 + ALL_SUPER_FIELDS;

    public static final int ALL__BEAN1_METHODS = 4 + ALL_SUPER_METHODS - 1;

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
