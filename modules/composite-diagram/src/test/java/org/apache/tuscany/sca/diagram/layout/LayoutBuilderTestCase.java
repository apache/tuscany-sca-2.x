/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.sca.diagram.layout;

import junit.framework.Assert;

import org.apache.tuscany.sca.diagram.layout.ComponentEntity;
import org.apache.tuscany.sca.diagram.layout.CompositeEntity;
import org.apache.tuscany.sca.diagram.layout.Entity;
import org.apache.tuscany.sca.diagram.layout.LayoutBuilder;
import org.junit.Before;
import org.junit.Test;

public class LayoutBuilderTestCase {

    LayoutBuilder lb;
    Entity[] ents;
    Entity parent;
    int[][] conns;

    @Before
    public void setUp() throws Exception {
        parent = new CompositeEntity("composite");
        parent.setX(200);
        parent.setY(100);

        ents = new Entity[5];
        for (int i = 0; i < ents.length; i++) {
            ents[i] = new ComponentEntity();
            ents[i].setId(i);
            ents[i].setParent(parent);
        }

    }

    @Test
    public void testPlaceEntities() throws Exception {
        //setUp();
        Assert.assertEquals(5, ents.length);
        Assert.assertEquals(200, ents[0].getStartPosition());

        conns = new int[5][5];
        for (int i = 0; i < conns.length; i++) {
            for (int j = 0; j < conns.length; j++) {
                //int x = Math.getExponent(Math.random());
                if (i == j - 1)
                    conns[i][j] = 1;
                else
                    conns[i][j] = 0;
            }
        }

        lb = new LayoutBuilder(ents, conns);

        ents = lb.placeEntities();

        Assert.assertEquals(5, ents.length);
        Assert.assertEquals(0, lb.getStartEnt().getId());

        Assert.assertEquals(0, ents[0].getLevel());
        Assert.assertEquals(0, ents[1].getLevel());
        Assert.assertEquals(0, ents[2].getLevel());
        Assert.assertEquals(0, ents[3].getLevel());
        Assert.assertEquals(0, ents[4].getLevel());

        Assert.assertEquals(0, ents[0].getLane());
        Assert.assertEquals(1, ents[1].getLane());
        Assert.assertEquals(2, ents[2].getLane());
        Assert.assertEquals(3, ents[3].getLane());
        Assert.assertEquals(4, ents[4].getLane());

    }

    @Test
    public void testPlaceEntities1() throws Exception {

        conns = new int[5][5];
        for (int i = 0; i < conns.length; i++) {
            for (int j = 0; j < conns.length; j++) {
                //int x = Math.getExponent(Math.random());
                if (i == j - 1 || i == j - 4)
                    conns[i][j] = 1;
                else
                    conns[i][j] = 0;
            }
        }
        conns[3][4] = 0;

        lb = new LayoutBuilder(ents, conns);

        ents = lb.placeEntities();

        Assert.assertEquals(5, ents.length);
        Assert.assertEquals(0, lb.getStartEnt().getId());

        Assert.assertEquals(0, ents[0].getLevel());
        Assert.assertEquals(0, ents[1].getLevel());
        Assert.assertEquals(0, ents[2].getLevel());
        Assert.assertEquals(0, ents[3].getLevel());
        Assert.assertEquals(1, ents[4].getLevel());

        Assert.assertEquals(0, ents[0].getLane());
        Assert.assertEquals(1, ents[1].getLane());
        Assert.assertEquals(2, ents[2].getLane());
        Assert.assertEquals(3, ents[3].getLane());
        Assert.assertEquals(1, ents[4].getLane());

    }

    @Test
    public void testPlaceEntities2() throws Exception {

        conns = new int[5][5];
        for (int i = 0; i < conns.length; i++) {
            for (int j = 0; j < conns.length; j++) {
                if (i * j > 2 && i * j <= 6)
                    conns[i][j] = 1;
                else
                    conns[i][j] = 0;
            }
        }

        lb = new LayoutBuilder(ents, conns);

        ents = lb.placeEntities();

        Assert.assertEquals(5, ents.length);
        Assert.assertEquals(1, lb.getStartEnt().getId());

        Assert.assertEquals(1, ents[0].getLevel());
        Assert.assertEquals(0, ents[1].getLevel());
        Assert.assertEquals(0, ents[2].getLevel());
        Assert.assertEquals(0, ents[3].getLevel());
        Assert.assertEquals(1, ents[4].getLevel());

        Assert.assertEquals(0, ents[0].getLane());
        Assert.assertEquals(0, ents[1].getLane());
        Assert.assertEquals(2, ents[2].getLane());
        Assert.assertEquals(1, ents[3].getLane());
        Assert.assertEquals(1, ents[4].getLane());

    }
}