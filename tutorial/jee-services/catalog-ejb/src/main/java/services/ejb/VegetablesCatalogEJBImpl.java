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

package services.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Init;
import javax.ejb.Stateless;

@Stateless(name="VegetablesCatalogEJB")
public class VegetablesCatalogEJBImpl implements CatalogEJB {
    private List<Vegetable> catalog = new ArrayList<Vegetable>();
 
    @Init
    public void init() {
        catalog.add(new Vegetable("Broccoli", "$2.99"));
        catalog.add(new Vegetable("Asparagus", "$3.55"));
        catalog.add(new Vegetable("Cauliflower", "$1.55"));
    }

    public Vegetable[] get() {   
        init();
        Vegetable[] catalogArray = new Vegetable[catalog.size()];
        catalog.toArray(catalogArray);
        return catalogArray;
    }
}
