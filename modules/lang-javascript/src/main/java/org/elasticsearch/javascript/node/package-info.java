/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

/**
 * A javascript tree is composed of the node classes found in this package.
 * <p>
 * The following are the types of nodes:
 * A* (abstract) - These are the abstract nodes that are the superclasses for the other types.
 * I* (interface) - These are marker interfaces to denote a property of the node.
 * S* (statement) - These are nodes that represent a statement in Javascript.
 * E* (expression) - These are nodes that represent an expression in Javascript.
 * P* (postfix) - These are nodes that represent a postfix of a variable chain.
 * E/P* (storeable) - These are nodes that are allowed to store a value to memory.
 * *Sub* (sub) - These are partial nodes with a parent (S/E/P)* node used to split up logic into smaller pieces.
 * <p>
 * The following is a brief description of each node:
 * {@link org.elasticsearch.javascript.node.AExpression} - The superclass for all E* (expression) and P* (postfix) nodes.
 * {@link org.elasticsearch.javascript.node.ANode} - The superclass for all nodes.
 * {@link org.elasticsearch.javascript.node.AStatement} - The superclass for all S* (statement) nodes.
 * {@link org.elasticsearch.javascript.node.EAssignment} - Represents an assignment with the lhs and rhs as child nodes.
 * {@link org.elasticsearch.javascript.node.EBinary} - Represents a binary math expression.
 * {@link org.elasticsearch.javascript.node.EBooleanComp} - Represents a boolean expression.
 * {@link org.elasticsearch.javascript.node.EBooleanConstant} - Represents a boolean constant.
 * {@link org.elasticsearch.javascript.node.ECallLocal} - Represents a user-defined call.
 * {@link org.elasticsearch.javascript.node.EComp} - Represents a comparison expression.
 * {@link org.elasticsearch.javascript.node.EConditional} - Represents a conditional expression.
 * {@link org.elasticsearch.javascript.node.EDecimal} - Represents a decimal constant.
 * {@link org.elasticsearch.javascript.node.EExplicit} - Represents an explicit cast.
 * {@link org.elasticsearch.javascript.node.EFunctionRef} - Represents a function reference (non-capturing).
 * {@link org.elasticsearch.javascript.node.EInstanceof} - Represents an instanceof check.
 * {@link org.elasticsearch.javascript.node.ELambda} - Represents a lambda function.
 * {@link org.elasticsearch.javascript.node.EListInit} - Represents a list initialization shortcut.
 * {@link org.elasticsearch.javascript.node.EMapInit} - Represents a map initialization shortcut.
 * {@link org.elasticsearch.javascript.node.ENewArray} - Represents an array instantiation.
 * {@link org.elasticsearch.javascript.node.ENewObj} - Represents and object instantiation.
 * {@link org.elasticsearch.javascript.node.ENull} - Represents a null constant.
 * {@link org.elasticsearch.javascript.node.ENumeric} - Represents a non-decimal numeric constant.
 * {@link org.elasticsearch.javascript.node.ERegex} - Represents a regular expression constant.
 * {@link org.elasticsearch.javascript.node.EString} - Represents a string constant.
 * {@link org.elasticsearch.javascript.node.EUnary} - Represents a unary math expression.
 * {@link org.elasticsearch.javascript.node.ESymbol} - Represents a variable load/store.
 * {@link org.elasticsearch.javascript.node.EBrace} - Represents an array load/store and defers to a child subnode.
 * {@link org.elasticsearch.javascript.node.ECall} - Represents a method call and defers to a child subnode.
 * {@link org.elasticsearch.javascript.node.EDot} - Represents a field load/store and defers to a child subnode.
 * {@link org.elasticsearch.javascript.node.SBlock} - Represents a set of statements as a branch of control-flow.
 * {@link org.elasticsearch.javascript.node.SBreak} - Represents a break statement.
 * {@link org.elasticsearch.javascript.node.SCatch} - Represents a catch block as part of a try-catch block.
 * {@link org.elasticsearch.javascript.node.SContinue} - Represents a continue statement.
 * {@link org.elasticsearch.javascript.node.SDeclaration} - Represents a single variable declaration.
 * {@link org.elasticsearch.javascript.node.SDeclBlock} - Represents a series of declarations.
 * {@link org.elasticsearch.javascript.node.SDo} - Represents a do-while loop.
 * {@link org.elasticsearch.javascript.node.SEach} - Represents a for-each loop and defers to subnodes depending on type.
 * {@link org.elasticsearch.javascript.node.SExpression} - Represents the top-level node for an expression as a statement.
 * {@link org.elasticsearch.javascript.node.SFor} - Represents a for loop.
 * {@link org.elasticsearch.javascript.node.SFunction} - Represents a user-defined function.
 * {@link org.elasticsearch.javascript.node.SIf} - Represents an if block.
 * {@link org.elasticsearch.javascript.node.SIfElse} - Represents an if/else block.
 * {@link org.elasticsearch.javascript.node.SReturn} - Represents a return statement.
 * {@link org.elasticsearch.javascript.node.SClass} - The root of all Javascript trees.  Contains a series of statements.
 * {@link org.elasticsearch.javascript.node.SThrow} - Represents a throw statement.
 * {@link org.elasticsearch.javascript.node.STry} - Represents the try block as part of a try-catch block.
 * {@link org.elasticsearch.javascript.node.SWhile} - Represents a while loop.
 * <p>
 * Note that internal nodes are generated during the analysis phase by modifying the tree on-the-fly
 * for clarity of development and convenience during the writing phase.
 * <p>
 * All Javascript trees must start with an SClass node at the root.  Each node has a constructor that requires
 * all of its values and children be passed in at the time of instantiation.  This means that Javascript trees
 * are build bottom-up; however, this helps enforce tree structure correctness and fits naturally with a
 * standard recursive-descent parser.
 * <p>
 * Generally, statement nodes have member data that evaluate legal control-flow during the analysis phase.
 * The typical order for statement nodes is for each node to call analyze on it's children during the analysis phase
 * and write on it's children during the writing phase.
 * <p>
 * Generally, expression nodes have member data that evaluate static and def types.  The typical order for an expression node
 * during the analysis phase looks like the following:
 * <pre>{@code
 * For known expected types:
 *
 * expression.child.expected = expectedType      // set the known expected type
 *
 * expression.child.analyze(...)                 // analyze the child node to set the child's actual type
 *
 * expression.child = expression.child.cast(...) // add an implicit cast node if the child node's
 *                                               // actual type is not the expected type and set the
 *                                               // expression's child to the implicit cast node
 *
 * For unknown expected types that need promotion:
 *
 * expression.child.analyze(...)                 // analyze the child node to set the child's actual type
 *
 * Type promote = Caster.promote(...)            // get the promotion type for the child based on
 *                                               // the current operation and child's actual type
 *
 * expression.child.expected = promote           // set the expected type to the promotion type
 *
 * expression.child = expression.child.cast(...) // add an implicit cast node if the child node's
 *                                               // actual type is not the expected type and set the
 *                                               // expression's child to the implicit cast node
 * }</pre>
 * Expression nodes just call each child during the writing phase.
 * <p>
 * Postfix nodes represent postfixes in a variable/method chain including braces, calls, or fields.
 * Postfix nodes will always have a prefix node that is the prior piece of the variable/method chain.
 * Analysis of a postfix node will cause a chain of analysis calls to happen where the prefix will
 * be analyzed first and continue until the prefix is not a postfix node.  Writing out a series of
 * loads from a postfix node works in the same fashion.  Stores work somewhat differently as
 * described by later documentation.
 * <p>
 * Storebable nodes have three methods for writing -- setup, load, and store.  These methods
 * are used in conjunction with a parent node aware of the storeable node (lhs) that has a node
 * representing a value to store (rhs). The setup method is always once called before a store
 * to give storeable nodes a chance to write any prefixes they may have and any values such as
 * array indices before the store happens.  Load is called on a storeable node that must also
 * be read from, and store is called to write a value to memory.
 * <p>
 * Sub nodes are partial nodes that require a parent to work correctly.  These nodes can really
 * represent anything the parent node would like to split up into logical pieces and don't really
 * have any distinct set of rules.  The currently existing subnodes all have ANode as a super class
 * somewhere in their class hierarchy so the parent node can defer some analysis and writing to
 * the sub node.
 */
package org.elasticsearch.javascript.node;
