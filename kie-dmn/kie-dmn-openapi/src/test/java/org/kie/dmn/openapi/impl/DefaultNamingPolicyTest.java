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
package org.kie.dmn.openapi.impl;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.SimpleTypeImpl;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultNamingPolicyTest {

    @Test
    void test() {
        DefaultNamingPolicy ut = new DefaultNamingPolicy("#/definitions/");

        assertThat(ut.getName(unregisteredType("tPerson"))).isEqualTo("tPerson");
        assertThat(ut.getName(unregisteredType("my person type"))).isEqualTo("my_32person_32type");
        assertThat(ut.getName(unregisteredType("my_person_type"))).isEqualTo("my__person__type");
        assertThat(ut.getName(unregisteredType("my-person-type"))).isEqualTo("my_45person_45type");
    }

    private static DMNType unregisteredType(String name) {
        return new SimpleTypeImpl("ns1", name, UUID.randomUUID().toString());
    }
}
