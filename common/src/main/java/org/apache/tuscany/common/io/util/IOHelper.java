/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.tuscany.common.io.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * I/O utility methods
 *
 * @version $Rev$ $Date$
 */
public class IOHelper {

    // ----------------------------------
    // Fields
    // ----------------------------------

    public static int BYTES = 8192;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    private IOHelper() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public static void copy(InputStream in, OutputStream out) throws IOException {
        copy(in, out, -1);
    }

    public static void copy(InputStream in, OutputStream out, long byteCount) throws IOException {
        byte buffer[] = new byte[BYTES];
        int len;

        if (byteCount >= 0) {
            while (byteCount > 0) {
                if (byteCount < BYTES) {
                    len = in.read(buffer, 0, (int) byteCount);
                } else {
                    len = in.read(buffer, 0, BYTES);
                }
                if (len == -1) {
                    break;
                }
                byteCount -= len;
                out.write(buffer, 0, len);
            }
        } else {
            while (true) {
                len = in.read(buffer, 0, BYTES);
                if (len < 0) {
                    break;
                }
                out.write(buffer, 0, len);
            }
        }
    }

    public static byte[] read(InputStream in) throws IOException {
        byte buffer[] = new byte[BYTES];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len;
        while (true) {
            len = in.read(buffer, 0, BYTES);
            if (len < 0) {
                break;
            }
            out.write(buffer, 0, len);
        }
        return out.toByteArray();
    }

    /**
     * Removes a directory from the file sytsem
     */
    public static boolean deleteDir(File pDir) {
        if (pDir.isDirectory()) {
            for (String aChildren : pDir.list()) {
                boolean success = deleteDir(new File(pDir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return pDir.delete();
    }

    /**
     * Returns a stream to the resource associated with pPath in the directory
     * pRoot
     */
    public static InputStream getResource(File pRoot, String pPath) throws FileNotFoundException {

        for (File file : pRoot.listFiles()) {
            if (file.isFile() && file.getName().equals(pPath)) {
                return new BufferedInputStream(new FileInputStream(file));
            }
        }
        return null;
    }

}
