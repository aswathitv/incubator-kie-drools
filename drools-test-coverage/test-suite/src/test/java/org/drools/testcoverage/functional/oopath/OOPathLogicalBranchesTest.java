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
package org.drools.testcoverage.functional.oopath;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.drools.testcoverage.common.model.Address;
import org.drools.testcoverage.common.model.Employee;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests usage of OOPath expressions resulting in multiple conditional branches (e.g. OR operator).
 */
public class OOPathLogicalBranchesTest {

    private static final KieServices KIE_SERVICES = KieServices.Factory.get();

    private KieSession kieSession;
    private List<String> results;

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseConfigurations().stream();
    }

    @AfterEach
    public void disposeKieSession() {
        if (this.kieSession != null) {
            this.kieSession.dispose();
            this.kieSession = null;
            this.results = null;
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testBasicOrCondition(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import org.drools.testcoverage.common.model.Employee;\n" +
                "import org.drools.testcoverage.common.model.Address;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Employee( $address: /address[ street == 'Elm' || city == 'Big City' ] )\n" +
                "then\n" +
                "  list.add( $address.getCity() );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromDRLResources(kieBaseTestConfiguration,
                KIE_SERVICES.getResources().newByteArrayResource(drl.getBytes()));
        this.initKieSession(kieBase);

        this.kieSession.fireAllRules();
        assertThat(this.results).containsExactlyInAnyOrder("Big City", "Small City");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testOrConstraint(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import org.drools.testcoverage.common.model.Employee;\n" +
                "import org.drools.testcoverage.common.model.Address;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  $emp: Employee( $address: /address[ street == 'Elm' || city == 'Big City' ] )\n" +
                "        Employee( this != $emp, /address[ street == 'Elm' || city == 'Big City' ] )\n" +
                "then\n" +
                "  list.add( $address.getCity() );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromDRLResources(kieBaseTestConfiguration,
                KIE_SERVICES.getResources().newByteArrayResource(drl.getBytes()));
        this.initKieSession(kieBase);

        this.kieSession.fireAllRules();
        assertThat(this.results).containsExactlyInAnyOrder("Big City", "Small City");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testOrConstraintWithJoin(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import org.drools.testcoverage.common.model.Employee;\n" +
                "import org.drools.testcoverage.common.model.Address;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  $emp: Employee( $address: /address[ street == 'Elm' || city == 'Big City' ] )\n" +
                "        Employee( this != $emp, /address[ street == $address.street || city == 'Big City' ] )\n" +
                "then\n" +
                "  list.add( $address.getCity() );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromDRLResources(kieBaseTestConfiguration,
                KIE_SERVICES.getResources().newByteArrayResource(drl.getBytes()));
        this.initKieSession(kieBase);

        this.kieSession.fireAllRules();
        assertThat(this.results).containsExactlyInAnyOrder("Big City", "Small City");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testOrConstraintNoBinding(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import org.drools.testcoverage.common.model.Employee;\n" +
                "import org.drools.testcoverage.common.model.Address;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  $emp: Employee( /address[ street == 'Elm' || city == 'Big City' ] )\n" +
                "        Employee( this != $emp, /address[ street == 'Elm' || city == 'Big City' ] )\n" +
                "then\n" +
                "  list.add( $emp.getName() );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromDRLResources(kieBaseTestConfiguration,
                KIE_SERVICES.getResources().newByteArrayResource(drl.getBytes()));
        this.initKieSession(kieBase);

        this.kieSession.fireAllRules();
        assertThat(this.results).containsExactlyInAnyOrder("Bruno", "Alice");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testOrConditionalElement(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import org.drools.testcoverage.common.model.Employee;\n" +
                "import org.drools.testcoverage.common.model.Address;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Employee( $address: /address[ street == 'Elm', city == 'Big City' ] )\n" +
                " or " +
                "  Employee( $address: /address[ street == 'Elm', city == 'Small City' ] )\n" +
                "then\n" +
                "  list.add( $address.getCity() );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromDRLResources(kieBaseTestConfiguration,
                KIE_SERVICES.getResources().newByteArrayResource(drl.getBytes()));
        this.initKieSession(kieBase);

        this.kieSession.fireAllRules();
        assertThat(this.results).containsExactlyInAnyOrder("Big City", "Small City");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testOrConditionalElementNoBinding(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import org.drools.testcoverage.common.model.Employee;\n" +
                "import org.drools.testcoverage.common.model.Address;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                " $employee: (\n" +
                "  Employee( /address[ street == 'Elm', city == 'Big City' ] )\n" +
                " or " +
                "  Employee( /address[ street == 'Elm', city == 'Small City' ] )\n" +
                " )\n" +
                "then\n" +
                "  list.add( $employee.getName() );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromDRLResources(kieBaseTestConfiguration,
                KIE_SERVICES.getResources().newByteArrayResource(drl.getBytes()));
        this.initKieSession(kieBase);

        this.kieSession.fireAllRules();
        assertThat(this.results).containsExactlyInAnyOrder("Bruno", "Alice");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testBasicAndCondition(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import org.drools.testcoverage.common.model.Employee;\n" +
                "import org.drools.testcoverage.common.model.Address;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Employee( $address: /address[ street == 'Elm' && city == 'Big City' ] )\n" +
                "then\n" +
                "  list.add( $address.getCity() );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromDRLResources(kieBaseTestConfiguration,
                KIE_SERVICES.getResources().newByteArrayResource(drl.getBytes()));
        this.initKieSession(kieBase);

        this.kieSession.fireAllRules();
        assertThat(this.results).containsExactly("Big City");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAndConstraint(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import org.drools.testcoverage.common.model.Employee;\n" +
                "import org.drools.testcoverage.common.model.Address;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  $emp: Employee( $address: /address[ street == 'Elm' && city == 'Big City' ] )\n" +
                "        Employee( this != $emp, /address[ street == 'Elm' && city == 'Small City' ] )\n" +
                "then\n" +
                "  list.add( $address.getCity() );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromDRLResources(kieBaseTestConfiguration,
                KIE_SERVICES.getResources().newByteArrayResource(drl.getBytes()));
        this.initKieSession(kieBase);

        this.kieSession.fireAllRules();
        assertThat(this.results).containsExactlyInAnyOrder("Big City");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAndConstraintNoBinding(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import org.drools.testcoverage.common.model.Employee;\n" +
                "import org.drools.testcoverage.common.model.Address;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  $emp: Employee( /address[ street == 'Elm' && city == 'Big City' ] )\n" +
                "        Employee( this != $emp, /address[ street == 'Elm' && city == 'Small City' ] )\n" +
                "then\n" +
                "  list.add( $emp.getName() );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromDRLResources(kieBaseTestConfiguration,
                KIE_SERVICES.getResources().newByteArrayResource(drl.getBytes()));
        this.initKieSession(kieBase);

        this.kieSession.fireAllRules();
        assertThat(this.results).containsExactlyInAnyOrder("Alice");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAndConditionalElement(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import org.drools.testcoverage.common.model.Employee;\n" +
                "import org.drools.testcoverage.common.model.Address;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Employee( $address: /address[ street == 'Elm', city == 'Big City' ] )\n" +
                " and " +
                "  Employee( /address[ street == 'Elm', city == 'Small City' ] )\n" +
                "then\n" +
                "  list.add( $address.getCity() );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromDRLResources(kieBaseTestConfiguration,
                KIE_SERVICES.getResources().newByteArrayResource(drl.getBytes()));
        this.initKieSession(kieBase);

        this.kieSession.fireAllRules();
        assertThat(this.results).containsExactlyInAnyOrder("Big City");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAndConditionalElementWithNot(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import org.drools.testcoverage.common.model.Employee;\n" +
                "import org.drools.testcoverage.common.model.Address;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  $employee: Employee( /address[ street == 'Elm', city == 'Big City' ] )\n" +
                " and " +
                "  not Employee( /address[ street == 'Elm', city == 'Small City' ] )\n" +
                "then\n" +
                "  list.add( $employee.getName() );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromDRLResources(kieBaseTestConfiguration,
                KIE_SERVICES.getResources().newByteArrayResource(drl.getBytes()));
        this.createKieSession(kieBase);

        final Employee bruno = this.createEmployee("Bruno", new Address("Elm", 10, "Small City"));
        final FactHandle brunoFactHandle = this.kieSession.insert(bruno);

        final Employee alice = this.createEmployee("Alice", new Address("Elm", 10, "Big City"));
        this.kieSession.insert(alice);

        this.kieSession.fireAllRules();
        assertThat(this.results).isEmpty();

        this.kieSession.delete(brunoFactHandle);
        this.kieSession.fireAllRules();
        assertThat(this.results).containsExactlyInAnyOrder("Alice");
    }

    private void initKieSession(final KieBase kieBase) {
        this.createKieSession(kieBase);
        this.populateKieSession();
    }

    private void createKieSession(final KieBase kieBase) {
        this.kieSession = kieBase.newKieSession();
        this.results = new ArrayList<>();
        this.kieSession.setGlobal("list", results);
    }

    private void populateKieSession() {
        final Employee bruno = this.createEmployee("Bruno", new Address("Elm", 10, "Small City"));
        this.kieSession.insert(bruno);

        final Employee alice = this.createEmployee("Alice", new Address("Elm", 10, "Big City"));
        this.kieSession.insert(alice);
    }

    private Employee createEmployee(final String name, final Address address) {
        final Employee employee = new Employee(name);
        employee.setAddress(address);
        return employee;
    }

}
