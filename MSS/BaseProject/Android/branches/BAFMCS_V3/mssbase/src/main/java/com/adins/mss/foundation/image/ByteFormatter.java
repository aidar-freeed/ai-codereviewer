/*
 * jets3t : Java Extra-Tasty S3 Toolkit (for Amazon S3 online storage service)
 * This is a java.net project, see https://jets3t.dev.java.net/
 *
 * Copyright 2006 James Murty
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
package com.adins.mss.foundation.image;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Formats numeric byte values into human-readable strings.
 *
 * @author James Murty
 */
public class ByteFormatter {

    private static String gigabyteSuffix = " Gigabytes";
    private static String megabyteSuffix = " Megabytes";
    private static String kilobyteSuffix = " Kilobytes";
    private static String byteSuffix = " Bytes";
    private static NumberFormat nf = new DecimalFormat("#,###.##");
    ;

    /**
     * Converts a byte size into a human-readable string, such as "1.43 MB" or "27 KB".
     * The values used are based on powers of 1024, ie 1 KB = 1024 bytes, not 1000 bytes.
     *
     * @param byteSize the byte size of some item
     * @return a human-readable description of the byte size
     */
    public static String formatByteSize(long byteSize) {
        String result = null;
        try {
            if (byteSize > Math.pow(1024, 3)) {
                // Report gigabytes
                result = nf.format(new Double(byteSize / Math.pow(1024, 3))) + gigabyteSuffix;
            } else if (byteSize > Math.pow(1024, 2)) {
                // Report megabytes
                result = nf.format(new Double(byteSize / Math.pow(1024, 2))) + megabyteSuffix;
            } else if (byteSize > 1024) {
                // Report kilobytes
                result = nf.format(new Double(byteSize / Math.pow(1024, 1))) + kilobyteSuffix;
            } else if (byteSize >= 0) {
                // Report bytes
                result = byteSize + byteSuffix;
            }
        } catch (NumberFormatException e) {
            return byteSize + byteSuffix;
        }
        return result;
    }
}