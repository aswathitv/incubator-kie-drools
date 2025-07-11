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
package org.drools.traits.core.factmodel;

import org.drools.base.factmodel.traits.Thing;
import org.drools.base.factmodel.traits.TraitFieldTMS;
import org.drools.base.factmodel.traits.Traitable;
import org.drools.base.factmodel.traits.TraitableBean;
import org.kie.api.definition.type.PropertyReactive;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@Traitable
@PropertyReactive
public class Entity implements TraitableBean<Entity,Entity>, Serializable {


    private String id;

    private TraitFieldTMS __$$field_Tms$$ = new TraitFieldTMSImpl();
    private Map<String,Object> __$$dynamic_properties_map$$;
    private Map<String, Thing<Entity>> __$$dynamic_traits_map$$;

    public Entity() {
        id = UUID.randomUUID().toString();
    }

    public Entity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> _getDynamicProperties() {
        return  __$$dynamic_properties_map$$;
    }

    public void _setDynamicProperties(Map map) {
        __$$dynamic_properties_map$$ = map;
    }


    public TraitFieldTMS _getFieldTMS() {
        if ( __$$field_Tms$$ == null ) {
            __$$field_Tms$$ = new TraitFieldTMSImpl();
        }
        return __$$field_Tms$$;
    }

    public void _setFieldTMS( TraitFieldTMS __$$field_Tms$$ ) {
        this.__$$field_Tms$$ = __$$field_Tms$$;
    }

    public void _setTraitMap(Map map) {
        __$$dynamic_traits_map$$ = map;
    }


    public Map<String, Thing<Entity>> _getTraitMap() {
        return __$$dynamic_traits_map$$;
    }

    @Override
    public String toString() {
        return "Entity{" +
               "id='" + id + '\'' +
               '}';
    }
}

