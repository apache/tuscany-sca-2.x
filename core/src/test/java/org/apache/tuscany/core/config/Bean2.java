package org.apache.tuscany.core.config;

import junit.framework.AssertionFailedError;

import java.util.List;

public class Bean2 {

    private List methodList;

    public List getMethodList() {
        return methodList;
    }

    public void setMethodList(List list) {
        methodList = list;
    }

    private List fieldList;

    public List getfieldList() {
        return fieldList;
    }

    public void setfieldList(List list) {
       throw new AssertionFailedError("setter inadvertantly called");
    }

     
}
