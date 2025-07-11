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
package org.drools.verifier.builder;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageBuilderErrors;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilderFactory;

/**
 * Wraps the PackageBuilder for Verifier.
 * Used to build PackageDescrs.
 */
class VerifierPackageBuilder {

    private InnerBuilder innerBuilder = new InnerBuilder();

    private PackageDescr packageDescr;

    public void addKnowledgeResource(Resource resource,
                                     ResourceType type,
                                     ResourceConfiguration configuration) {
        innerBuilder.addKnowledgeResource( resource,
                                           type,
                                           configuration );
    }

    public PackageDescr getPackageDescr() {
        return packageDescr;
    }

    public boolean hasErrors() {
        return innerBuilder.hasErrors();
    }

    public PackageBuilderErrors getErrors() {
        return innerBuilder.getErrors();
    }

    class InnerBuilder extends KnowledgeBuilderImpl {
        public InnerBuilder() {
            super(KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY));
        }

        @Override
        protected void addPackageWithResource(PackageDescr pDescr, Resource unused) {
            packageDescr = pDescr;
        }
    }
}
