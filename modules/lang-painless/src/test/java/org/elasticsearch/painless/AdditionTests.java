/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.painless;

import org.elasticsearch.common.hash.MessageDigests;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Tests for addition operator across all types */
//TODO: NaN/Inf/overflow/...
public class AdditionTests extends ScriptTestCase {

    public void testDiff() throws Exception {
        String info = "\n";

        BufferedReader reader8 = new BufferedReader(new FileReader("/home/jdconrad/test8"));

        Map<String, String> map8 = new HashMap<>();
        String line8 = reader8.readLine();
        StringBuilder builder8 = new StringBuilder();

        while (line8 != null) {
            if ("".equals(line8)) {
                String test8 = builder8.toString();
                String key8 = test8.substring(0, test8.indexOf("\n")) + ":" +
                        MessageDigests.toHexString(MessageDigests.sha256().digest(test8.getBytes()));
                map8.put(key8, test8);
                builder8 = new StringBuilder();
            } else {
                if (line8.contains("IINC 1") == false) {
                    builder8.append(line8).append("\n");
                }
            }
            line8 = reader8.readLine();
        }

        BufferedReader reader7 = new BufferedReader(new FileReader("/home/jdconrad/test7"));

        Map<String, String> map7 = new HashMap<>();
        String line7 = reader7.readLine();
        StringBuilder builder7 = new StringBuilder();

        while (line7 != null) {
            if ("".equals(line7)) {
                String test7 = builder7.toString();
                String key7 = test7.substring(0, test7.indexOf("\n")) + ":" +
                        MessageDigests.toHexString(MessageDigests.sha256().digest(test7.getBytes()));
                map7.put(key7, test7);
                builder7 = new StringBuilder();
            } else {
                if (line7.contains("IINC 1") == false) {
                    builder7.append(line7).append("\n");
                }
            }
            line7 = reader7.readLine();
        }

        info += map8.keySet().size() + " - " + map7.keySet().size() + "\n";

        List<String> remove = new ArrayList<>();

        for (String key8 : map8.keySet()) {
            if (map7.containsKey(key8)) {
                remove.add(key8);
            }
        }

        for (String rkey : remove) {
            map8.remove(rkey);
            map7.remove(rkey);
        }

        info += map8.keySet().size() + " - " + map7.keySet().size() + "\n";

        // remove tests that don't exist in both

        Set<String> names8 = new HashSet<>();

        for (String key8 : map8.keySet()) {
            names8.add(key8.substring(0, key8.indexOf(':')));
        }

        Set<String> names7 = new HashSet<>();

        for (String key7 : map7.keySet()) {
            names7.add(key7.substring(0, key7.indexOf(':')));
        }

        info += names8.size() + " - " + names7.size() + "\n";

        Set<String> remove8 = new HashSet<>();

        for (String name8 : names8) {
            if (names7.contains(name8) == false) {
                remove8.add(name8);
            }
        }

        remove.clear();

        for (String key8 : map8.keySet()) {
            if (remove8.contains(key8.substring(0, key8.indexOf(':')))) {
                remove.add(key8);
            }
        }

        for (String rkey : remove) {
            map8.remove(rkey);
        }

        Set<String> remove7 = new HashSet<>();

        for (String name7 : names7) {
            if (names8.contains(name7) == false) {
                remove7.add(name7);
            }
        }

        remove.clear();

        for (String key7 : map7.keySet()) {
            if (remove7.contains(key7.substring(0, key7.indexOf(':')))) {
                remove.add(key7);
            }
        }

        for (String rkey : remove) {
            map7.remove(rkey);
        }

        info += map8.keySet().size() + " - " + map7.keySet().size() + "\n";

        List<String> sort8 = new ArrayList<>(map8.keySet());
        sort8.sort(String::compareTo);

        List<String> sort7 = new ArrayList<>(map7.keySet());
        sort7.sort(String::compareTo);

        PrintWriter writer = new PrintWriter(new FileOutputStream("/home/jdconrad/test7o"));
        writer.write(info + "7");
        writer.write("\n\n");

        for (String key7 : sort7) {
            writer.write(map7.get(key7));
            writer.write("\n\n");
        }

        writer.close();

        writer = new PrintWriter(new FileOutputStream("/home/jdconrad/test8o"));
        writer.write(info + "8");
        writer.write("\n\n");

        for (String key8 : sort8) {
            writer.write(map8.get(key8));
            writer.write("\n\n");
        }

        writer.close();

        //throw new IllegalArgumentException(info + "\n" + builder8.toString() + "\n" + builder7.toString());
    }

    public void testBasics() throws Exception {
        assertEquals(3.0, exec("double x = 1; byte y = 2; return x + y;"));
    }

    public void testInt() throws Exception {
        assertEquals(1+1, exec("int x = 1; int y = 1; return x+y;"));
        assertEquals(1+2, exec("int x = 1; int y = 2; return x+y;"));
        assertEquals(5+10, exec("int x = 5; int y = 10; return x+y;"));
        assertEquals(1+1+2, exec("int x = 1; int y = 1; int z = 2; return x+y+z;"));
        assertEquals((1+1)+2, exec("int x = 1; int y = 1; int z = 2; return (x+y)+z;"));
        assertEquals(1+(1+2), exec("int x = 1; int y = 1; int z = 2; return x+(y+z);"));
        assertEquals(0+1, exec("int x = 0; int y = 1; return x+y;"));
        assertEquals(1+0, exec("int x = 1; int y = 0; return x+y;"));
        assertEquals(0+0, exec("int x = 0; int y = 0; return x+y;"));
        assertEquals(0+0, exec("int x = 0; int y = 0; return x+y;"));
    }

    public void testIntConst() throws Exception {
        assertEquals(1+1, exec("return 1+1;"));
        assertEquals(1+2, exec("return 1+2;"));
        assertEquals(5+10, exec("return 5+10;"));
        assertEquals(1+1+2, exec("return 1+1+2;"));
        assertEquals((1+1)+2, exec("return (1+1)+2;"));
        assertEquals(1+(1+2), exec("return 1+(1+2);"));
        assertEquals(0+1, exec("return 0+1;"));
        assertEquals(1+0, exec("return 1+0;"));
        assertEquals(0+0, exec("return 0+0;"));
    }

    public void testByte() throws Exception {
        assertEquals((byte)1+(byte)1, exec("byte x = 1; byte y = 1; return x+y;"));
        assertEquals((byte)1+(byte)2, exec("byte x = 1; byte y = 2; return x+y;"));
        assertEquals((byte)5+(byte)10, exec("byte x = 5; byte y = 10; return x+y;"));
        assertEquals((byte)1+(byte)1+(byte)2, exec("byte x = 1; byte y = 1; byte z = 2; return x+y+z;"));
        assertEquals(((byte)1+(byte)1)+(byte)2, exec("byte x = 1; byte y = 1; byte z = 2; return (x+y)+z;"));
        assertEquals((byte)1+((byte)1+(byte)2), exec("byte x = 1; byte y = 1; byte z = 2; return x+(y+z);"));
        assertEquals((byte)0+(byte)1, exec("byte x = 0; byte y = 1; return x+y;"));
        assertEquals((byte)1+(byte)0, exec("byte x = 1; byte y = 0; return x+y;"));
        assertEquals((byte)0+(byte)0, exec("byte x = 0; byte y = 0; return x+y;"));
    }

    public void testByteConst() throws Exception {
        assertEquals((byte)1+(byte)1, exec("return (byte)1+(byte)1;"));
        assertEquals((byte)1+(byte)2, exec("return (byte)1+(byte)2;"));
        assertEquals((byte)5+(byte)10, exec("return (byte)5+(byte)10;"));
        assertEquals((byte)1+(byte)1+(byte)2, exec("return (byte)1+(byte)1+(byte)2;"));
        assertEquals(((byte)1+(byte)1)+(byte)2, exec("return ((byte)1+(byte)1)+(byte)2;"));
        assertEquals((byte)1+((byte)1+(byte)2), exec("return (byte)1+((byte)1+(byte)2);"));
        assertEquals((byte)0+(byte)1, exec("return (byte)0+(byte)1;"));
        assertEquals((byte)1+(byte)0, exec("return (byte)1+(byte)0;"));
        assertEquals((byte)0+(byte)0, exec("return (byte)0+(byte)0;"));
    }

    public void testChar() throws Exception {
        assertEquals((char)1+(char)1, exec("char x = 1; char y = 1; return x+y;"));
        assertEquals((char)1+(char)2, exec("char x = 1; char y = 2; return x+y;"));
        assertEquals((char)5+(char)10, exec("char x = 5; char y = 10; return x+y;"));
        assertEquals((char)1+(char)1+(char)2, exec("char x = 1; char y = 1; char z = 2; return x+y+z;"));
        assertEquals(((char)1+(char)1)+(char)2, exec("char x = 1; char y = 1; char z = 2; return (x+y)+z;"));
        assertEquals((char)1+((char)1+(char)2), exec("char x = 1; char y = 1; char z = 2; return x+(y+z);"));
        assertEquals((char)0+(char)1, exec("char x = 0; char y = 1; return x+y;"));
        assertEquals((char)1+(char)0, exec("char x = 1; char y = 0; return x+y;"));
        assertEquals((char)0+(char)0, exec("char x = 0; char y = 0; return x+y;"));
    }

    public void testCharConst() throws Exception {
        assertEquals((char)1+(char)1, exec("return (char)1+(char)1;"));
        assertEquals((char)1+(char)2, exec("return (char)1+(char)2;"));
        assertEquals((char)5+(char)10, exec("return (char)5+(char)10;"));
        assertEquals((char)1+(char)1+(char)2, exec("return (char)1+(char)1+(char)2;"));
        assertEquals(((char)1+(char)1)+(char)2, exec("return ((char)1+(char)1)+(char)2;"));
        assertEquals((char)1+((char)1+(char)2), exec("return (char)1+((char)1+(char)2);"));
        assertEquals((char)0+(char)1, exec("return (char)0+(char)1;"));
        assertEquals((char)1+(char)0, exec("return (char)1+(char)0;"));
        assertEquals((char)0+(char)0, exec("return (char)0+(char)0;"));
    }

    public void testShort() throws Exception {
        assertEquals((short)1+(short)1, exec("short x = 1; short y = 1; return x+y;"));
        assertEquals((short)1+(short)2, exec("short x = 1; short y = 2; return x+y;"));
        assertEquals((short)5+(short)10, exec("short x = 5; short y = 10; return x+y;"));
        assertEquals((short)1+(short)1+(short)2, exec("short x = 1; short y = 1; short z = 2; return x+y+z;"));
        assertEquals(((short)1+(short)1)+(short)2, exec("short x = 1; short y = 1; short z = 2; return (x+y)+z;"));
        assertEquals((short)1+((short)1+(short)2), exec("short x = 1; short y = 1; short z = 2; return x+(y+z);"));
        assertEquals((short)0+(short)1, exec("short x = 0; short y = 1; return x+y;"));
        assertEquals((short)1+(short)0, exec("short x = 1; short y = 0; return x+y;"));
        assertEquals((short)0+(short)0, exec("short x = 0; short y = 0; return x+y;"));
    }

    public void testShortConst() throws Exception {
        assertEquals((short)1+(short)1, exec("return (short)1+(short)1;"));
        assertEquals((short)1+(short)2, exec("return (short)1+(short)2;"));
        assertEquals((short)5+(short)10, exec("return (short)5+(short)10;"));
        assertEquals((short)1+(short)1+(short)2, exec("return (short)1+(short)1+(short)2;"));
        assertEquals(((short)1+(short)1)+(short)2, exec("return ((short)1+(short)1)+(short)2;"));
        assertEquals((short)1+((short)1+(short)2), exec("return (short)1+((short)1+(short)2);"));
        assertEquals((short)0+(short)1, exec("return (short)0+(short)1;"));
        assertEquals((short)1+(short)0, exec("return (short)1+(short)0;"));
        assertEquals((short)0+(short)0, exec("return (short)0+(short)0;"));
    }

    public void testLong() throws Exception {
        assertEquals(1L+1L, exec("long x = 1; long y = 1; return x+y;"));
        assertEquals(1L+2L, exec("long x = 1; long y = 2; return x+y;"));
        assertEquals(5L+10L, exec("long x = 5; long y = 10; return x+y;"));
        assertEquals(1L+1L+2L, exec("long x = 1; long y = 1; long z = 2; return x+y+z;"));
        assertEquals((1L+1L)+2L, exec("long x = 1; long y = 1; long z = 2; return (x+y)+z;"));
        assertEquals(1L+(1L+2L), exec("long x = 1; long y = 1; long z = 2; return x+(y+z);"));
        assertEquals(0L+1L, exec("long x = 0; long y = 1; return x+y;"));
        assertEquals(1L+0L, exec("long x = 1; long y = 0; return x+y;"));
        assertEquals(0L+0L, exec("long x = 0; long y = 0; return x+y;"));
    }

    public void testLongConst() throws Exception {
        assertEquals(1L+1L, exec("return 1L+1L;"));
        assertEquals(1L+2L, exec("return 1L+2L;"));
        assertEquals(5L+10L, exec("return 5L+10L;"));
        assertEquals(1L+1L+2L, exec("return 1L+1L+2L;"));
        assertEquals((1L+1L)+2L, exec("return (1L+1L)+2L;"));
        assertEquals(1L+(1L+2L), exec("return 1L+(1L+2L);"));
        assertEquals(0L+1L, exec("return 0L+1L;"));
        assertEquals(1L+0L, exec("return 1L+0L;"));
        assertEquals(0L+0L, exec("return 0L+0L;"));
    }

    public void testFloat() throws Exception {
        assertEquals(1F+1F, exec("float x = 1F; float y = 1F; return x+y;"));
        assertEquals(1F+2F, exec("float x = 1F; float y = 2F; return x+y;"));
        assertEquals(5F+10F, exec("float x = 5F; float y = 10F; return x+y;"));
        assertEquals(1F+1F+2F, exec("float x = 1F; float y = 1F; float z = 2F; return x+y+z;"));
        assertEquals((1F+1F)+2F, exec("float x = 1F; float y = 1F; float z = 2F; return (x+y)+z;"));
        assertEquals((1F+1F)+2F, exec("float x = 1F; float y = 1F; float z = 2F; return x+(y+z);"));
        assertEquals(0F+1F, exec("float x = 0F; float y = 1F; return x+y;"));
        assertEquals(1F+0F, exec("float x = 1F; float y = 0F; return x+y;"));
        assertEquals(0F+0F, exec("float x = 0F; float y = 0F; return x+y;"));
    }

    public void testFloatConst() throws Exception {
        assertEquals(1F+1F, exec("return 1F+1F;"));
        assertEquals(1F+2F, exec("return 1F+2F;"));
        assertEquals(5F+10F, exec("return 5F+10F;"));
        assertEquals(1F+1F+2F, exec("return 1F+1F+2F;"));
        assertEquals((1F+1F)+2F, exec("return (1F+1F)+2F;"));
        assertEquals(1F+(1F+2F), exec("return 1F+(1F+2F);"));
        assertEquals(0F+1F, exec("return 0F+1F;"));
        assertEquals(1F+0F, exec("return 1F+0F;"));
        assertEquals(0F+0F, exec("return 0F+0F;"));
    }

    public void testDouble() throws Exception {
        assertEquals(1.0+1.0, exec("double x = 1.0; double y = 1.0; return x+y;"));
        assertEquals(1.0+2.0, exec("double x = 1.0; double y = 2.0; return x+y;"));
        assertEquals(5.0+10.0, exec("double x = 5.0; double y = 10.0; return x+y;"));
        assertEquals(1.0+1.0+2.0, exec("double x = 1.0; double y = 1.0; double z = 2.0; return x+y+z;"));
        assertEquals((1.0+1.0)+2.0, exec("double x = 1.0; double y = 1.0; double z = 2.0; return (x+y)+z;"));
        assertEquals(1.0+(1.0+2.0), exec("double x = 1.0; double y = 1.0; double z = 2.0; return x+(y+z);"));
        assertEquals(0.0+1.0, exec("double x = 0.0; double y = 1.0; return x+y;"));
        assertEquals(1.0+0.0, exec("double x = 1.0; double y = 0.0; return x+y;"));
        assertEquals(0.0+0.0, exec("double x = 0.0; double y = 0.0; return x+y;"));
    }

    public void testDoubleConst() throws Exception {
        assertEquals(1.0+1.0, exec("return 1.0+1.0;"));
        assertEquals(1.0+2.0, exec("return 1.0+2.0;"));
        assertEquals(5.0+10.0, exec("return 5.0+10.0;"));
        assertEquals(1.0+1.0+2.0, exec("return 1.0+1.0+2.0;"));
        assertEquals((1.0+1.0)+2.0, exec("return (1.0+1.0)+2.0;"));
        assertEquals(1.0+(1.0+2.0), exec("return 1.0+(1.0+2.0);"));
        assertEquals(0.0+1.0, exec("return 0.0+1.0;"));
        assertEquals(1.0+0.0, exec("return 1.0+0.0;"));
        assertEquals(0.0+0.0, exec("return 0.0+0.0;"));
    }

    public void testDef() {
        assertEquals(2, exec("def x = (byte)1; def y = (byte)1; return x + y"));
        assertEquals(2, exec("def x = (short)1; def y = (byte)1; return x + y"));
        assertEquals(2, exec("def x = (char)1; def y = (byte)1; return x + y"));
        assertEquals(2, exec("def x = (int)1; def y = (byte)1; return x + y"));
        assertEquals(2L, exec("def x = (long)1; def y = (byte)1; return x + y"));
        assertEquals(2F, exec("def x = (float)1; def y = (byte)1; return x + y"));
        assertEquals(2D, exec("def x = (double)1; def y = (byte)1; return x + y"));

        assertEquals(2, exec("def x = (byte)1; def y = (short)1; return x + y"));
        assertEquals(2, exec("def x = (short)1; def y = (short)1; return x + y"));
        assertEquals(2, exec("def x = (char)1; def y = (short)1; return x + y"));
        assertEquals(2, exec("def x = (int)1; def y = (short)1; return x + y"));
        assertEquals(2L, exec("def x = (long)1; def y = (short)1; return x + y"));
        assertEquals(2F, exec("def x = (float)1; def y = (short)1; return x + y"));
        assertEquals(2D, exec("def x = (double)1; def y = (short)1; return x + y"));

        assertEquals(2, exec("def x = (byte)1; def y = (char)1; return x + y"));
        assertEquals(2, exec("def x = (short)1; def y = (char)1; return x + y"));
        assertEquals(2, exec("def x = (char)1; def y = (char)1; return x + y"));
        assertEquals(2, exec("def x = (int)1; def y = (char)1; return x + y"));
        assertEquals(2L, exec("def x = (long)1; def y = (char)1; return x + y"));
        assertEquals(2F, exec("def x = (float)1; def y = (char)1; return x + y"));
        assertEquals(2D, exec("def x = (double)1; def y = (char)1; return x + y"));

        assertEquals(2, exec("def x = (byte)1; def y = (int)1; return x + y"));
        assertEquals(2, exec("def x = (short)1; def y = (int)1; return x + y"));
        assertEquals(2, exec("def x = (char)1; def y = (int)1; return x + y"));
        assertEquals(2, exec("def x = (int)1; def y = (int)1; return x + y"));
        assertEquals(2L, exec("def x = (long)1; def y = (int)1; return x + y"));
        assertEquals(2F, exec("def x = (float)1; def y = (int)1; return x + y"));
        assertEquals(2D, exec("def x = (double)1; def y = (int)1; return x + y"));

        assertEquals(2L, exec("def x = (byte)1; def y = (long)1; return x + y"));
        assertEquals(2L, exec("def x = (short)1; def y = (long)1; return x + y"));
        assertEquals(2L, exec("def x = (char)1; def y = (long)1; return x + y"));
        assertEquals(2L, exec("def x = (int)1; def y = (long)1; return x + y"));
        assertEquals(2L, exec("def x = (long)1; def y = (long)1; return x + y"));
        assertEquals(2F, exec("def x = (float)1; def y = (long)1; return x + y"));
        assertEquals(2D, exec("def x = (double)1; def y = (long)1; return x + y"));

        assertEquals(2F, exec("def x = (byte)1; def y = (float)1; return x + y"));
        assertEquals(2F, exec("def x = (short)1; def y = (float)1; return x + y"));
        assertEquals(2F, exec("def x = (char)1; def y = (float)1; return x + y"));
        assertEquals(2F, exec("def x = (int)1; def y = (float)1; return x + y"));
        assertEquals(2F, exec("def x = (long)1; def y = (float)1; return x + y"));
        assertEquals(2F, exec("def x = (float)1; def y = (float)1; return x + y"));
        assertEquals(2D, exec("def x = (double)1; def y = (float)1; return x + y"));

        assertEquals(2D, exec("def x = (byte)1; def y = (double)1; return x + y"));
        assertEquals(2D, exec("def x = (short)1; def y = (double)1; return x + y"));
        assertEquals(2D, exec("def x = (char)1; def y = (double)1; return x + y"));
        assertEquals(2D, exec("def x = (int)1; def y = (double)1; return x + y"));
        assertEquals(2D, exec("def x = (long)1; def y = (double)1; return x + y"));
        assertEquals(2D, exec("def x = (float)1; def y = (double)1; return x + y"));
        assertEquals(2D, exec("def x = (double)1; def y = (double)1; return x + y"));
    }

    public void testDefTypedLHS() {
        assertEquals(2, exec("byte x = (byte)1; def y = (byte)1; return x + y"));
        assertEquals(2, exec("short x = (short)1; def y = (byte)1; return x + y"));
        assertEquals(2, exec("char x = (char)1; def y = (byte)1; return x + y"));
        assertEquals(2, exec("int x = (int)1; def y = (byte)1; return x + y"));
        assertEquals(2L, exec("long x = (long)1; def y = (byte)1; return x + y"));
        assertEquals(2F, exec("float x = (float)1; def y = (byte)1; return x + y"));
        assertEquals(2D, exec("double x = (double)1; def y = (byte)1; return x + y"));

        assertEquals(2, exec("byte x = (byte)1; def y = (short)1; return x + y"));
        assertEquals(2, exec("short x = (short)1; def y = (short)1; return x + y"));
        assertEquals(2, exec("char x = (char)1; def y = (short)1; return x + y"));
        assertEquals(2, exec("int x = (int)1; def y = (short)1; return x + y"));
        assertEquals(2L, exec("long x = (long)1; def y = (short)1; return x + y"));
        assertEquals(2F, exec("float x = (float)1; def y = (short)1; return x + y"));
        assertEquals(2D, exec("double x = (double)1; def y = (short)1; return x + y"));

        assertEquals(2, exec("byte x = (byte)1; def y = (char)1; return x + y"));
        assertEquals(2, exec("short x = (short)1; def y = (char)1; return x + y"));
        assertEquals(2, exec("char x = (char)1; def y = (char)1; return x + y"));
        assertEquals(2, exec("int x = (int)1; def y = (char)1; return x + y"));
        assertEquals(2L, exec("long x = (long)1; def y = (char)1; return x + y"));
        assertEquals(2F, exec("float x = (float)1; def y = (char)1; return x + y"));
        assertEquals(2D, exec("double x = (double)1; def y = (char)1; return x + y"));

        assertEquals(2, exec("byte x = (byte)1; def y = (int)1; return x + y"));
        assertEquals(2, exec("short x = (short)1; def y = (int)1; return x + y"));
        assertEquals(2, exec("char x = (char)1; def y = (int)1; return x + y"));
        assertEquals(2, exec("int x = (int)1; def y = (int)1; return x + y"));
        assertEquals(2L, exec("long x = (long)1; def y = (int)1; return x + y"));
        assertEquals(2F, exec("float x = (float)1; def y = (int)1; return x + y"));
        assertEquals(2D, exec("double x = (double)1; def y = (int)1; return x + y"));

        assertEquals(2L, exec("byte x = (byte)1; def y = (long)1; return x + y"));
        assertEquals(2L, exec("short x = (short)1; def y = (long)1; return x + y"));
        assertEquals(2L, exec("char x = (char)1; def y = (long)1; return x + y"));
        assertEquals(2L, exec("int x = (int)1; def y = (long)1; return x + y"));
        assertEquals(2L, exec("long x = (long)1; def y = (long)1; return x + y"));
        assertEquals(2F, exec("float x = (float)1; def y = (long)1; return x + y"));
        assertEquals(2D, exec("double x = (double)1; def y = (long)1; return x + y"));

        assertEquals(2F, exec("byte x = (byte)1; def y = (float)1; return x + y"));
        assertEquals(2F, exec("short x = (short)1; def y = (float)1; return x + y"));
        assertEquals(2F, exec("char x = (char)1; def y = (float)1; return x + y"));
        assertEquals(2F, exec("int x = (int)1; def y = (float)1; return x + y"));
        assertEquals(2F, exec("long x = (long)1; def y = (float)1; return x + y"));
        assertEquals(2F, exec("float x = (float)1; def y = (float)1; return x + y"));
        assertEquals(2D, exec("double x = (double)1; def y = (float)1; return x + y"));

        assertEquals(2D, exec("byte x = (byte)1; def y = (double)1; return x + y"));
        assertEquals(2D, exec("short x = (short)1; def y = (double)1; return x + y"));
        assertEquals(2D, exec("char x = (char)1; def y = (double)1; return x + y"));
        assertEquals(2D, exec("int x = (int)1; def y = (double)1; return x + y"));
        assertEquals(2D, exec("long x = (long)1; def y = (double)1; return x + y"));
        assertEquals(2D, exec("float x = (float)1; def y = (double)1; return x + y"));
        assertEquals(2D, exec("double x = (double)1; def y = (double)1; return x + y"));
    }

    public void testDefTypedRHS() {
        assertEquals(2, exec("def x = (byte)1; byte y = (byte)1; return x + y"));
        assertEquals(2, exec("def x = (short)1; byte y = (byte)1; return x + y"));
        assertEquals(2, exec("def x = (char)1; byte y = (byte)1; return x + y"));
        assertEquals(2, exec("def x = (int)1; byte y = (byte)1; return x + y"));
        assertEquals(2L, exec("def x = (long)1; byte y = (byte)1; return x + y"));
        assertEquals(2F, exec("def x = (float)1; byte y = (byte)1; return x + y"));
        assertEquals(2D, exec("def x = (double)1; byte y = (byte)1; return x + y"));

        assertEquals(2, exec("def x = (byte)1; short y = (short)1; return x + y"));
        assertEquals(2, exec("def x = (short)1; short y = (short)1; return x + y"));
        assertEquals(2, exec("def x = (char)1; short y = (short)1; return x + y"));
        assertEquals(2, exec("def x = (int)1; short y = (short)1; return x + y"));
        assertEquals(2L, exec("def x = (long)1; short y = (short)1; return x + y"));
        assertEquals(2F, exec("def x = (float)1; short y = (short)1; return x + y"));
        assertEquals(2D, exec("def x = (double)1; short y = (short)1; return x + y"));

        assertEquals(2, exec("def x = (byte)1; char y = (char)1; return x + y"));
        assertEquals(2, exec("def x = (short)1; char y = (char)1; return x + y"));
        assertEquals(2, exec("def x = (char)1; char y = (char)1; return x + y"));
        assertEquals(2, exec("def x = (int)1; char y = (char)1; return x + y"));
        assertEquals(2L, exec("def x = (long)1; char y = (char)1; return x + y"));
        assertEquals(2F, exec("def x = (float)1; char y = (char)1; return x + y"));
        assertEquals(2D, exec("def x = (double)1; char y = (char)1; return x + y"));

        assertEquals(2, exec("def x = (byte)1; int y = (int)1; return x + y"));
        assertEquals(2, exec("def x = (short)1; int y = (int)1; return x + y"));
        assertEquals(2, exec("def x = (char)1; int y = (int)1; return x + y"));
        assertEquals(2, exec("def x = (int)1; int y = (int)1; return x + y"));
        assertEquals(2L, exec("def x = (long)1; int y = (int)1; return x + y"));
        assertEquals(2F, exec("def x = (float)1; int y = (int)1; return x + y"));
        assertEquals(2D, exec("def x = (double)1; int y = (int)1; return x + y"));

        assertEquals(2L, exec("def x = (byte)1; long y = (long)1; return x + y"));
        assertEquals(2L, exec("def x = (short)1; long y = (long)1; return x + y"));
        assertEquals(2L, exec("def x = (char)1; long y = (long)1; return x + y"));
        assertEquals(2L, exec("def x = (int)1; long y = (long)1; return x + y"));
        assertEquals(2L, exec("def x = (long)1; long y = (long)1; return x + y"));
        assertEquals(2F, exec("def x = (float)1; long y = (long)1; return x + y"));
        assertEquals(2D, exec("def x = (double)1; long y = (long)1; return x + y"));

        assertEquals(2F, exec("def x = (byte)1; float y = (float)1; return x + y"));
        assertEquals(2F, exec("def x = (short)1; float y = (float)1; return x + y"));
        assertEquals(2F, exec("def x = (char)1; float y = (float)1; return x + y"));
        assertEquals(2F, exec("def x = (int)1; float y = (float)1; return x + y"));
        assertEquals(2F, exec("def x = (long)1; float y = (float)1; return x + y"));
        assertEquals(2F, exec("def x = (float)1; float y = (float)1; return x + y"));
        assertEquals(2D, exec("def x = (double)1; float y = (float)1; return x + y"));

        assertEquals(2D, exec("def x = (byte)1; double y = (double)1; return x + y"));
        assertEquals(2D, exec("def x = (short)1; double y = (double)1; return x + y"));
        assertEquals(2D, exec("def x = (char)1; double y = (double)1; return x + y"));
        assertEquals(2D, exec("def x = (int)1; double y = (double)1; return x + y"));
        assertEquals(2D, exec("def x = (long)1; double y = (double)1; return x + y"));
        assertEquals(2D, exec("def x = (float)1; double y = (double)1; return x + y"));
        assertEquals(2D, exec("def x = (double)1; double y = (double)1; return x + y"));
    }

    public void testDefNulls() {
        expectScriptThrows(NullPointerException.class, () -> {
            exec("def x = null; int y = 1; return x + y");
        });
        expectScriptThrows(NullPointerException.class, () -> {
            exec("int x = 1; def y = null; return x + y");
        });
        expectScriptThrows(NullPointerException.class, () -> {
            exec("def x = null; def y = 1; return x + y");
        });
    }

    public void testCompoundAssignment() {
        // byte
        assertEquals((byte) 15, exec("byte x = 5; x += 10; return x;"));
        assertEquals((byte) -5, exec("byte x = 5; x += -10; return x;"));

        // short
        assertEquals((short) 15, exec("short x = 5; x += 10; return x;"));
        assertEquals((short) -5, exec("short x = 5; x += -10; return x;"));
        // char
        assertEquals((char) 15, exec("char x = 5; x += 10; return x;"));
        assertEquals((char) 5, exec("char x = 10; x += -5; return x;"));
        // int
        assertEquals(15, exec("int x = 5; x += 10; return x;"));
        assertEquals(-5, exec("int x = 5; x += -10; return x;"));
        // long
        assertEquals(15L, exec("long x = 5; x += 10; return x;"));
        assertEquals(-5L, exec("long x = 5; x += -10; return x;"));
        // float
        assertEquals(15F, exec("float x = 5f; x += 10; return x;"));
        assertEquals(-5F, exec("float x = 5f; x += -10; return x;"));
        // double
        assertEquals(15D, exec("double x = 5.0; x += 10; return x;"));
        assertEquals(-5D, exec("double x = 5.0; x += -10; return x;"));
    }

    public void testDefCompoundAssignmentLHS() {
        // byte
        assertEquals((byte) 15, exec("def x = (byte)5; x += 10; return x;"));
        assertEquals((byte) -5, exec("def x = (byte)5; x += -10; return x;"));

        // short
        assertEquals((short) 15, exec("def x = (short)5; x += 10; return x;"));
        assertEquals((short) -5, exec("def x = (short)5; x += -10; return x;"));
        // char
        assertEquals((char) 15, exec("def x = (char)5; x += 10; return x;"));
        assertEquals((char) 5, exec("def x = (char)10; x += -5; return x;"));
        // int
        assertEquals(15, exec("def x = 5; x += 10; return x;"));
        assertEquals(-5, exec("def x = 5; x += -10; return x;"));
        // long
        assertEquals(15L, exec("def x = 5L; x += 10; return x;"));
        assertEquals(-5L, exec("def x = 5L; x += -10; return x;"));
        // float
        assertEquals(15F, exec("def x = 5f; x += 10; return x;"));
        assertEquals(-5F, exec("def x = 5f; x += -10; return x;"));
        // double
        assertEquals(15D, exec("def x = 5.0; x += 10; return x;"));
        assertEquals(-5D, exec("def x = 5.0; x += -10; return x;"));
    }

    public void testDefCompoundAssignmentRHS() {
        // byte
        assertEquals((byte) 15, exec("byte x = 5; def y = 10; x += y; return x;"));
        assertEquals((byte) -5, exec("byte x = 5; def y = -10; x += y; return x;"));

        // short
        assertEquals((short) 15, exec("short x = 5; def y = 10; x += y; return x;"));
        assertEquals((short) -5, exec("short x = 5; def y = -10; x += y; return x;"));
        // char
        assertEquals((char) 15, exec("char x = 5; def y = 10; x += y; return x;"));
        assertEquals((char) 5, exec("char x = 10; def y = -5; x += y; return x;"));
        // int
        assertEquals(15, exec("int x = 5; def y = 10; x += y; return x;"));
        assertEquals(-5, exec("int x = 5; def y = -10; x += y; return x;"));
        // long
        assertEquals(15L, exec("long x = 5; def y = 10; x += y; return x;"));
        assertEquals(-5L, exec("long x = 5; def y = -10; x += y; return x;"));
        // float
        assertEquals(15F, exec("float x = 5f; def y = 10; x += y; return x;"));
        assertEquals(-5F, exec("float x = 5f; def y = -10; x += y; return x;"));
        // double
        assertEquals(15D, exec("double x = 5.0; def y = 10; x += y; return x;"));
        assertEquals(-5D, exec("double x = 5.0; def y = -10; x += y; return x;"));
    }
}
