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

package org.elasticsearch.painless.ir;

import org.elasticsearch.painless.ClassWriter;
import org.elasticsearch.painless.Globals;
import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.MethodWriter;
import org.elasticsearch.painless.symbol.ScopeTable;
import org.elasticsearch.painless.symbol.ScopeTable.Variable;
import org.objectweb.asm.Opcodes;

public class DeclarationNode extends StatementNode {

    /* ---- begin tree structure ---- */

    protected TypeNode declarationTypeNode;
    protected ExpressionNode expressionNode;

    public DeclarationNode setDeclarationTypeNode(TypeNode declarationTypeNode) {
        this.declarationTypeNode = declarationTypeNode;
        return this;
    }

    public TypeNode getDeclarationTypeNode() {
        return declarationTypeNode;
    }

    public Class<?> getDeclarationType() {
        return declarationTypeNode.getType();
    }

    public String getDeclarationCanonicalTypeName() {
        return declarationTypeNode.getCanonicalTypeName();
    }

    public DeclarationNode setExpressionNode(ExpressionNode childNode) {
        this.expressionNode = childNode;
        return this;
    }

    public ExpressionNode getExpressionNode() {
        return expressionNode;
    }

    /* ---- end tree structure, begin node data ---- */

    protected String name;
    protected boolean requiresDefault;

    public DeclarationNode setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public DeclarationNode setRequiresDefault(boolean requiresDefault) {
        this.requiresDefault = requiresDefault;
        return this;
    }

    public boolean requiresDefault() {
        return requiresDefault;
    }

    @Override
    public DeclarationNode setLocation(Location location) {
        super.setLocation(location);
        return this;
    }

    /* ---- end node data ---- */

    public DeclarationNode() {
        // do nothing
    }

    @Override
    protected void write(ClassWriter classWriter, MethodWriter methodWriter, Globals globals, ScopeTable scopeTable) {
        methodWriter.writeStatementOffset(location);

        Variable variable = scopeTable.defineVariable(getDeclarationType(), name);

        if (expressionNode == null) {
            if (requiresDefault) {
                Class<?> sort = variable.getType();

                if (sort == void.class || sort == boolean.class || sort == byte.class ||
                        sort == short.class || sort == char.class || sort == int.class) {
                    methodWriter.push(0);
                } else if (sort == long.class) {
                    methodWriter.push(0L);
                } else if (sort == float.class) {
                    methodWriter.push(0F);
                } else if (sort == double.class) {
                    methodWriter.push(0D);
                } else {
                    methodWriter.visitInsn(Opcodes.ACONST_NULL);
                }
            }
        } else {
            expressionNode.write(classWriter, methodWriter, globals, scopeTable);
        }

        methodWriter.visitVarInsn(variable.getAsmType().getOpcode(Opcodes.ISTORE), variable.getSlot());
    }
}
