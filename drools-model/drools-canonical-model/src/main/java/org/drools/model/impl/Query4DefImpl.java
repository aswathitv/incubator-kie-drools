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
package org.drools.model.impl;

import org.drools.model.Argument;
import org.drools.model.Query4Def;
import org.drools.model.Variable;
import org.drools.model.view.QueryCallViewItem;
import org.drools.model.view.QueryCallViewItemImpl;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.impl.RuleBuilder.DEFAULT_PACKAGE;

public class Query4DefImpl<T1, T2, T3, T4> extends QueryDefImpl implements ModelComponent, Query4Def<T1, T2, T3, T4> {

    private final Variable<T1> arg1;

    private final Variable<T2> arg2;

    private final Variable<T3> arg3;

    private final Variable<T4> arg4;

    public Query4DefImpl(ViewBuilder viewBuilder, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4) {
        this(viewBuilder, DEFAULT_PACKAGE, name, type1, type2, type3, type4);
    }

    public Query4DefImpl(ViewBuilder viewBuilder, String pkg, String name, Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4) {
        super(viewBuilder, pkg, name);
        this.arg1 = declarationOf(type1);
        this.arg2 = declarationOf(type2);
        this.arg3 = declarationOf(type3);
        this.arg4 = declarationOf(type4);
    }

    public Query4DefImpl(ViewBuilder viewBuilder, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name) {
        this(viewBuilder, DEFAULT_PACKAGE, name, type1, arg1name, type2, arg2name, type3, arg3name, type4, arg4name);
    }

    public Query4DefImpl(ViewBuilder viewBuilder, String pkg, String name, Class<T1> type1, String arg1name, Class<T2> type2, String arg2name, Class<T3> type3, String arg3name, Class<T4> type4, String arg4name) {
        super(viewBuilder, pkg, name);
        this.arg1 = declarationOf(type1, arg1name);
        this.arg2 = declarationOf(type2, arg2name);
        this.arg3 = declarationOf(type3, arg3name);
        this.arg4 = declarationOf(type4, arg4name);
    }

    @Override()
    public QueryCallViewItem call(boolean open, Argument<T1> var1, Argument<T2> var2, Argument<T3> var3, Argument<T4> var4) {
        return new QueryCallViewItemImpl(this, open, var1, var2, var3, var4);
    }

    @Override()
    public Variable<?>[] getArguments() {
        return new Variable<?>[] { arg1, arg2, arg3, arg4 };
    }

    @Override()
    public Variable<T1> getArg1() {
        return arg1;
    }

    @Override()
    public Variable<T2> getArg2() {
        return arg2;
    }

    @Override()
    public Variable<T3> getArg3() {
        return arg3;
    }

    @Override()
    public Variable<T4> getArg4() {
        return arg4;
    }

    @Override
    public boolean isEqualTo(ModelComponent other) {
        if (this == other)
            return true;
        if (!(other instanceof Query4DefImpl))
            return false;
        Query4DefImpl that = (Query4DefImpl) other;
        return true && ModelComponent.areEqualInModel(arg1, that.arg1) && ModelComponent.areEqualInModel(arg2, that.arg2) && ModelComponent.areEqualInModel(arg3, that.arg3) && ModelComponent.areEqualInModel(arg4, that.arg4);
    }
}
