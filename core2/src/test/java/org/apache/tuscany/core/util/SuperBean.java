package org.apache.tuscany.core.util;

/**
 * @version $Rev$ $Date$
 */
public class SuperBean {

    public static final int ALL_SUPER_FIELDS = 6;
    public static final int ALL_SUPER_PUBLIC_PROTECTED_FIELDS = 5;

    public static final int ALL_SUPER_METHODS = 4;

    private String superField1;

    public String superField2;

    protected String superField3;

    public void setSuperMethod1(String param) {
    }

    public void setSuperMethod1(int param) {
    }

    public void override(String param) throws Exception {
        throw new Exception("Override not handled");
    }

    public void noOverride() throws Exception {
    }

}
