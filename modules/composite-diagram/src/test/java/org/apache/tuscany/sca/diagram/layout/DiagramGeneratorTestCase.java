/**********************************************************************
 * NAME:
 *   DiagramGeneratorTestCase
 *
 * AUTHOR:
 *   rfeng
 *
 * DESCRIPTION:
 *   
 *
 * Copyright (c) Shutterfly, Inc. 2010. All Rights reserved.
 **********************************************************************/

package org.apache.tuscany.sca.diagram.layout;

import java.io.File;

import org.apache.tuscany.sca.diagram.main.Main;
import org.junit.Test;

public class DiagramGeneratorTestCase {

    @Test
    public final void test() throws Exception {
        for (File xml : new File("input").listFiles()) {
            if (xml.getName().endsWith(".xml")) {
                System.out.println(xml);
                Main.generate(new File("target"), null, true, false, false, xml.toString());
            }
        }
    }

}
