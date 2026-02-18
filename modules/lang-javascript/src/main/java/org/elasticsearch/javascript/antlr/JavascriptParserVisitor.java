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
   * Visit a parse tree produced by {@link JavascriptParser#source}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSource(JavascriptParser.SourceContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#function}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunction(JavascriptParser.FunctionContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#parameters}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitParameters(JavascriptParser.ParametersContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#statement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitStatement(JavascriptParser.StatementContext ctx);
  /**
   * Visit a parse tree produced by the {@code if}
   * labeled alternative in {@link JavascriptParser#rstatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitIf(JavascriptParser.IfContext ctx);
  /**
   * Visit a parse tree produced by the {@code while}
   * labeled alternative in {@link JavascriptParser#rstatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitWhile(JavascriptParser.WhileContext ctx);
  /**
   * Visit a parse tree produced by the {@code for}
   * labeled alternative in {@link JavascriptParser#rstatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFor(JavascriptParser.ForContext ctx);
  /**
   * Visit a parse tree produced by the {@code each}
   * labeled alternative in {@link JavascriptParser#rstatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitEach(JavascriptParser.EachContext ctx);
  /**
   * Visit a parse tree produced by the {@code ineach}
   * labeled alternative in {@link JavascriptParser#rstatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitIneach(JavascriptParser.IneachContext ctx);
  /**
   * Visit a parse tree produced by the {@code try}
   * labeled alternative in {@link JavascriptParser#rstatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitTry(JavascriptParser.TryContext ctx);
  /**
   * Visit a parse tree produced by the {@code do}
   * labeled alternative in {@link JavascriptParser#dstatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitDo(JavascriptParser.DoContext ctx);
  /**
   * Visit a parse tree produced by the {@code decl}
   * labeled alternative in {@link JavascriptParser#dstatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitDecl(JavascriptParser.DeclContext ctx);
  /**
   * Visit a parse tree produced by the {@code continue}
   * labeled alternative in {@link JavascriptParser#dstatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitContinue(JavascriptParser.ContinueContext ctx);
  /**
   * Visit a parse tree produced by the {@code break}
   * labeled alternative in {@link JavascriptParser#dstatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBreak(JavascriptParser.BreakContext ctx);
  /**
   * Visit a parse tree produced by the {@code return}
   * labeled alternative in {@link JavascriptParser#dstatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitReturn(JavascriptParser.ReturnContext ctx);
  /**
   * Visit a parse tree produced by the {@code throw}
   * labeled alternative in {@link JavascriptParser#dstatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitThrow(JavascriptParser.ThrowContext ctx);
  /**
   * Visit a parse tree produced by the {@code expr}
   * labeled alternative in {@link JavascriptParser#dstatement}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitExpr(JavascriptParser.ExprContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#trailer}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitTrailer(JavascriptParser.TrailerContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#block}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBlock(JavascriptParser.BlockContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#empty}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitEmpty(JavascriptParser.EmptyContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#initializer}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitInitializer(JavascriptParser.InitializerContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#afterthought}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitAfterthought(JavascriptParser.AfterthoughtContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#declaration}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitDeclaration(JavascriptParser.DeclarationContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#decltype}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitDecltype(JavascriptParser.DecltypeContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#type}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitType(JavascriptParser.TypeContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#declvar}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitDeclvar(JavascriptParser.DeclvarContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#trap}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitTrap(JavascriptParser.TrapContext ctx);
  /**
   * Visit a parse tree produced by the {@code single}
   * labeled alternative in {@link JavascriptParser#noncondexpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSingle(JavascriptParser.SingleContext ctx);
  /**
   * Visit a parse tree produced by the {@code comp}
   * labeled alternative in {@link JavascriptParser#noncondexpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitComp(JavascriptParser.CompContext ctx);
  /**
   * Visit a parse tree produced by the {@code bool}
   * labeled alternative in {@link JavascriptParser#noncondexpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBool(JavascriptParser.BoolContext ctx);
  /**
   * Visit a parse tree produced by the {@code binary}
   * labeled alternative in {@link JavascriptParser#noncondexpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBinary(JavascriptParser.BinaryContext ctx);
  /**
   * Visit a parse tree produced by the {@code elvis}
   * labeled alternative in {@link JavascriptParser#noncondexpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitElvis(JavascriptParser.ElvisContext ctx);
  /**
   * Visit a parse tree produced by the {@code instanceof}
   * labeled alternative in {@link JavascriptParser#noncondexpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitInstanceof(JavascriptParser.InstanceofContext ctx);
  /**
   * Visit a parse tree produced by the {@code lambdaexpression}
   * labeled alternative in {@link JavascriptParser#expression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLambdaexpression(JavascriptParser.LambdaexpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code funcrefexpression}
   * labeled alternative in {@link JavascriptParser#expression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFuncrefexpression(JavascriptParser.FuncrefexpressionContext ctx);
  /**
   * Visit a parse tree produced by the {@code nonconditional}
   * labeled alternative in {@link JavascriptParser#expression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNonconditional(JavascriptParser.NonconditionalContext ctx);
  /**
   * Visit a parse tree produced by the {@code conditional}
   * labeled alternative in {@link JavascriptParser#expression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitConditional(JavascriptParser.ConditionalContext ctx);
  /**
   * Visit a parse tree produced by the {@code assignment}
   * labeled alternative in {@link JavascriptParser#expression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitAssignment(JavascriptParser.AssignmentContext ctx);
  /**
   * Visit a parse tree produced by the {@code pre}
   * labeled alternative in {@link JavascriptParser#unary}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPre(JavascriptParser.PreContext ctx);
  /**
   * Visit a parse tree produced by the {@code addsub}
   * labeled alternative in {@link JavascriptParser#unary}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitAddsub(JavascriptParser.AddsubContext ctx);
  /**
   * Visit a parse tree produced by the {@code notaddsub}
   * labeled alternative in {@link JavascriptParser#unary}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNotaddsub(JavascriptParser.NotaddsubContext ctx);
  /**
   * Visit a parse tree produced by the {@code read}
   * labeled alternative in {@link JavascriptParser#unarynotaddsub}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitRead(JavascriptParser.ReadContext ctx);
  /**
   * Visit a parse tree produced by the {@code post}
   * labeled alternative in {@link JavascriptParser#unarynotaddsub}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPost(JavascriptParser.PostContext ctx);
  /**
   * Visit a parse tree produced by the {@code not}
   * labeled alternative in {@link JavascriptParser#unarynotaddsub}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNot(JavascriptParser.NotContext ctx);
  /**
   * Visit a parse tree produced by the {@code cast}
   * labeled alternative in {@link JavascriptParser#unarynotaddsub}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitCast(JavascriptParser.CastContext ctx);
  /**
   * Visit a parse tree produced by the {@code primordefcast}
   * labeled alternative in {@link JavascriptParser#castexpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPrimordefcast(JavascriptParser.PrimordefcastContext ctx);
  /**
   * Visit a parse tree produced by the {@code refcast}
   * labeled alternative in {@link JavascriptParser#castexpression}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitRefcast(JavascriptParser.RefcastContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#primordefcasttype}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPrimordefcasttype(JavascriptParser.PrimordefcasttypeContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#refcasttype}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitRefcasttype(JavascriptParser.RefcasttypeContext ctx);
  /**
   * Visit a parse tree produced by the {@code dynamic}
   * labeled alternative in {@link JavascriptParser#chain}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitDynamic(JavascriptParser.DynamicContext ctx);
  /**
   * Visit a parse tree produced by the {@code newarray}
   * labeled alternative in {@link JavascriptParser#chain}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNewarray(JavascriptParser.NewarrayContext ctx);
  /**
   * Visit a parse tree produced by the {@code precedence}
   * labeled alternative in {@link JavascriptParser#primary}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPrecedence(JavascriptParser.PrecedenceContext ctx);
  /**
   * Visit a parse tree produced by the {@code numeric}
   * labeled alternative in {@link JavascriptParser#primary}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNumeric(JavascriptParser.NumericContext ctx);
  /**
   * Visit a parse tree produced by the {@code true}
   * labeled alternative in {@link JavascriptParser#primary}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitTrue(JavascriptParser.TrueContext ctx);
  /**
   * Visit a parse tree produced by the {@code false}
   * labeled alternative in {@link JavascriptParser#primary}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFalse(JavascriptParser.FalseContext ctx);
  /**
   * Visit a parse tree produced by the {@code null}
   * labeled alternative in {@link JavascriptParser#primary}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNull(JavascriptParser.NullContext ctx);
  /**
   * Visit a parse tree produced by the {@code string}
   * labeled alternative in {@link JavascriptParser#primary}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitString(JavascriptParser.StringContext ctx);
  /**
   * Visit a parse tree produced by the {@code regex}
   * labeled alternative in {@link JavascriptParser#primary}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitRegex(JavascriptParser.RegexContext ctx);
  /**
   * Visit a parse tree produced by the {@code listinit}
   * labeled alternative in {@link JavascriptParser#primary}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitListinit(JavascriptParser.ListinitContext ctx);
  /**
   * Visit a parse tree produced by the {@code mapinit}
   * labeled alternative in {@link JavascriptParser#primary}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitMapinit(JavascriptParser.MapinitContext ctx);
  /**
   * Visit a parse tree produced by the {@code variable}
   * labeled alternative in {@link JavascriptParser#primary}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitVariable(JavascriptParser.VariableContext ctx);
  /**
   * Visit a parse tree produced by the {@code calllocal}
   * labeled alternative in {@link JavascriptParser#primary}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitCalllocal(JavascriptParser.CalllocalContext ctx);
  /**
   * Visit a parse tree produced by the {@code newobject}
   * labeled alternative in {@link JavascriptParser#primary}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNewobject(JavascriptParser.NewobjectContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#postfix}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPostfix(JavascriptParser.PostfixContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#postdot}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPostdot(JavascriptParser.PostdotContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#callinvoke}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitCallinvoke(JavascriptParser.CallinvokeContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#fieldaccess}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFieldaccess(JavascriptParser.FieldaccessContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#braceaccess}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBraceaccess(JavascriptParser.BraceaccessContext ctx);
  /**
   * Visit a parse tree produced by the {@code newstandardarray}
   * labeled alternative in {@link JavascriptParser#arrayinitializer}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNewstandardarray(JavascriptParser.NewstandardarrayContext ctx);
  /**
   * Visit a parse tree produced by the {@code newinitializedarray}
   * labeled alternative in {@link JavascriptParser#arrayinitializer}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNewinitializedarray(JavascriptParser.NewinitializedarrayContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#listinitializer}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitListinitializer(JavascriptParser.ListinitializerContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#mapinitializer}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitMapinitializer(JavascriptParser.MapinitializerContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#maptoken}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitMaptoken(JavascriptParser.MaptokenContext ctx);
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
   * Visit a parse tree produced by {@link JavascriptParser#lambda}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLambda(JavascriptParser.LambdaContext ctx);
  /**
   * Visit a parse tree produced by {@link JavascriptParser#lamtype}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLamtype(JavascriptParser.LamtypeContext ctx);
  /**
   * Visit a parse tree produced by the {@code classfuncref}
   * labeled alternative in {@link JavascriptParser#funcref}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitClassfuncref(JavascriptParser.ClassfuncrefContext ctx);
  /**
   * Visit a parse tree produced by the {@code constructorfuncref}
   * labeled alternative in {@link JavascriptParser#funcref}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitConstructorfuncref(JavascriptParser.ConstructorfuncrefContext ctx);
  /**
   * Visit a parse tree produced by the {@code localfuncref}
   * labeled alternative in {@link JavascriptParser#funcref}.
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitLocalfuncref(JavascriptParser.LocalfuncrefContext ctx);
}
