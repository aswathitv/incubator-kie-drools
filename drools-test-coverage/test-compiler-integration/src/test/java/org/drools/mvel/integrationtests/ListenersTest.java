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
package org.drools.mvel.integrationtests;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.ListenerModel;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests stateful/stateless KieSession listeners registration - DROOLS-818.
 */
public class ListenersTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    private static final ReleaseId RELEASE_ID = KieServices.Factory.get()
            .newReleaseId("org.drools.mvel.compiler.test", "listeners-test", "1.0.0");

    private static final String PACKAGE = ListenersTest.class.getPackage().getName();
    private static final String PACKAGE_PATH = PACKAGE.replaceAll("\\.", "/");

    private static final String DRL =
            "import java.util.Collection\n"
                    + "rule R1 when\n"
                    + " String()\n"
                    + "then\n"
                    + "end\n";

    private KieServices ks = KieServices.Factory.get();
    private KieSession kieSession;
    private StatelessKieSession statelessKieSession;

    public void init(KieBaseTestConfiguration kieBaseTestConfiguration) {
        ReleaseId kieModuleId = prepareKieModule(kieBaseTestConfiguration);

        final KieContainer kieContainer = ks.newKieContainer(kieModuleId);
        this.kieSession = kieContainer.newKieSession();
        this.statelessKieSession = kieContainer.newStatelessKieSession();
    }

    @AfterEach
    public void cleanup() {
        if (this.kieSession != null) {
            this.kieSession.dispose();
        }
        this.statelessKieSession = null;
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testRegisterAgendaEventListenerStateful(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        init(kieBaseTestConfiguration);
        kieSession.insert("test");
        kieSession.fireAllRules();
        checkThatListenerFired(kieSession.getAgendaEventListeners());
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testRegisterRuleRuntimeEventListenerStateful(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        init(kieBaseTestConfiguration);
        kieSession.insert("test");
        kieSession.fireAllRules();
        checkThatListenerFired(kieSession.getRuleRuntimeEventListeners());
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testRegisterAgendaEventListenerStateless(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        init(kieBaseTestConfiguration);
        statelessKieSession.execute(KieServices.Factory.get().getCommands().newInsert("test"));
        checkThatListenerFired(statelessKieSession.getAgendaEventListeners());
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testRegisterRuleEventListenerStateless(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        init(kieBaseTestConfiguration);
        statelessKieSession.execute(KieServices.Factory.get().getCommands().newInsert("test"));
        checkThatListenerFired(statelessKieSession.getRuleRuntimeEventListeners());
    }

    private void checkThatListenerFired(Collection listeners) {
        assertThat(listeners.size() >= 1).as("Listener not registered.").isTrue();
        MarkingListener listener = getMarkingListener(listeners);
        assertThat(listener.hasFired()).as("Expected listener to fire.").isTrue();
    }

    private MarkingListener getMarkingListener(Collection listeners) {
        for (Object listener : listeners) {
            if (listener instanceof MarkingListener) {
                return (MarkingListener) listener;
            }
        }
        throw new IllegalArgumentException("Expected at least one MarkingListener in the collection");
    }

    /**
     * Inserts a new KieModule containing single KieBase and a stateful and stateless KieSessions with listeners
     * into KieRepository.
     * @param kieBaseTestConfiguration 
     *
     * @return created KIE module ReleaseId
     */
    private ReleaseId prepareKieModule(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final KieServices ks = KieServices.Factory.get();

        KieModuleModel module = ks.newKieModuleModel();

        KieBaseModel baseModel = module.newKieBaseModel("defaultKBase");
        baseModel.setDefault(true);
        baseModel.addPackage("*");

        KieSessionModel sessionModel = baseModel.newKieSessionModel("defaultKSession");
        sessionModel.setDefault(true);
        sessionModel.setType(KieSessionModel.KieSessionType.STATEFUL);
        sessionModel.newListenerModel(MarkingAgendaEventListener.class.getName(), ListenerModel.Kind.AGENDA_EVENT_LISTENER);
        sessionModel.newListenerModel(MarkingRuntimeEventListener.class.getName(), ListenerModel.Kind.RULE_RUNTIME_EVENT_LISTENER);

        KieSessionModel statelessSessionModel = baseModel.newKieSessionModel("defaultStatelessKSession");
        statelessSessionModel.setDefault(true);
        statelessSessionModel.setType(KieSessionModel.KieSessionType.STATELESS);
        statelessSessionModel.newListenerModel(MarkingAgendaEventListener.class.getName(), ListenerModel.Kind.AGENDA_EVENT_LISTENER);
        statelessSessionModel.newListenerModel(MarkingRuntimeEventListener.class.getName(), ListenerModel.Kind.RULE_RUNTIME_EVENT_LISTENER);

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(module.toXML());
        kfs.generateAndWritePomXML(RELEASE_ID);

        kfs.write("src/main/resources/" + PACKAGE_PATH + "/test.drl",
                ResourceFactory.newByteArrayResource(DRL.getBytes()));

        final KieBuilder builder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        assertThat(builder.getResults().getMessages().size()).as("Unexpected compilation errors").isEqualTo(0);

        ks.getRepository().addKieModule(builder.getKieModule());

        return RELEASE_ID;
    }

    /**
     * Listener which just marks that it had fired.
     */
    public interface MarkingListener {

        boolean hasFired();
    }

    /**
     * A listener marking that an AgendaEvent has fired.
     */
    public static class MarkingAgendaEventListener extends DefaultAgendaEventListener implements MarkingListener {

        private final AtomicBoolean fired = new AtomicBoolean(false);

        @Override
        public void afterMatchFired(final AfterMatchFiredEvent event) {
            super.afterMatchFired(event);
            this.fired.compareAndSet(false, true);
        }

        public boolean hasFired() {
            return this.fired.get();
        }
    }

    /**
     * A listener marking that a RuleRuntimeEvent has fired.
     */
    public static class MarkingRuntimeEventListener extends DefaultRuleRuntimeEventListener implements MarkingListener {

        private final AtomicBoolean fired = new AtomicBoolean(false);

        @Override
        public void objectInserted(final ObjectInsertedEvent event) {
            super.objectInserted(event);
            this.fired.compareAndSet(false, true);
        }

        public boolean hasFired() {
            return this.fired.get();
        }
    }

}
