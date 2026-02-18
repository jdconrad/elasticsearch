// ANTLR GENERATED CODE: DO NOT EDIT
package org.elasticsearch.javascript.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link JavascriptParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
interface JavascriptParserVisitor<T> extends ParseTreeVisitor<T> {
  /**
   * Visit a parse tree produced by {@link JavascriptParser#program}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitProgram(JavascriptParser.ProgramContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#sourceElement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSourceElement(JavascriptParser.SourceElementContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#statement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitStatement(JavascriptParser.StatementContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#block}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBlock(JavascriptParser.BlockContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#statementList}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitStatementList(JavascriptParser.StatementListContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#importStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitImportStatement(JavascriptParser.ImportStatementContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#importFromBlock}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitImportFromBlock(JavascriptParser.ImportFromBlockContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#importModuleItems}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitImportModuleItems(JavascriptParser.ImportModuleItemsContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#importAliasName}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitImportAliasName(JavascriptParser.ImportAliasNameContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#moduleExportName}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitModuleExportName(JavascriptParser.ModuleExportNameContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#importedBinding}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitImportedBinding(JavascriptParser.ImportedBindingContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#importDefault}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitImportDefault(JavascriptParser.ImportDefaultContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#importNamespace}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitImportNamespace(JavascriptParser.ImportNamespaceContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#importFrom}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitImportFrom(JavascriptParser.ImportFromContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#aliasName}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitAliasName(JavascriptParser.AliasNameContext ctx);
  /**
   * Visit a parse tree produced by the {@code ExportDeclaration}
   * labeled alternative in {@link JavascriptParser#exportStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitExportDeclaration(JavascriptParser.ExportDeclarationContext ctx);
  /**
   * Visit a parse tree produced by the {@code ExportDefaultDeclaration}
   * labeled alternative in {@link JavascriptParser#exportStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitExportDefaultDeclaration(JavascriptParser.ExportDefaultDeclarationContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#exportFromBlock}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitExportFromBlock(JavascriptParser.ExportFromBlockContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#exportModuleItems}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitExportModuleItems(JavascriptParser.ExportModuleItemsContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#exportAliasName}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitExportAliasName(JavascriptParser.ExportAliasNameContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#declaration}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitDeclaration(JavascriptParser.DeclarationContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#variableStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitVariableStatement(JavascriptParser.VariableStatementContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#variableDeclarationList}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitVariableDeclarationList(JavascriptParser.VariableDeclarationListContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#singleVariableDeclaration}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSingleVariableDeclaration(JavascriptParser.SingleVariableDeclarationContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#variableDeclaration}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitVariableDeclaration(JavascriptParser.VariableDeclarationContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#emptyStatement_}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitEmptyStatement_(JavascriptParser.EmptyStatement_Context ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#expressionStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitExpressionStatement(JavascriptParser.ExpressionStatementContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#ifStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitIfStatement(JavascriptParser.IfStatementContext ctx);
  /**
   * Visit a parse tree produced by the {@code DoStatement}
   * labeled alternative in {@link JavascriptParser#iterationStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitDoStatement(JavascriptParser.DoStatementContext ctx);
  /**
   * Visit a parse tree produced by the {@code WhileStatement}
   * labeled alternative in {@link JavascriptParser#iterationStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitWhileStatement(JavascriptParser.WhileStatementContext ctx);
  /**
   * Visit a parse tree produced by the {@code ForStatement}
   * labeled alternative in {@link JavascriptParser#iterationStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitForStatement(JavascriptParser.ForStatementContext ctx);
  /**
   * Visit a parse tree produced by the {@code ForInStatement}
   * labeled alternative in {@link JavascriptParser#iterationStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitForInStatement(JavascriptParser.ForInStatementContext ctx);
  /**
   * Visit a parse tree produced by the {@code ForOfStatement}
   * labeled alternative in {@link JavascriptParser#iterationStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitForOfStatement(JavascriptParser.ForOfStatementContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#varModifier}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitVarModifier(JavascriptParser.VarModifierContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#continueStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitContinueStatement(JavascriptParser.ContinueStatementContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#breakStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBreakStatement(JavascriptParser.BreakStatementContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#returnStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitReturnStatement(JavascriptParser.ReturnStatementContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#yieldStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitYieldStatement(JavascriptParser.YieldStatementContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#withStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitWithStatement(JavascriptParser.WithStatementContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#switchStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSwitchStatement(JavascriptParser.SwitchStatementContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#caseBlock}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitCaseBlock(JavascriptParser.CaseBlockContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#caseClauses}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitCaseClauses(JavascriptParser.CaseClausesContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#caseClause}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitCaseClause(JavascriptParser.CaseClauseContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#defaultClause}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitDefaultClause(JavascriptParser.DefaultClauseContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#labelledStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLabelledStatement(JavascriptParser.LabelledStatementContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#throwStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitThrowStatement(JavascriptParser.ThrowStatementContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#tryStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitTryStatement(JavascriptParser.TryStatementContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#catchProduction}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitCatchProduction(JavascriptParser.CatchProductionContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#finallyProduction}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFinallyProduction(JavascriptParser.FinallyProductionContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#debuggerStatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitDebuggerStatement(JavascriptParser.DebuggerStatementContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#functionDeclaration}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunctionDeclaration(JavascriptParser.FunctionDeclarationContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#classDeclaration}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitClassDeclaration(JavascriptParser.ClassDeclarationContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#classTail}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitClassTail(JavascriptParser.ClassTailContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#classElement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitClassElement(JavascriptParser.ClassElementContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#methodDefinition}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitMethodDefinition(JavascriptParser.MethodDefinitionContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#fieldDefinition}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFieldDefinition(JavascriptParser.FieldDefinitionContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#classElementName}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitClassElementName(JavascriptParser.ClassElementNameContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#privateIdentifier}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPrivateIdentifier(JavascriptParser.PrivateIdentifierContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#formalParameterList}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFormalParameterList(JavascriptParser.FormalParameterListContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#formalParameterArg}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFormalParameterArg(JavascriptParser.FormalParameterArgContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#lastFormalParameterArg}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLastFormalParameterArg(JavascriptParser.LastFormalParameterArgContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#functionBody}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunctionBody(JavascriptParser.FunctionBodyContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#sourceElements}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSourceElements(JavascriptParser.SourceElementsContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#arrayLiteral}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitArrayLiteral(JavascriptParser.ArrayLiteralContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#elementList}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitElementList(JavascriptParser.ElementListContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#arrayElement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitArrayElement(JavascriptParser.ArrayElementContext ctx);
  /**
   * Visit a parse tree produced by the {@code PropertyExpressionAssignment}
   * labeled alternative in {@link JavascriptParser#propertyAssignment}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPropertyExpressionAssignment(JavascriptParser.PropertyExpressionAssignmentContext ctx);
  /**
   * Visit a parse tree produced by the {@code ComputedPropertyExpressionAssignment}
   * labeled alternative in {@link JavascriptParser#propertyAssignment}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitComputedPropertyExpressionAssignment(JavascriptParser.ComputedPropertyExpressionAssignmentContext ctx);
  /**
   * Visit a parse tree produced by the {@code FunctionProperty}
   * labeled alternative in {@link JavascriptParser#propertyAssignment}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunctionProperty(JavascriptParser.FunctionPropertyContext ctx);
  /**
   * Visit a parse tree produced by the {@code PropertyGetter}
   * labeled alternative in {@link JavascriptParser#propertyAssignment}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPropertyGetter(JavascriptParser.PropertyGetterContext ctx);
  /**
   * Visit a parse tree produced by the {@code PropertySetter}
   * labeled alternative in {@link JavascriptParser#propertyAssignment}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPropertySetter(JavascriptParser.PropertySetterContext ctx);
  /**
   * Visit a parse tree produced by the {@code PropertyShorthand}
   * labeled alternative in {@link JavascriptParser#propertyAssignment}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPropertyShorthand(JavascriptParser.PropertyShorthandContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#propertyName}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPropertyName(JavascriptParser.PropertyNameContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#arguments}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitArguments(JavascriptParser.ArgumentsContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#argument}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitArgument(JavascriptParser.ArgumentContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#expressionSequence}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitExpressionSequence(JavascriptParser.ExpressionSequenceContext ctx);
  /**
   * Visit a parse tree produced by the {@code TemplateStringExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitTemplateStringExpression(JavascriptParser.TemplateStringExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code TernaryExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitTernaryExpression(JavascriptParser.TernaryExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code LogicalAndExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLogicalAndExpression(JavascriptParser.LogicalAndExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code PowerExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPowerExpression(JavascriptParser.PowerExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code PreIncrementExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPreIncrementExpression(JavascriptParser.PreIncrementExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code ObjectLiteralExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitObjectLiteralExpression(JavascriptParser.ObjectLiteralExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code MetaExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitMetaExpression(JavascriptParser.MetaExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code InExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitInExpression(JavascriptParser.InExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code LogicalOrExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLogicalOrExpression(JavascriptParser.LogicalOrExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code OptionalChainExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitOptionalChainExpression(JavascriptParser.OptionalChainExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code NotExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNotExpression(JavascriptParser.NotExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code PreDecreaseExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPreDecreaseExpression(JavascriptParser.PreDecreaseExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code ArgumentsExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitArgumentsExpression(JavascriptParser.ArgumentsExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code AwaitExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitAwaitExpression(JavascriptParser.AwaitExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code ThisExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitThisExpression(JavascriptParser.ThisExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code FunctionExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunctionExpression(JavascriptParser.FunctionExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code UnaryMinusExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitUnaryMinusExpression(JavascriptParser.UnaryMinusExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code AssignmentExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitAssignmentExpression(JavascriptParser.AssignmentExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code PostDecreaseExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPostDecreaseExpression(JavascriptParser.PostDecreaseExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code TypeofExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitTypeofExpression(JavascriptParser.TypeofExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code InstanceofExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitInstanceofExpression(JavascriptParser.InstanceofExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code UnaryPlusExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitUnaryPlusExpression(JavascriptParser.UnaryPlusExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code DeleteExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitDeleteExpression(JavascriptParser.DeleteExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code ImportExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitImportExpression(JavascriptParser.ImportExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code EqualityExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitEqualityExpression(JavascriptParser.EqualityExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code BitXOrExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBitXOrExpression(JavascriptParser.BitXOrExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code SuperExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSuperExpression(JavascriptParser.SuperExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code MultiplicativeExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitMultiplicativeExpression(JavascriptParser.MultiplicativeExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code BitShiftExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBitShiftExpression(JavascriptParser.BitShiftExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code ParenthesizedExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitParenthesizedExpression(JavascriptParser.ParenthesizedExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code AdditiveExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitAdditiveExpression(JavascriptParser.AdditiveExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code RelationalExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitRelationalExpression(JavascriptParser.RelationalExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code PostIncrementExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPostIncrementExpression(JavascriptParser.PostIncrementExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code YieldExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitYieldExpression(JavascriptParser.YieldExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code BitNotExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBitNotExpression(JavascriptParser.BitNotExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code NewExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNewExpression(JavascriptParser.NewExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code LiteralExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLiteralExpression(JavascriptParser.LiteralExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code ArrayLiteralExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitArrayLiteralExpression(JavascriptParser.ArrayLiteralExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code MemberDotExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitMemberDotExpression(JavascriptParser.MemberDotExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code ClassExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitClassExpression(JavascriptParser.ClassExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code MemberIndexExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitMemberIndexExpression(JavascriptParser.MemberIndexExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code IdentifierExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitIdentifierExpression(JavascriptParser.IdentifierExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code BitAndExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBitAndExpression(JavascriptParser.BitAndExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code BitOrExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBitOrExpression(JavascriptParser.BitOrExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code AssignmentOperatorExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitAssignmentOperatorExpression(JavascriptParser.AssignmentOperatorExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code VoidExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitVoidExpression(JavascriptParser.VoidExpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code CoalesceExpression}
   * labeled alternative in {@link JavascriptParser#singleExpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitCoalesceExpression(JavascriptParser.CoalesceExpressionContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#initializer}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitInitializer(JavascriptParser.InitializerContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#assignable}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitAssignable(JavascriptParser.AssignableContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#objectLiteral}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitObjectLiteral(JavascriptParser.ObjectLiteralContext ctx);
  /**
   * Visit a parse tree produced by the {@code NamedFunction}
   * labeled alternative in {@link JavascriptParser#anonymousFunction}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNamedFunction(JavascriptParser.NamedFunctionContext ctx);
  /**
   * Visit a parse tree produced by the {@code AnonymousFunctionDecl}
   * labeled alternative in {@link JavascriptParser#anonymousFunction}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitAnonymousFunctionDecl(JavascriptParser.AnonymousFunctionDeclContext ctx);
  /**
   * Visit a parse tree produced by the {@code ArrowFunction}
   * labeled alternative in {@link JavascriptParser#anonymousFunction}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitArrowFunction(JavascriptParser.ArrowFunctionContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#arrowFunctionParameters}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitArrowFunctionParameters(JavascriptParser.ArrowFunctionParametersContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#arrowFunctionBody}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitArrowFunctionBody(JavascriptParser.ArrowFunctionBodyContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#assignmentOperator}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitAssignmentOperator(JavascriptParser.AssignmentOperatorContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#literal}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLiteral(JavascriptParser.LiteralContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#templateStringLiteral}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitTemplateStringLiteral(JavascriptParser.TemplateStringLiteralContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#templateStringAtom}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitTemplateStringAtom(JavascriptParser.TemplateStringAtomContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#numericLiteral}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNumericLiteral(JavascriptParser.NumericLiteralContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#bigintLiteral}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBigintLiteral(JavascriptParser.BigintLiteralContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#getter}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitGetter(JavascriptParser.GetterContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#setter}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSetter(JavascriptParser.SetterContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#identifierName}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitIdentifierName(JavascriptParser.IdentifierNameContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#identifier}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitIdentifier(JavascriptParser.IdentifierContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#reservedWord}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitReservedWord(JavascriptParser.ReservedWordContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#keyword}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitKeyword(JavascriptParser.KeywordContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#let_}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLet_(JavascriptParser.Let_Context ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#eos}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitEos(JavascriptParser.EosContext ctx);
}
