/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.rule.builder.dialect.java.parser;

import java.util.List;
import java.util.Map;

public abstract class AbstractJavaBlockDescr implements JavaBlockDescr {
    private Map<String, Class< ? >> inputs;
    private List<JavaLocalDeclarationDescr> inScopeLocalVars;

    public Map<String, Class< ? >> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Class< ? >> variables) {
        this.inputs = variables;
    }

    /**
     * Returns the list of in-code, declared variables that are available
     * in the scope of this block
     * @return
     */
    public List<JavaLocalDeclarationDescr> getInScopeLocalVars() {
        return inScopeLocalVars;
    }

    /**
     * Sets the list of in-code, declared variables that are available
     * in the scope of this block
     */
    public void setInScopeLocalVars( List<JavaLocalDeclarationDescr> inScopeLocalVars ) {
        this.inScopeLocalVars = inScopeLocalVars;
    }    
}
