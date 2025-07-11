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
package org.drools.mvel.integrationtests.eventgenerator;

import java.io.IOException;
import java.util.stream.Stream;

import org.drools.drl.parser.DroolsParserException;
import org.drools.mvel.integrationtests.eventgenerator.Event.EventType;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleEventGeneratorTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    private final static String TEST_RULE_FILE = "test_eventGenerator.drl";

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testEventGenerationMaxItems(KieBaseTestConfiguration kieBaseTestConfiguration) throws DroolsParserException, IOException, Exception{
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, TEST_RULE_FILE);
        KieSession ksession = kbase.newKieSession();

        // create unrestricted event generator
        SimpleEventGenerator myGenerator = new SimpleEventGenerator(ksession , new SimpleEventListener(ksession));
        // generate 10 events, starting from the session clock
        myGenerator.addEventSource("Conveyor1", new Event(EventType.CUSTOM, null), PseudoSessionClock.timeInSeconds(4), PseudoSessionClock.timeInSeconds(6), 0, 10);
        myGenerator.generate();
        assertThat(10).isEqualTo(ksession.getQueryResults("all inserted events").size());
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testEventGenerationMaxTime(KieBaseTestConfiguration kieBaseTestConfiguration) throws DroolsParserException, IOException, Exception{
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, TEST_RULE_FILE);
        KieSession ksession = kbase.newKieSession();

        // create unrestricted event generator
        SimpleEventGenerator myGenerator = new SimpleEventGenerator(ksession , new SimpleEventListener(ksession));
        // generate events for 1 min, starting from the session clock
        myGenerator.addEventSource("Conveyor1", new Event(EventType.CUSTOM, null), PseudoSessionClock.timeInSeconds(4), PseudoSessionClock.timeInSeconds(6), PseudoSessionClock.timeInMinutes(1), 0);
        myGenerator.generate();
        assertThat(ksession.getQueryResults("all inserted events with generation time < 1 min").size()).isEqualTo(ksession.getQueryResults("all inserted events").size());
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testEventGenerationMaxTimeAndMaxItems(KieBaseTestConfiguration kieBaseTestConfiguration) throws DroolsParserException, IOException, Exception{
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, TEST_RULE_FILE);
        KieSession ksession = kbase.newKieSession();

        // create unrestricted event generator
        SimpleEventGenerator myGenerator = new SimpleEventGenerator(ksession , new SimpleEventListener(ksession));
        // generate at most 10 events not exceeding 1 min, starting from the session clock
        myGenerator.addEventSource("Conveyor1", new Event(EventType.CUSTOM, null), PseudoSessionClock.timeInSeconds(4), PseudoSessionClock.timeInSeconds(6), PseudoSessionClock.timeInMinutes(1), 10);
        myGenerator.generate();
        assertThat(ksession.getQueryResults("all inserted events with generation time < 1 min").size()).isEqualTo(ksession.getQueryResults("all inserted events").size());
        assertThat(ksession.getQueryResults("all inserted events with generation time < 1 min").size() <= 10).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testEventGenerationDelayedMaxItems(KieBaseTestConfiguration kieBaseTestConfiguration) throws DroolsParserException, IOException, Exception{
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, TEST_RULE_FILE);
        KieSession ksession = kbase.newKieSession();

        // create unrestricted event generator
        SimpleEventGenerator myGenerator = new SimpleEventGenerator(ksession , new SimpleEventListener(ksession));
        // generate 10 events, delayed by 2 minutes from start session clock
        myGenerator.addDelayedEventSource("Conveyor1", new Event(EventType.CUSTOM, null), PseudoSessionClock.timeInSeconds(4), PseudoSessionClock.timeInSeconds(6), PseudoSessionClock.timeInMinutes(2), 0, 10);
        myGenerator.generate();
        assertThat(10).isEqualTo(ksession.getQueryResults("all inserted events").size());
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testEventGenerationDelayedMaxTime(KieBaseTestConfiguration kieBaseTestConfiguration) throws DroolsParserException, IOException, Exception{
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, TEST_RULE_FILE);
        KieSession ksession = kbase.newKieSession();

        // create unrestricted event generator
        SimpleEventGenerator myGenerator = new SimpleEventGenerator(ksession , new SimpleEventListener(ksession));
        // generate events for 1 min, delayed by 2 minutes from start session clock
        myGenerator.addDelayedEventSource("Conveyor1", new Event(EventType.CUSTOM, null), PseudoSessionClock.timeInSeconds(4), PseudoSessionClock.timeInSeconds(6), PseudoSessionClock.timeInMinutes(2), PseudoSessionClock.timeInMinutes(1), 0);
        myGenerator.generate();
        assertThat(ksession.getQueryResults("all inserted events with 2 min < generation time < 3 min").size()).isEqualTo(ksession.getQueryResults("all inserted events").size());
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testEventGenerationDelayedMaxTimeAndMaxItems(KieBaseTestConfiguration kieBaseTestConfiguration) throws DroolsParserException, IOException, Exception{
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, TEST_RULE_FILE);
        KieSession ksession = kbase.newKieSession();

        // create unrestricted event generator
        SimpleEventGenerator myGenerator = new SimpleEventGenerator(ksession , new SimpleEventListener(ksession));
        // generate at most 10 events not exceeding 1 min, delayed by 2 minutes from start session clock
        myGenerator.addDelayedEventSource("Conveyor1", new Event(EventType.CUSTOM, null), PseudoSessionClock.timeInSeconds(4), PseudoSessionClock.timeInSeconds(6), PseudoSessionClock.timeInMinutes(2), PseudoSessionClock.timeInMinutes(1), 10);
        myGenerator.generate();
        assertThat(ksession.getQueryResults("all inserted events with 2 min < generation time < 3 min").size()).isEqualTo(ksession.getQueryResults("all inserted events").size());
        assertThat(ksession.getQueryResults("all inserted events with 2 min < generation time < 3 min").size() <= 10).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testEventGenerationGlobalMaxTime(KieBaseTestConfiguration kieBaseTestConfiguration) throws DroolsParserException, IOException, Exception{
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, TEST_RULE_FILE);
        KieSession ksession = kbase.newKieSession();

        // create unrestricted event generator
        SimpleEventGenerator myGenerator = new SimpleEventGenerator(ksession , new SimpleEventListener(ksession), PseudoSessionClock.timeInMinutes(1));

        // generate events for 1 min, starting from the session clock
        myGenerator.addEventSource("Conveyor1", new Event(EventType.CUSTOM, null), PseudoSessionClock.timeInSeconds(4), PseudoSessionClock.timeInSeconds(6), PseudoSessionClock.timeInMinutes(3), 0);
        myGenerator.generate();
        assertThat(ksession.getQueryResults("all inserted events with generation time < 1 min").size()).isEqualTo(ksession.getQueryResults("all inserted events").size());
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testEventGenerationMultipleSources(KieBaseTestConfiguration kieBaseTestConfiguration) throws DroolsParserException, IOException, Exception{
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, TEST_RULE_FILE);
        KieSession ksession = kbase.newKieSession();

        // create unrestricted event generator
        SimpleEventGenerator myGenerator = new SimpleEventGenerator(ksession , new SimpleEventListener(ksession));
        // generate 15 events with parent resource A and 20 events with parent resource B
        myGenerator.addEventSource("Conveyor1", new Event(EventType.CUSTOM, "resA"), PseudoSessionClock.timeInSeconds(4), PseudoSessionClock.timeInSeconds(6), 0, 15);
        myGenerator.addEventSource("Conveyor2", new Event(EventType.CUSTOM, "resB"), PseudoSessionClock.timeInSeconds(3), PseudoSessionClock.timeInSeconds(5), 0, 20);
        myGenerator.generate();
        assertThat(15).isEqualTo(ksession.getQueryResults("all inserted events with parent resource A").size());
        assertThat(20).isEqualTo(ksession.getQueryResults("all inserted events with parent resource B").size());
    }

}
