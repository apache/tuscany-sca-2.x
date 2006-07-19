package org.apache.tuscany.core.util;

import java.util.List;

import junit.framework.AssertionFailedError;

public class Bean2 {

    private List methodList;
    private List fieldList;

    public List getMethodList() {
        return methodList;
    }

    public void setMethodList(List list) {
        methodList = list;
    }

    public List getfieldList() {
        return fieldList;
    }

    public void setfieldList(List list) {
        throw new AssertionFailedError("setter inadvertantly called");
    }


}
