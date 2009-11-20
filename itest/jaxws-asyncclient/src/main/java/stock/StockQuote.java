package stock;
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



import javax.jws.WebService;

/**
 * JAX-WS Async style Stock quote interface
 * 
 * @version $Rev: 828277 $ $Date: 2009-10-22 02:40:52 +0100 (Thu, 22 Oct 2009) $
 */

@WebService
public interface StockQuote {
    
    float getPrice(String ticker); 
    
}
