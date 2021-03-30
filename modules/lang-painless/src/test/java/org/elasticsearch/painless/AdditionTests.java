/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.painless;

import java.util.HashMap;
import java.util.Map;

/** Tests for addition operator across all types */
//TODO: NaN/Inf/overflow/...
public class AdditionTests extends ScriptTestCase {

    // this method takes in the source of a script and will
    // convert it from a Kibana scripted field to a runtime
    // field replacing return with emit where appropriate
    // within the main body
    // this conversion method assumes all source has correct
    // syntax and is semantically correct
    public static String replaceReturnWithEmit(String source) {
        // a skip list where characters from key to value
        // are either comments or strings
        Map<Integer, Integer> forward = new HashMap<>();

        // a reverse skip list where characters from key to value
        // are either comments or strings
        Map<Integer, Integer> reverse = new HashMap<>();

        // ---- START FOR FILLING OUT SKIP LISTS ---- //
        char current;
        char previous = 0;

        for (int i = 0; i < source.length(); i++) {
            current = source.charAt(i);

            // add a skip of a // comment
            if (previous == '/' && current == '/') {
                int j = i - 1;
                while (++i < source.length()) {
                    current = source.charAt(i);

                    if (current == '\n' || current == '\r' || i + 1 >= source.length()) {
                        forward.put(j, i);
                        reverse.put(i, j);
                        break;
                    }
                }
            // add a skip of a /* comment
            } else if (previous == '/' && current == '*') {
                int j = i - 1;
                while (++i < source.length()) {
                    previous = current;
                    current = source.charAt(i);

                    if (previous == '*' && current == '/') {
                        forward.put(j, i);
                        reverse.put(i, j);
                        break;
                    }
                }
            // add a skip of a double-quoted string
            } else if (current == '"') {
                int j = i;
                while (++i < source.length()) {
                    previous = current;
                    current = source.charAt(i);

                    if (previous != '\\' && current == '"') {
                        forward.put(j, i);
                        reverse.put(i, j);
                        break;
                    }
                }
            // add a skip of a single-quoted string
            } else if (current == '\'') {
                int j = i;
                while (++i < source.length()) {
                    previous = current;
                    current = source.charAt(i);

                    if (previous != '\\' && current == '\'') {
                        forward.put(j, i);
                        reverse.put(i, j);
                        break;
                    }
                }
            }

            previous = current;
        }

        // ---- END FOR FILLING OUT SKIP LISTS ---- //

        // ---- START OF MARKING THE END OF USER-DEFINED FUNCTIONS //
        // returns in user-defined functions are valid so we figure
        // out where they end and main body begins and we can skip
        // replacing returns with emits on this entire section

        int function = -1;

        for (int i = 0; i < source.length(); ++i) {
            // check for a skip and if there is one move forward
            Integer skip = forward.get(i);

            if (skip != null) {
                i = skip;
                continue;
            }

            // ignore white space
            if (Character.isWhitespace(source.charAt(i))) {
                continue;
            }

            current = source.charAt(i);

            // from here we look for a type, followed by an id,
            // followed by a set of parenthesis, followed by a set of
            // brackets
            // if anyone of the serialized criteria above is not met
            // we know we are in the main body of a script as long
            // as that script is able to be successfully compiled

            // we first search for a type
            boolean typefound = true;
            boolean start = true;

            while (true) {
                // check for the first character of a type
                if (start) {
                    if ((
                            current >= 'a' && current <= 'z' ||
                                    current >= 'A' && current <= 'Z' ||
                                    current == '_'
                    ) == false) {
                        // this is not a type, stop checking
                        typefound = false;
                        break;
                    } else {
                        // found the first character of a type
                        start = false;
                    }
                // check for any subsequent characters of a type
                } else if ((
                        current >= 'a' && current <= 'z' ||
                                current >= 'A' && current <= 'Z' ||
                                current >= '0' && current <= '9' ||
                                current == '.' || current == '_'
                ) == false) {
                    // a type was found so move onto the next check
                    break;
                }

                ++i;

                if (i >= source.length()) {
                    // this is not a type, stop checking
                    break;
                }

                if (current == '.') {
                    // a dot was found so we must check for the first character of a type again
                    start = true;
                }

                current = source.charAt(i);
            }

            // if a type was not found we are already in the main body
            // so stop checking
            if (i >= source.length() || typefound == false) {
                break;
            }

            // a type may end with brackets, so we check for those

            // we repurpose start here to mean we are an opening bracket
            // or possibly no bracket at all
            start = true;

            for (; i < source.length(); ++i) {
                // check for a skip and if there is one move forward
                skip = forward.get(i);

                if (skip != null) {
                    i = skip;
                    continue;
                }

                // ignore white space
                if (Character.isWhitespace(source.charAt(i))) {
                    continue;
                }

                current = source.charAt(i);

                // we found an opening bracket and require a closing bracket
                // for this to be a type, set start to false
                if (start && current == '[') {
                    start = false;
                // we found a closing bracket, if this matches an opening
                // bracket, set start back to true
                } else if (start == false && current == ']') {
                    start = true;
                // no more brackets so we have reached the end of a possible type
                } else {
                    break;
                }
            }

            // if a type was not found we are already in the main body
            // so stop checking
            if (i >= source.length() || start == false) {
                break;
            }

            // we now check for a method name
            boolean idfound = true;
            start = true;

            while (true) {
                // check for the first character of a method name
                if (start) {
                    if ((
                            current >= 'a' && current <= 'z' ||
                                    current >= 'A' && current <= 'Z' ||
                                    current == '_'
                    ) == false) {
                        // this is not a method name, stop checking
                        idfound = false;
                        break;
                    } else {
                        // found the first character of a type
                        start = false;
                    }
                // check for any subsequent characters of a type
                } else if ((
                        current >= 'a' && current <= 'z' ||
                                current >= 'A' && current <= 'Z' ||
                                current >= '0' && current <= '9' ||
                                current == '_'
                ) == false) {
                    // a method name was found so move onto the next check
                    break;
                }

                ++i;

                if (i >= source.length()) {
                    // this is not a type, stop checking
                    break;
                }

                current = source.charAt(i);
            }

            // if a method name was not found we are already in the main body
            // so stop checking
            if (i >= source.length() || idfound == false) {
                break;
            }

            // check for the opening parenthesis followed by a closing parenthesis now
            // along with the opening and closing braces

            // skip any comments and whitespace between the method name and
            // the opening parenthesis
            for (; i < source.length(); ++i) {
                skip = forward.get(i);

                if (skip != null) {
                    i = skip;
                    continue;
                }

                if (Character.isWhitespace(source.charAt(i)) == false) {
                    break;
                }
            }

            // if no opening parenthesis is found we are in the main body
            if (i > source.length()) {
                break;
            }

            current = source.charAt(i);

            int braceCount = -1;
            boolean headerfound = false;

            // if we find an opening parenthesis look for the rest
            // of the method header and method body
            if (current == '(') {
                // skip whitespace and comments
                for (++i; i < source.length(); ++i) {
                    skip = forward.get(i);

                    if (skip != null) {
                        i = skip;
                        continue;
                    }

                    if (Character.isWhitespace(source.charAt(i))) {
                        continue;
                    }

                    current = source.charAt(i);

                    if (current == ')' && headerfound == false) {
                        // we found the closing parenthesis so start looking
                        // for the opening and closing braces
                        headerfound = true;
                    } else if (headerfound && braceCount == -1) {
                        if (current != '{') {
                            // if we don't find an opening brace were in the main body
                            break;
                        }

                        // we found an opening brace so start looking
                        // for the closing one
                        braceCount = 1;
                    } else if (braceCount > 0) {
                        if (current == '{') {
                            ++braceCount;
                        } else if (current == '}') {
                            --braceCount;

                            if (braceCount == 0) {
                                // we found the closing brace and have found
                                // a user-defined function, mark the position
                                function = i;
                                break;
                            }
                        }
                    }
                }
            }

            // if we did not find a function, we are in the main body
            // if we did find a function, look for another
            if (headerfound == false || braceCount == -1) {
                break;
            }
        }

        // ---- END OF MARKING THE END OF USER-DEFINED FUNCTIONS //

        // ---- START OF FINDING THE END OF THE LAST STATEMENT IN THE MAIN BODY //
        // we look for the end of the last statement in the main body by going
        // through the script in reverse and removing any whitespace, comments,
        // and semicolons
        int end = -1;

        // find the end of the last statement
        for (int i = source.length() - 1; i > function; --i) {
            // skip comments
            Integer skip = reverse.get(i);

            if (skip != null) {
                i = skip;
                continue;
            }

            // skip whitespace and semicolons
            if (Character.isWhitespace(source.charAt(i)) || source.charAt(i) == ';') {
                continue;
            }

            // mark the end of the final statement and stop looking for it
            end = i;
            break;
        }

        // ---- END OF FINDING THE END OF THE LAST STATEMENT IN THE MAIN BODY //

        // ---- START OF FINDING THE START OF THE LAST STATEMENT IN THE MAIN BODY //
        // we look for the start of the last statement in the main body by going
        // through the script in reverse from our end mark and look for a sentinel
        // either as a semicolon or a closing brace to indicate a prior statement

        previous = 0;
        int braceCount = 0;
        int parenCount = 0;
        int start = function + 1;
        boolean finalrtn = false;

        // find the start of the last statement
        for (int i = end; i > function; --i) {
            // skip comments and strings
            Integer skip = reverse.get(i);

            if (skip != null) {
                i = skip;
                continue;
            }

            // skip whitespace
            if (Character.isWhitespace(source.charAt(i))) {
                continue;
            }

            current = source.charAt(i);

            // keep a count of parenthesis as we only look for
            // sentinels for the start of a statement when the parenthesis count is 0

            if (current == '(') {
                ++parenCount;
            }

            if (current == ')') {
                --parenCount;
            }

            if (parenCount == 0) {
                // check for a return keyword
                boolean endReturn =
                        current == 'n' && i - 5 > function &&
                        source.charAt(i - 1) == 'r' &&
                        source.charAt(i - 2) == 'u' &&
                        source.charAt(i - 3) == 't' &&
                        source.charAt(i - 4) == 'e' &&
                        source.charAt(i - 5) == 'r';
                boolean isIdChar = false;
                if (endReturn && i - 6 > function) {
                    char idchar = source.charAt(i - 6);
                    isIdChar = idchar >= 'a' && idchar <= 'z' ||
                            idchar >= 'A' && idchar <= 'Z' ||
                            idchar >= '0' && idchar <= '9' ||
                            idchar == '_' || idchar == '.';
                }
                if (endReturn && i + 1 < end) {
                    char idchar = source.charAt(i + 1);
                    isIdChar |= idchar >= 'a' && idchar <= 'z' ||
                            idchar >= 'A' && idchar <= 'Z' ||
                            idchar >= '0' && idchar <= '9' ||
                            idchar == '_' || idchar == '.';
                }

                if (endReturn && isIdChar == false) {
                    // we found a return keyword and need to mark this
                    // as we need to replace it with emit upon rebuilding
                    // the script source
                    // this is a sentinel as the start of the last statement
                    start = i - 5;
                    finalrtn = true;
                    break;
                } else if (current == ';') {
                    // we found the prior statement and mark the start of
                    // last one
                    start = i + 1;
                    break;
                } else if (current == '}') {
                    // we found the prior statement and mark the start of
                    // the last one
                    // we don't yet break as we have to ensure this isn't a false
                    // positive of an array initializer
                    start = i + 1;
                    braceCount++;
                } else if (current == '{') {
                    braceCount--;
                } else if (braceCount == 0 && previous == '{') {
                    if (current == ']') {
                        // we found an array initializer so this isn't actually
                        // the start of the last statement yet, keep looking
                        start = function + 1;
                    } else {
                        // this is a legitimate block so we have figured out
                        // the start of the last statement
                        break;
                    }
                }
            }

            previous = current;
        }

        // ---- END OF FINDING THE START OF THE LAST STATEMENT IN THE MAIN BODY //

        // ---- START OF REPLACING RETURN WITH EMIT IN THE MAIN BODY //
        // we go through the main body now and replace 'return ...' with
        // 'emit(...); return;' with the exception of the last statement as
        // it does not require an additional return statement

        StringBuilder emits = new StringBuilder();

        if (function != -1) {
            emits.append(source, 0, function + 1);
        }

        previous = 0;
        int previousI = 0;
        braceCount = 0;
        parenCount = 0;
        boolean closebrace = false;
        boolean rtn = false;

        // replace return(s) with emit(s)
        for (int i = function + 1; i < start; ++i) {
            // skip comments and strings
            Integer skip = forward.get(i);

            if (skip != null) {
                emits.append(source, i, skip + 1);
                i = skip;
                continue;
            }

            // skip whitespace
            if (Character.isWhitespace(source.charAt(i))) {
                emits.append(source.charAt(i));
                continue;
            }

            current = source.charAt(i);

            // keep a count of parenthesis as we only look for
            // sentinels for the start of a statement when the parenthesis count is 0

            if (current == '(') {
                ++parenCount;
            }

            if (current == ')') {
                --parenCount;
            }

            if (parenCount == 0) {
                // check for a return statement with protection
                // of possibly being part of an id
                boolean endReturn =
                        current == 'r' && i + 5 < start &&
                                source.charAt(i + 1) == 'e' &&
                                source.charAt(i + 2) == 't' &&
                                source.charAt(i + 3) == 'u' &&
                                source.charAt(i + 4) == 'r' &&
                                source.charAt(i + 5) == 'n';
                boolean isIdChar = false;
                if (endReturn && i + 6 < start) {
                    char idchar = source.charAt(i + 6);
                    isIdChar = idchar >= 'a' && idchar <= 'z' ||
                            idchar >= 'A' && idchar <= 'Z' ||
                            idchar >= '0' && idchar <= '9' ||
                            idchar == '_' || idchar == '.';
                }
                if (endReturn && i - 1 > function) {
                    char idchar = source.charAt(i - 1);
                    isIdChar |= idchar >= 'a' && idchar <= 'z' ||
                            idchar >= 'A' && idchar <= 'Z' ||
                            idchar >= '0' && idchar <= '9' ||
                            idchar == '_' || idchar == '.';
                }

                if (endReturn && isIdChar == false) {
                    // we found a return to replace
                    i += 6;
                    rtn = true;
                    // check to see if its in a block
                    // if its not we have to add additional braces as
                    // replace a single statement with multiple statements
                    if (previous == ')' ||
                            previous == 'e' && previousI - 3 > function &&
                            source.charAt(previousI - 1) == 's' &&
                            source.charAt(previousI - 2) == 'l' &&
                            source.charAt(previousI - 3) == 'e' &&
                                    (previousI - 4 > function &&
                                            source.charAt(previousI - 4) >= 'a' && source.charAt(previousI - 4) <= 'z' ||
                                            source.charAt(previousI - 4) >= 'A' && source.charAt(previousI - 4) <= 'Z' ||
                                            source.charAt(previousI - 4) >= '0' && source.charAt(previousI - 4) <= '9' ||
                                            source.charAt(previousI - 4) == '_' || source.charAt(previousI - 4) == '.') == false
                    ) {
                        // emit an additional brace to create
                        // a multiple statement block
                        closebrace = true;
                        emits.append('{');
                    }
                    // replace return with emit
                    emits.append("emit(");
                } else if (current == '{') {
                    ++braceCount;
                    emits.append(current);
                } else if (current == '}') {
                    --braceCount;

                    if (braceCount == 0 && rtn) {
                        // close the emit method call if
                        // a closing brace is found as part of the return
                        // statement
                        rtn = false;
                        emits.append("); return;");
                    }

                    emits.append(current);
                } else if (rtn && current == ';') {
                    // close the emit method call if
                    // a semicolon is found as part of the return
                    // statement
                    rtn = false;
                    emits.append("); return;");
                    if (closebrace) {
                        // add a closing brace if an opening
                        // brace was added
                        emits.append("}");
                        closebrace = false;
                    }
                } else {
                    emits.append(current);
                }
            } else {
                emits.append(current);
            }

            previous = current;
            previousI = i;
        }

        // if the last statement has a return remove it by
        // moving the start marker forward by 6 characters
        if (finalrtn) {
            start += 6;
        } else {
            emits.append(" ");
        }

        // skip whitespace to close emit on the first relevant character
        while (start < source.length()) {
            if (Character.isWhitespace(source.charAt(start)) == false) {
                break;
            }

            ++start;
        }

        // append an emit on the last statement under all scenarios
        emits.append("emit(");
        // append the last statement
        emits.append(source, start, end + 1);
        // append the closing parenthesis for an emit
        emits.append(")");
        // append any trailing semicolons or comments after the last statement
        emits.append(source.substring(end + 1));

        return emits.toString();
    }

    public void testReplace() throws Exception {
        String original =
                "int get(/* blah */){return 5;}" +
                "org.elastic.type [] method() {   //test\n }  " +
                "if (x) {return 1} else return 2; " +
                "return /*hello hello*/ get(x -> x, () -> {return 9;}); // comment";
        throw new RuntimeException("\n" + original + "\n\n" + replaceReturnWithEmit(//"return doc['my_field'] + doc['my_field2'] /* test time */ ; // test time"));
                original));
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
