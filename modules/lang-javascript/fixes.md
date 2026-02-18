# JavaScript test fixes and speculations

This file documents the approach taken for lang-javascript tests when migrating from Painless to JavaScript, and speculations for tests that still fail after substituting Painless syntax with valid JavaScript.

## Approach

- **Painless-only tests** (no JavaScript equivalent): Skipped with JUnit 4 `@Ignore("Painless-only: <reason>")`. Examples: primitive types (byte, short, char, int, long, float, double), `def`, Painless casts `(byte)`, `(int)`, etc., primitive arrays (`int[]`, `byte[]`, `boolean[]`), ClassCastException semantics for bitwise/compound assignment, Painless-specific APIs.
- **Tests with a JavaScript equivalent**: Script literals were converted (e.g. `int x = 5` → `let x = 5`, `5L` → `5` where acceptable, `def` → `let`, remove type casts). If such a test still fails, possible causes and fix ideas are noted below.

## Speculations (tests that fail after Painless → JS substitution)

### Parser / grammar

- **`no viable alternative at input 'x'` (or similar)**  
  The JavaScript grammar may not allow certain statement/expression forms that Painless allows (e.g. expression used as statement in a specific context). Fix: extend `JavascriptParser.g4` (and possibly lexer) to accept the same surface syntax, or normalize the script in tests to a form the grammar already accepts.

- **`reportAttemptingFullContext` / ambiguity**  
  The grammar can be ambiguous (e.g. `&` vs other operators, `.` in member vs numeric). Fix: disambiguate in the grammar (e.g. separate rules or predicates) or ensure the lexer produces distinct token types so the parser can choose the right alternative.

- **`let` / variable statement**  
  Scripts like `let x = 5; let y = 3; return x & y;` trigger "reportAttemptingFullContext d=2 (statement), input='let x = 5; let'". The grammar’s `statement` rule may not accept variable statement in the way the Walker expects. Fix: add or adjust variableStatement in JavascriptParser.g4 and ensure the Walker visits it.

- **Bitwise `&` in expression**  
  Scripts like `return 5 & 12;` can trigger "reportAttemptingFullContext d=99 (singleExpression), input='&'". Fix: disambiguate bitwise AND from logical AND in the grammar.

### Runtime / execution

- **Expected type (e.g. Long, Integer) but got Double**  
  JavaScript has only Number (double) and BigInt. Tests that assert exact boxed types (Long, Integer) may need to relax expectations (e.g. assert numeric value and optionally type) or document that JS does not preserve Painless’s int/long distinction.

- **Regex / augmentation / whitelist**  
  Tests that rely on Painless regex augmentations, custom whitelists, or context-specific APIs may fail if the JavaScript runtime does not expose the same augmentations or if method resolution differs. Fix: implement equivalent augmentations or whitelist entries for the JS engine, or skip as Painless-only.

- **Null safety / optional chaining**  
  Painless’s `?.` and “question space dot” behavior may differ from JavaScript’s optional chaining. Fix: align Walker/codegen to emit optional chaining where intended and match test expectations, or document behavioral differences and skip.

### Error handling

- **Expected ScriptException but got parse/AssertionError**  
  Tests that expect a boxed ScriptException (e.g. from `expectScriptThrows`) may get a parse-time AssertionError from the Walker’s error listener instead. Fix: either have the Walker collect parse errors and throw a ScriptException with a similar message, or adjust tests to accept parse-time failures when the script is invalid.

- **Stack trace / column numbers**  
  Tests that assert exact script stack or column (e.g. `assertScriptElementColumn`) may fail if the JS compiler produces different line/column metadata. Fix: ensure Writer/compiler record source locations consistently and that ScriptException reports them in the same way as Painless.

### General

- **Array/length and “array-like” behavior**  
  Painless’s `int[]`, `def[]`, and `length` field differ from JavaScript’s Array and `.length`. Tests that depend on primitive arrays or Painless’s Def bootstrap should be skipped; tests that only need “array of numbers” can use JS arrays and adjust expectations (e.g. return type, overflow behavior).

- **Loop limits / infinite loop detection**  
  If the JavaScript runtime does not enforce the same loop limits or timeouts as Painless, tests for “infinite loop” or “loop limit” may need to be skipped or adapted to whatever safety mechanism the JS engine uses.
