package org.apache.tuscany.spi.model;

import java.util.Arrays;

import junit.framework.TestCase;

public class IntentNameTestCase extends TestCase {

    public void testConstructor() throws Exception {
        String case1 = "sec.confidentiality/message/body";
        IntentName intentName = new IntentName(case1);
        assertEquals("sec", intentName.getDomain());
        assertEquals(case1, intentName.toString());
        assertTrue(Arrays.equals(new String[]{"confidentiality", "message", "body"}, intentName.getQualifiedNames()));
    }
}
