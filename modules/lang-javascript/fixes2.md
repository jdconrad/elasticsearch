# Current plan: lang-javascript test fixes

Concise restatement of the plan from `fixes.md`, including developer decisions (DEV).

## Principles

- **Painless-only:** Use `@Ignore("Painless-only: <reason>")` for tests that have no JavaScript equivalent (primitive types, `def`, Painless casts, primitive arrays, ClassCastException semantics, etc.).
- **JS-equivalent tests:** Convert script literals to valid JS where possible; let tests run and fail so failures guide fixes. Do not extend the JavaScript language for Painless-only behavior.
- **Prefer code over tests:** Prefer changing Walker/grammar/whitelist over changing test expectations, except where the difference is intentional (e.g. JS number types, null vs undefined).

---

## Fix order and decisions

| # | Fix | Scope | Decision / action |
|---|-----|--------|-------------------|
| 1 | **FIX2** | Grammar | Eliminate parser ambiguity. **let:** Ensure lexer emits distinct token for `let` (or reorder `statement` alternatives) so `let x = 5; let y = 3;` parses without `reportAttemptingFullContext`. **&:** Ensure lexer gives distinct tokens for `&` vs `&&` (and `&&` before `&`) so `return 5 & 12;` parses. Use ANTLR features where possible. Re-run tests to confirm no ambiguity. |
| 2 | **FIX1** | Test scripts | For "no viable alternative" errors, fix the **script** to valid JS the grammar already accepts; do not change the JavaScript language. |
| 3 | **FIX5** | Walker | On parse error, throw (or wrap in) `ScriptException` instead of `AssertionError` so tests that expect `ScriptException` get it. Change Walker only. |
| 4 | **FIX3** | Tests | Alter tests to expect JS number semantics (e.g. numeric value, Double, BigInt). No Long/Integer where JS doesn’t provide them. |
| 5 | **FIX6** | Writer | Record source locations (script name, line, column) and report them in `ScriptException` the same way as Painless so stack/column tests can pass. |
| 6 | **FIX4** | Regex / null safety / arrays | See below. |
| 7 | **FIX7** | Loop limits | No action. Same runtime as Painless; assume limits apply. |

---

## FIX4 – Regex, null safety, arrays (decisions)

- **Regex:** Full JS equivalent (Pattern/Matcher, limiting, augmentations) is pending. **DEV:** Will check with the Painless architect. No concrete implementation decision yet.
- **Null safety / optional chaining:** **DEV (FIX4a):** Retain the distinction between `null` and `undefined` and follow JavaScript syntax. So: emit optional chaining `?.` per JS semantics (result `undefined` when short-circuiting), do not force Painless’s “must be nullable” rule, and for “question space dot” either follow JS (allow/reject per grammar) or document and skip the Painless-only test.
- **Arrays / length:** **DEV (FIX4b):** Represent JavaScript arrays as Java `ArrayList`. So: in the JS implementation, script arrays (e.g. `[1, 2, 3]`) become `ArrayList`; `.length` can map to `size()` or an exposed `.length` getter. Tests that only need “array of numbers” use JS array literals and expect ArrayList/size or equivalent; primitive arrays (`int[]`, `def[]`) remain Painless-only.

---

## Verification

After each step, run:

```bash
./gradlew :modules:lang-javascript:test --tests "org.elasticsearch.javascript.*" --configuration-cache
```

Use failures to guide the next fix; keep `fixes.md` (and this file) updated when decisions or priorities change.
