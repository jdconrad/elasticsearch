/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.painless;

import org.elasticsearch.test.ESTestCase;

import java.util.HashMap;
import java.util.Map;

public class MigrationTests extends ESTestCase {

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
                --parenCount;
            }

            if (current == ')') {
                ++parenCount;
            }

            if (parenCount == 0) {
                // check for a return keyword
                boolean endReturn = braceCount == 0 &&
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
                    if (braceCount == 0) {
                        // we found the prior statement and mark the start of
                        // last one
                        start = i + 1;
                    }
                    break;
                } else if (current == '}') {
                    // we found the prior statement and mark the start of
                    // the last one
                    // we don't yet break as we have to ensure this isn't a false
                    // positive of an array initializer
                    if (braceCount == 0) {
                        start = i + 1;
                    }
                    braceCount++;
                } else if (current == '{') {
                    braceCount--;

                    if (braceCount == 0) {
                        while (--i > function) {
                            // skip comments and strings
                            skip = reverse.get(i);

                            if (skip != null) {
                                i = skip;
                                continue;
                            }

                            // skip whitespace
                            if (Character.isWhitespace(source.charAt(i))) {
                                continue;
                            }

                            break;
                        }

                        if (--i > function) {
                            current = source.charAt(i);

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
                }
            }
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
        int rtn = -1;

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
                    rtn = braceCount;
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
                    if (rtn == braceCount) {
                        // close the emit method call if
                        // a closing brace is found as part of the return
                        // statement
                        rtn = -1;
                        emits.append("); return;");
                    }

                    --braceCount;
                    emits.append(current);
                } else if (rtn != -1 && current == ';') {
                    // close the emit method call if
                    // a semicolon is found as part of the return
                    // statement
                    rtn = -1;
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
        } else if (start - 6 > 0) {
            emits.append(' ');
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
        //if (true) throw new RuntimeException(replaceReturnWithEmit("def values = doc['my_field']; int total = 0; " +
        //                        "for (def value : values) {if (value > 10) {return value} else {total += value}} (total + 1) / 2"));

        // 1. basic return
        assertEquals(
                replaceReturnWithEmit("return 1;"),
                "emit(1);"
        );

        // 2. basic return w/o semicolon
        assertEquals(
                replaceReturnWithEmit("return 1"),
                "emit(1)"
        );

        // 3. basic implicit return
        assertEquals(
                replaceReturnWithEmit("1;"),
                "emit(1);"
        );

        // 4. basic implicit return w/o semicolon
        assertEquals(
                replaceReturnWithEmit("1"),
                "emit(1)"
        );

        // 5. basic return w/ operator
        assertEquals(
                replaceReturnWithEmit("return 1 + 2;"),
                "emit(1 + 2);"
        );

        // 6. basic return w/o semicolon w/ operator
        assertEquals(
                replaceReturnWithEmit("return 1 + 2"),
                "emit(1 + 2)"
        );

        // 7. basic implicit return w/ operator
        assertEquals(
                replaceReturnWithEmit("1 + 2;"),
                "emit(1 + 2);"
        );

        // 8. basic implicit return w/o semicolon w/ operator
        assertEquals(
                replaceReturnWithEmit("1 + 2"),
                "emit(1 + 2)"
        );

        // 9. multi-statement basic return w/ operator
        assertEquals(
                replaceReturnWithEmit("int x = 1; return x + 2;"),
                "int x = 1; emit(x + 2);"
        );

        // 10. multi-statement basic return w/o semicolon w/ operator
        assertEquals(
                replaceReturnWithEmit("int x = 1; return x + 2"),
                "int x = 1; emit(x + 2)"
        );

        // 11. multi-statement basic implicit return w/ operator
        assertEquals(
                replaceReturnWithEmit("int x = 1; x + 2;"),
                "int x = 1; emit(x + 2);"
        );

        // 12. multi-statement basic implicit return w/o semicolon w/ operator
        assertEquals(
                replaceReturnWithEmit("int x = 1; x + 2"),
                "int x = 1; emit(x + 2)"
        );

        // 13. multi-statement basic return w/ operator w/ comments
        assertEquals(
                replaceReturnWithEmit("// beginning comment \n int x = 1; return x + /* constant */ 2; // end comment"),
                "// beginning comment \n int x = 1; emit(x + /* constant */ 2); // end comment"
        );

        // 14. multi-statement basic return w/o semicolon w/ operator w/ comments
        assertEquals(
                replaceReturnWithEmit("// beginning comment \n int x = 1; return x + /* constant */ 2 // end comment"),
                "// beginning comment \n int x = 1; emit(x + /* constant */ 2) // end comment"
        );

        // 15. multi-statement basic implicit return w/ operator w/ comments
        assertEquals(
                replaceReturnWithEmit("// beginning comment \n int x = 1; x + /* constant */ 2; // end comment"),
                "// beginning comment \n int x = 1; emit(x + /* constant */ 2); // end comment"
        );

        // 16. multi-statement basic implicit return w/o semicolon w/ operator /w comments
        assertEquals(
                replaceReturnWithEmit("// beginning comment \n int x = 1; x + /* constant */ 2 // end comment"),
                "// beginning comment \n int x = 1; emit(x + /* constant */ 2) // end comment"
        );

        // 17. multi-statement basic return w/ operator w/ comments w/ method
        assertEquals(
                replaceReturnWithEmit("// beginning comment \n int x = 1; return x.toString() + /* constant */ 2; "),
                "// beginning comment \n int x = 1; emit(x.toString() + /* constant */ 2); "
        );

        // 18. multi-statement basic return w/o semicolon w/ operator w/ comments w/ method
        assertEquals(
                replaceReturnWithEmit("// beginning comment \n int x = 1; return x.toString() + /* constant */ 2 "),
                "// beginning comment \n int x = 1; emit(x.toString() + /* constant */ 2) "
        );

        // 19. multi-statement basic implicit return w/ operator w/ comments /w method
        assertEquals(
                replaceReturnWithEmit("// beginning comment \n int x = 1; x.toString() + /* constant */ 2; "),
                "// beginning comment \n int x = 1; emit(x.toString() + /* constant */ 2); "
        );

        // 20. multi-statement basic implicit return w/o semicolon w/ operator /w comments /w method
        assertEquals(
                replaceReturnWithEmit("// beginning comment \n int x = 1; x.toString() + /* constant */ 2 "),
                "// beginning comment \n int x = 1; emit(x.toString() + /* constant */ 2) "
        );

        // 21. multi-statement basic return w/ comments w/ method w/ lambda
        assertEquals(
                replaceReturnWithEmit("// beginning comment \n " +
                        "def x = [1, 2, 4, 12, 7]; return x.stream().mapToInt(x /* crazy comment */ -> x + 1).sum();"),
                "// beginning comment \n def x = [1, 2, 4, 12, 7]; emit(x.stream().mapToInt(x /* crazy comment */ -> x + 1).sum());"
        );

        // 22. multi-statement basic return w/o semicolon w/ comments w/ method w/ lambda
        assertEquals(
                replaceReturnWithEmit("// beginning comment \n " +
                        "def x = [1, 2, 4, 12, 7]; return x.stream().mapToInt(x /* crazy comment */ -> x + 1).sum()"),
                "// beginning comment \n def x = [1, 2, 4, 12, 7]; emit(x.stream().mapToInt(x /* crazy comment */ -> x + 1).sum())"
        );

        // 23. multi-statement basic implicit return w/ operator w/ comments /w method w/ lambda
        assertEquals(
                replaceReturnWithEmit("// beginning comment \n " +
                        "def x = [1, 2, 4, 12, 7]; x.stream().mapToInt(x /* crazy comment */ -> x + 1).sum();"),
                "// beginning comment \n def x = [1, 2, 4, 12, 7]; emit(x.stream().mapToInt(x /* crazy comment */ -> x + 1).sum());"
        );

        // 24. multi-statement basic implicit return w/o semicolon w/ operator /w comments /w method w/ lambda
        assertEquals(
                replaceReturnWithEmit("// beginning comment \n " +
                        "def x = [1, 2, 4, 12, 7]; x.stream().mapToInt(x /* crazy comment */ -> x + 1).sum()"),
                "// beginning comment \n def x = [1, 2, 4, 12, 7]; emit(x.stream().mapToInt(x /* crazy comment */ -> x + 1).sum())"
        );

        // 25. check doc syntax and check against return as a string w/ lambda
        assertEquals(
                replaceReturnWithEmit("def value = 'return'; doc[value] + doc[my_method(() -> {return 'my_' + value})]"),
                "def value = 'return'; emit(doc[value] + doc[my_method(() -> {return 'my_' + value})])"
        );

        // 26. check user defined method and method reference
        assertEquals(
                replaceReturnWithEmit("int my_method(int value) {return value + 1;} my_call(this::my_method)"),
                "int my_method(int value) {return value + 1;} emit(my_call(this::my_method))"
        );

        // 27. check user defined method and method reference
        assertEquals(
                replaceReturnWithEmit("int my_method(int value) {return value + 1;} my_call(this::my_method)"),
                "int my_method(int value) {return value + 1;} emit(my_call(this::my_method))"
        );

        // 28. check loose if block
        assertEquals(
                replaceReturnWithEmit("boolean value = doc['my_field'].value; if (value) return 1; 3"),
                "boolean value = doc['my_field'].value; if (value) {emit(1); return;} emit(3)"
        );

        // 29. check if block
        assertEquals(
                replaceReturnWithEmit("boolean value = doc['my_field'].value; if (value) {return 1;} 3"),
                "boolean value = doc['my_field'].value; if (value) {emit(1); return;} emit(3)"
        );

        // 30. check loose if/else block
        assertEquals(
                replaceReturnWithEmit("boolean value = doc['my_field'].value; if (value) return 1; else return 2; 3"),
                "boolean value = doc['my_field'].value; if (value) {emit(1); return;} else {emit(2); return;} emit(3)"
        );

        // 31. check if/else block
        assertEquals(
                replaceReturnWithEmit("boolean value = doc['my_field'].value; if (value) {return 1;} else return 2; 3"),
                "boolean value = doc['my_field'].value; if (value) {emit(1); return;} else {emit(2); return;} emit(3)"
        );

        // 32. check if/else block
        assertEquals(
                replaceReturnWithEmit("boolean value = doc['my_field'].value; if (value) return 1; else {return 2;} 3"),
                "boolean value = doc['my_field'].value; if (value) {emit(1); return;} else {emit(2); return;} emit(3)"
        );

        // 33. check if/else block
        assertEquals(
                replaceReturnWithEmit("boolean value = doc['my_field'].value; if (value) {return 1;} else {return 2;} 3"),
                "boolean value = doc['my_field'].value; if (value) {emit(1); return;} else {emit(2); return;} emit(3)"
        );

        // 34. check loose while block
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; while (value < 0) ++value; 3"),
                "int value = doc['my_field'].value; while (value < 0) ++value; emit(3)"
        );

        // 35. check while block
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; while (value < 0) {++value;} 3"),
                "int value = doc['my_field'].value; while (value < 0) {++value;} emit(3)"
        );

        // 36. check loose while block w/ return
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; while (value < 0) return ++value; 3"),
                "int value = doc['my_field'].value; while (value < 0) {emit(++value); return;} emit(3)"
        );

        // 37. check while block w/ return
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; while (value < 0) {return ++value;} 3"),
                "int value = doc['my_field'].value; while (value < 0) {emit(++value); return;} emit(3)"
        );

        // 38. check while block w/ multiple statements
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; while (value < 0) {int x = 1; x + value;} 3"),
                "int value = doc['my_field'].value; while (value < 0) {int x = 1; x + value;} emit(3)"
        );

        // 39. check while block w/ return
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; while (value < 0) {int x = 1; return x + value;} 3"),
                "int value = doc['my_field'].value; while (value < 0) {int x = 1; emit(x + value); return;} emit(3)"
        );

        // 40. check while block w/ no semicolon
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; while (value < 0) {++value} 3"),
                "int value = doc['my_field'].value; while (value < 0) {++value} emit(3)"
        );

        // 41. check while block w/ return w/ no semicolon
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; while (value < 0) {return ++value} 3"),
                "int value = doc['my_field'].value; while (value < 0) {emit(++value); return;} emit(3)"
        );

        // 42. check while block w/ multiple statements w/ no semicolon
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; while (value < 0) {int x = 1; x + value} 3"),
                "int value = doc['my_field'].value; while (value < 0) {int x = 1; x + value} emit(3)"
        );

        // 43. check while block w/ return w/ no semicolon
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; while (value < 0) {int x = 1; return x + value} 3"),
                "int value = doc['my_field'].value; while (value < 0) {int x = 1; emit(x + value); return;} emit(3)"
        );

        // 44. check if block w/ no semicolon
        assertEquals(
                replaceReturnWithEmit("boolean value = doc['my_field'].value; if (value) {return 1} 3"),
                "boolean value = doc['my_field'].value; if (value) {emit(1); return;} emit(3)"
        );

        // 45. check if/else block w/ no semicolon
        assertEquals(
                replaceReturnWithEmit("boolean value = doc['my_field'].value; if (value) {return 1} else return 2; 3"),
                "boolean value = doc['my_field'].value; if (value) {emit(1); return;} else {emit(2); return;} emit(3)"
        );

        // 46. check if/else block w/ no semicolon
        assertEquals(
                replaceReturnWithEmit("boolean value = doc['my_field'].value; if (value) return 1; else {return 2} 3"),
                "boolean value = doc['my_field'].value; if (value) {emit(1); return;} else {emit(2); return;} emit(3)"
        );

        // 47. check if/else block w/ no semicolon
        assertEquals(
                replaceReturnWithEmit("boolean value = doc['my_field'].value; if (value) {return 1} else {return 2} 3"),
                "boolean value = doc['my_field'].value; if (value) {emit(1); return;} else {emit(2); return;} emit(3)"
        );

        // 48. check if block w/ multiple statements
        assertEquals(
                replaceReturnWithEmit("boolean value = doc['my_field'].value; if (value) {boolean v2 = value; return v2;} 3"),
                "boolean value = doc['my_field'].value; if (value) {boolean v2 = value; emit(v2); return;} emit(3)"
        );

        // 49. check if/else block w/ multiple statements
        assertEquals(
                replaceReturnWithEmit(
                        "boolean value = doc['my_field'].value; if (value) {boolean v2 = value; return v2;} else return 2; 3"),
                "boolean value = doc['my_field'].value; if (value) {boolean v2 = value; emit(v2); return;} else {emit(2); return;} emit(3)"
        );

        // 50. check if/else block w/ multiple statements
        assertEquals(
                replaceReturnWithEmit(
                        "boolean value = doc['my_field'].value; if (value) return 1; else {boolean v2 = value; return v2;} 3"),
                "boolean value = doc['my_field'].value; if (value) {emit(1); return;} else {boolean v2 = value; emit(v2); return;} emit(3)"
        );

        // 51. check if/else block w/ multiple statements
        assertEquals(
                replaceReturnWithEmit("boolean value = doc['my_field'].value; " +
                        "if (value) {boolean v2 = value; return 1;} else {boolean v2 = value; return 2;} 3"),
                "boolean value = doc['my_field'].value; " +
                        "if (value) {boolean v2 = value; emit(1); return;} else {boolean v2 = value; emit(2); return;} emit(3)"
        );

        // 52. check if block w/ multiple statements w/ no semicolon
        assertEquals(
                replaceReturnWithEmit("boolean value = doc['my_field'].value; if (value) {boolean v2 = value; return v2} value"),
                "boolean value = doc['my_field'].value; if (value) {boolean v2 = value; emit(v2); return;} emit(value)"
        );

        // 53. check if/else block w/ multiple statements w/ no semicolon
        assertEquals(
                replaceReturnWithEmit("boolean value = doc['my_field'].value; " +
                        "if (value) {boolean v2 = value; return v2} else return 2; value"),
                "boolean value = doc['my_field'].value; " +
                        "if (value) {boolean v2 = value; emit(v2); return;} else {emit(2); return;} emit(value)"
        );

        // 54. check if/else block w/ multiple statements w/ no semicolon
        assertEquals(
                replaceReturnWithEmit("boolean value = doc['my_field'].value; " +
                        "if (value) return 1; else {boolean v2 = value; return v2} value"),
                "boolean value = doc['my_field'].value; " +
                        "if (value) {emit(1); return;} else {boolean v2 = value; emit(v2); return;} emit(value)"
        );

        // 55. check if/else block w/ multiple statements w/ no semicolon
        assertEquals(
                replaceReturnWithEmit("boolean value = doc['my_field'].value; " +
                        "if (value) {boolean v2 = value; return v2} else {boolean v2 = value} !value"),
                "boolean value = doc['my_field'].value; " +
                        "if (value) {boolean v2 = value; emit(v2); return;} else {boolean v2 = value} emit(!value)"
        );

        // 56. check loose for block
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; for (int x = 0; x < value; ++x) --value; 3"),
                "int value = doc['my_field'].value; for (int x = 0; x < value; ++x) --value; emit(3)"
        );

        // 57. check for block
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; for (int x = 0; x < value; ++x) {--value;} 3"),
                "int value = doc['my_field'].value; for (int x = 0; x < value; ++x) {--value;} emit(3)"
        );

        // 58. check loose for block w/ return
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; for (int x = 0; x < value; x += 2) return ++value; 3"),
                "int value = doc['my_field'].value; for (int x = 0; x < value; x += 2) {emit(++value); return;} emit(3)"
        );

        // 59. check for block w/ return
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; for (int x = 0; x < value; x += 2) {return ++value;} 3"),
                "int value = doc['my_field'].value; for (int x = 0; x < value; x += 2) {emit(++value); return;} emit(3)"
        );

        // 60. check for block w/ multiple statements
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; for (int x = 0; x < value; x += 2) {int y = 1; x + value;} 3"),
                "int value = doc['my_field'].value; for (int x = 0; x < value; x += 2) {int y = 1; x + value;} emit(3)"
        );

        // 61. check for block w/ multiple statements w/ return
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; " +
                        "for (int x = 0; x < value; x += 2) {int x = 1; return x + value;} 3"),
                "int value = doc['my_field'].value; " +
                        "for (int x = 0; x < value; x += 2) {int x = 1; emit(x + value); return;} emit(3)"
        );

        // 62. check for block w/ no semicolon
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; for (int x = 0; x < value; x += 2) {++value} 3"),
                "int value = doc['my_field'].value; for (int x = 0; x < value; x += 2) {++value} emit(3)"
        );

        // 63. check for block w/ return w/ no semicolon
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; for (int x = 0; x < value; x += 2) {return ++value} 3"),
                "int value = doc['my_field'].value; for (int x = 0; x < value; x += 2) {emit(++value); return;} emit(3)"
        );

        // 64. check for block w/ multiple statements w/ no semicolon
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; for (int x = 0; x < value; x += 2) {int x = 1; x + value} 3"),
                "int value = doc['my_field'].value; for (int x = 0; x < value; x += 2) {int x = 1; x + value} emit(3)"
        );

        // 65. check for block w/ return w/ no semicolon
        assertEquals(
                replaceReturnWithEmit("int value = doc['my_field'].value; " +
                        "for (int x = 0; x < value; x += 2) {int x = 1; return x + value} 3"),
                "int value = doc['my_field'].value; " +
                        "for (int x = 0; x < value; x += 2) {int x = 1; emit(x + value); return;} emit(3)"
        );

        // 66. check loose for each block
        assertEquals(
                replaceReturnWithEmit("def values = doc['my_field']; int total = 0; " +
                        "for (def value : values) total += value; (total + 1) / 2"),
                "def values = doc['my_field']; int total = 0; " +
                        "for (def value : values) total += value; emit((total + 1) / 2)"
        );

        // 67. check for each block
        assertEquals(
                replaceReturnWithEmit("def values = doc['my_field']; int total = 0; " +
                        "for (def value : values) {total += value;} (total + 1) / 2"),
                "def values = doc['my_field']; int total = 0; " +
                        "for (def value : values) {total += value;} emit((total + 1) / 2)"
        );

        // 68. check loose for each block w/ return
        assertEquals(
                replaceReturnWithEmit("def values = doc['my_field']; int total = 0; " +
                        "for (def value : values) return value; (total + 1) / 2"),
                "def values = doc['my_field']; int total = 0; " +
                        "for (def value : values) {emit(value); return;} emit((total + 1) / 2)"
        );

        // 69. check for each block w/ return
        assertEquals(
                replaceReturnWithEmit("def values = doc['my_field']; int total = 0; " +
                        "for (def value : values) {return value;} (total + 1) / 2"),
                "def values = doc['my_field']; int total = 0; " +
                        "for (def value : values) {emit(value); return;} emit((total + 1) / 2)"
        );

        // 70. check for each block w/ multiple statements
        assertEquals(
                replaceReturnWithEmit("def values = doc['my_field']; int total = 0; " +
                        "for (def value : values) {if (value > 10) {value = 10;} else total += value;} (total + 1) / 2"),
                "def values = doc['my_field']; int total = 0; " +
                        "for (def value : values) {if (value > 10) {value = 10;} else total += value;} emit((total + 1) / 2)"
        );

        // 71. check for each block w/ multiple statements w/ return
        assertEquals(
                replaceReturnWithEmit("def values = doc['my_field']; int total = 0; " +
                        "for (def value : values) {if (value > 10) {return value;} else total += value;} (total + 1) / 2"),
                "def values = doc['my_field']; int total = 0; " +
                        "for (def value : values) {if (value > 10) {emit(value); return;} else total += value;} emit((total + 1) / 2)"
        );

        // 72. check for each block w/ no semicolon
        assertEquals(
                replaceReturnWithEmit("def values = doc['my_field']; int total = 0; " +
                        "for (def value : values) {if (value > 10) {return value} total += value} (total + 1) / 2"),
                "def values = doc['my_field']; int total = 0; " +
                        "for (def value : values) {if (value > 10) {emit(value); return;} total += value} emit((total + 1) / 2)"
        );

        // TODO: add for each w/ in tests
        // TODO: do while block tests
        // TODO: try/catch block tests
        // TODO: return as variable tests
        // TODO: multi block main method tests
        // TODO: tests with user-defined functions
        // TODO: multi level block tests
        // TODO: multi level lambda tests
        // TODO: array initializer tests
    }

}
