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
package org.drools.core.event.rule.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.kie.api.event.rule.AgendaGroupEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.AgendaGroup;

public class AgendaGroupEventImpl implements AgendaGroupEvent, Externalizable {
    private AgendaGroup agendaGroup;
    private KieRuntime kruntime;

    public AgendaGroupEventImpl(AgendaGroup agendaGroup, KieRuntime kruntime) {
        this.agendaGroup = agendaGroup;
        this.kruntime = kruntime;
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public AgendaGroupEventImpl() {
    }

    public AgendaGroup getAgendaGroup() {
        return agendaGroup;
    }

    public KieRuntime getKieRuntime() {
        return this.kruntime;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        new SerializableAgendaGroup( this.agendaGroup ).writeExternal( out );
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.agendaGroup = new SerializableAgendaGroup( );
        ((SerializableAgendaGroup)this.agendaGroup).readExternal( in );
        this.kruntime = null; // we null this as it isn't serializable
    }

    @Override
    public String toString() {
        return "==>[AgendaGroupEventImpl: getAgendaGroup()=" + getAgendaGroup() + ", getKnowledgeRuntime()="
                + getKieRuntime() + "]";
    }
}
