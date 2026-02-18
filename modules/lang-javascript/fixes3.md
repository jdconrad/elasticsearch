# Test failure summary and fix suggestions (fixes3.md)

This document summarizes the **problems currently causing lang-javascript test failures** after FIX2 (grammar/parser ambiguity) and partial FIX1 (script-only conversions), and suggests how to fix them.

---

## 1. Semantic: "cannot resolve type [let]"

**Symptom:** Scripts like `let x = 1; let y = 2; return x + y;` parse successfully but fail at compile with:

```text
java.lang.IllegalArgumentException: invalid declaration: cannot resolve type [let]
```

**Cause:** The parser correctly treats `let` as a variable-declaration keyword and produces a declaration node. The **semantic phase** (e.g. `DefaultSemanticAnalysisPhase.visitDeclaration`) is interpreting the declaration’s “type” as a type name and trying to resolve it (e.g. via a type resolver or whitelist). For `let` and `const`, the keyword is being passed as the type name, so resolution fails.

**Suggestion:** In the phase that visits variable declarations (likely `DefaultSemanticAnalysisPhase` or a shared declaration visitor):

- Treat declarations whose “type” is the keyword `let` or `const` (or the corresponding token/identifier) as **untyped variable declarations**, not as type names.
- Do not call the type resolver for `let`/`const`; treat them like `var` or as “no type, just variable.” Apply the same scoping and assignment rules as for other variables.

**Where to look:** `DefaultSemanticAnalysisPhase.visitDeclaration` (and any helper that resolves the declaration type); how `var` is handled today (use that as the model for `let`/`const`).

**Verification:** After the change, tests that only failed with “cannot resolve type [let]” (e.g. AdditionTests.testBasics, testInt, testByte, …; BasicExpressionTests.testDeclareVariable, testComp, testBool, …; AliasTests with `let a = ...`) should get past compile and fail only on later steps (e.g. type expectations or runtime) if at all.

---

## 2. Remaining "no viable alternative" (parse errors)

**Symptom:** Tests in **unconverted** classes still fail with:

```text
no viable alternative at input 'x'
```
(or at `'['`, `'L'`, `'F'`, `'a'`, etc.)

**Cause:** Those tests still use Painless-style syntax the JavaScript grammar does not accept: e.g. typed variable declarations (`int x = ...`, `def m = ...`, `TypeName var = ...`), long/float literals (`5L`, `1F`), Painless casts `(byte)1`, or Painless map/array syntax in places the parser doesn’t expect.

**Suggestion:** Continue **FIX1** (script-only fixes; do not extend the JS grammar for Painless-only constructs):

- In each failing test, replace Painless syntax with valid JavaScript: e.g. `int x = 5` → `let x = 5`, `5L` → `5`, `1F` → `1` or `1.0`, `(byte)1` → `1`, `def m = ...` → `let m = ...`, `Map a = [:];` → `let a = {};`, etc.
- For tests that are purely Painless (e.g. primitive arrays, `def[]`, typed for-each), add or keep `@Ignore("Painless-only: <reason>")` instead of converting.

**Classes still likely to need script conversions:** ArrayTests, AugmentationTests, ComparisonTests, BasicStatementTests (beyond testIfStatement), BasicAPITests, and any other that still appears in “no viable alternative” failure output.

**Verification:** After converting a test’s scripts, that test should no longer fail with “no viable alternative”; it may then fail on semantics (e.g. “cannot resolve type [let]”) or on assertions (e.g. type or value).

---

## 3. Type expectations (Long, Integer, Byte, etc.)

**Symptom:** Test expects a boxed type (e.g. `Long`, `Integer`, `Byte`) but gets another (e.g. `Integer` where `Long` was expected, or a double/number).

**Cause:** JavaScript has a single numeric type (Number) or BigInt; the implementation may return boxed Java types that don’t match the test’s expectations (e.g. `5 & 12` returning `Integer` instead of `Long`).

**Suggestion:** Apply **FIX3** (test expectations, not the language):

- Where the test only cares about numeric value, assert on the value (e.g. `assertEquals(4, result)` or allow both `Integer` and `Long` for the same value).
- Where the test explicitly checks Painless’s int/long/byte semantics, either relax the assertion to “value and optionally acceptable types” or mark the test `@Ignore("Painless-only: exact boxed type")`.

**Verification:** Tests like AndTests.testLongConst (expected Long, got Integer) should pass once assertions accept the actual JS semantics (e.g. value 4 or both Integer/Long).

---

## 4. Optional chaining / null-safe and object literals (testNullSafeDeref, etc.)

**Symptom:** Tests that use optional chaining `?.` or object literals (e.g. `let a = {}`, `a?.size()`) may fail in compile or runtime.

**Cause:** Possible mismatches: (1) Painless map literal `[:]` / `['k':v]` was converted to JS `{}` / `{'k':v}` — ensure the parser and runtime accept these and that `{}` is an empty object, not a block. (2) Painless’s “result of null-safe operator must be nullable” rule may not apply in JS; `assertMustBeNullable` might fire where JS would allow the expression. (3) Method names (e.g. `size()` vs `length`) or whitelist differences.

**Suggestion:**

- Confirm that object literals `{}` and `{ key: value }` are parsed and executed as objects (not blocks) in script context.
- Align optional-chaining behavior with **FIX4a** (follow JavaScript semantics; do not enforce Painless’s “must be nullable” rule). If a test exists solely to assert that rule, `@Ignore("Painless-only: null-safe nullable rule")`.
- Ensure whitelist and runtime expose the same methods (e.g. `size()` for maps/lists) that the scripts call.

**Verification:** testNullSafeDeref (and similar) pass when scripts use `let`, JS object literals, and when assertions match JS optional-chaining semantics.

---

## 5. AliasTests.testInnerNoAlias

**Symptom:** After FIX1 the test was simplified to a single successful exec (`let a = AliasTestClass.getInnerUnaliased(); a.minus(2, 3)` → -1). The second assertion (expecting “cannot resolve type [UnaliasedTestInnerClass]”) was removed because there is no JS equivalent for “typed declaration with unresolved type.”

**Cause:** The test now only checks that the call works with `let`. Any remaining failure could be due to semantics (“cannot resolve type [let]”) or to a different expectation (e.g. that the first call throws in some scenario).

**Suggestion:** If the test still fails, ensure (1) semantic phase accepts `let` (see §1). (2) If the intent is only “call with unaliased inner class works,” keep the single assertion; otherwise document that the “unresolved type” check is Painless-only and not covered in JS.

---

## 6. testStringEscapes

**Symptom:** testStringEscapes may fail (parse, compile, or assertion).

**Cause:** Could be lexer/parser handling of escape sequences in strings, or a difference in which escapes are allowed (e.g. `'\"'` in single-quoted strings). The test may also expect a specific error message.

**Suggestion:** Check string literal handling in the lexer and parser (and any string-related semantic checks). Align allowed escapes and error messages with the test’s expectations, or adjust the test if the desired behavior is “JS-style” escapes and messages.

---

## 7. testCast and similar (Painless casts removed)

**Symptom:** testCast was converted to `return 1` and `let x = 100; return x` (no Painless casts). The test may still expect a boxed type (e.g. `(byte)100`) or a specific type.

**Cause:** JavaScript has no `(byte)` / `(int)` casts; we only assert numeric value.

**Suggestion:** If the test asserts on type (e.g. Byte), either (a) relax to value (e.g. `assertEquals(100, exec(...))`) per FIX3, or (b) `@Ignore("Painless-only: primitive cast")` for that assertion. Keep the script in valid JS.

---

## 8. Static interface methods / HashMap / Map (testStaticInterfaceMethod, testCast)

**Symptom:** Scripts use `new HashMap()`, `Comparator.comparing(...)`, `Map`, etc. Failures may be “no viable alternative,” “cannot resolve type [let],” or missing method/type on whitelist.

**Cause:** Grammar may not allow these in some contexts; semantic phase may not resolve `Map`/`HashMap`; or whitelist may not expose the required methods.

**Suggestion:** (1) Ensure scripts use valid JS (e.g. `let x = new HashMap();` not `Map x = ...` if the grammar doesn’t support that). (2) Ensure `HashMap`, `Map`, `Comparator`, and used methods are whitelisted and that the semantic phase resolves them. (3) If the test is about Painless-specific static interface method resolution, consider `@Ignore("Painless-only: ...")` once the script is valid JS.

---

## Suggested order of work

1. **Fix semantic “cannot resolve type [let]”** (§1) so all scripts that use `let`/`const` compile. This unblocks most of the failures introduced by FIX1.
2. **Continue FIX1** (§2) for remaining “no viable alternative” failures in other test classes.
3. **Apply FIX3** (§3) for type expectations (Long/Integer/Byte) and cast-related assertions (§7).
4. **Adjust optional chaining / object literals** (§4) and **string escapes** (§6) as needed.
5. **Tidy edge cases** (§5, §8) once the main categories are fixed.

**Verification command (unchanged):**

```bash
./gradlew :modules:lang-javascript:test --tests "org.elasticsearch.javascript.*"
```

Use failure messages to map each failing test to one of the sections above and apply the corresponding suggestion.
