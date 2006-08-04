package org.apache.tuscany.core.implementation;

import java.lang.reflect.Member;

/**
 * A Callback definition that is mapped to a specific location in the implementation class. This location will typically
 * be used to inject callback values.
 *
 * @version $Rev: 416931 $ $Date: 2006-06-24 11:26:09 -0400 (Sat, 24 Jun 2006) $
 */
public class JavaMappedCallback {

    private String name;
    private Member member;
    private Class  callbackInterface;

    public JavaMappedCallback(String name, Member member, Class callbackInterface) {
        this.name = name;
        this.member = member;
        this.callbackInterface = callbackInterface;
    }

    /**
     * Returns the callback name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the Member that this reference is mapped to.
     */
    public Member getMember() {
        return member;
    }

    /**
     * Returns the callback interface
     */
    public Class getCallbackInterface() {
        return callbackInterface;
    }
}
