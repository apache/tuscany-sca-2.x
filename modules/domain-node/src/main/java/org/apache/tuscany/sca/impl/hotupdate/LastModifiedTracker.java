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

package org.apache.tuscany.sca.impl.hotupdate;

import java.io.File;

/**
 * Keeps track of if a file or directory has been modified since a previous check
 */
public class LastModifiedTracker {

    private File targetFile;
    private Long lastModified;

    public LastModifiedTracker(File targetFile) {
        this.targetFile = targetFile;
        checkModified();
    }
    
    public void reset() {
        lastModified = null;
    }
    
    public boolean checkModified() {
        
        long newLastModified = getNewLastModified(targetFile);

        if (lastModified == null) {
            lastModified = newLastModified;
            return false;
        }
        
        if (newLastModified > lastModified) {
            lastModified = newLastModified;
            return true;
        }
        
        return false;
    }

    protected long getNewLastModified(File f) {

        if (!f.exists()) return 0;
        
        if (f.isFile()) return f.lastModified();

        long newLastModified = f.lastModified();

        for (File fx : f.listFiles()) {
            long fxLastModified = getNewLastModified(fx);
            if (fxLastModified > newLastModified){
                newLastModified = fxLastModified;
            }
        }

        return newLastModified;
    }
}
