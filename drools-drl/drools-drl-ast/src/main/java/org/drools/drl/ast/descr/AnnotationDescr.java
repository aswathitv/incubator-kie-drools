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
package org.drools.drl.ast.descr;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AnnotationDescr extends AnnotatedBaseDescr implements Supplier<Map<String, Object>> {

    public static final String VALUE = "value";
    private static final long serialVersionUID = 520l;

    private String name;
    private String fullyQualifiedName;
    private Map<String, Object> values;

    private boolean duplicated = false;
    private boolean strict = false;

    // '' and 'a' are passed through as 
    public static String unquote(String s) {
        if (s.startsWith("\"") && s.endsWith("\"") ||
            s.startsWith("'") && s.endsWith("'")) {
            return s.substring(1, s.length() - 1);
        } else {
            return s;
        }
    }

    public AnnotationDescr() {
    }

    public AnnotationDescr(final String name) {
        this.name = name;
        this.values = new HashMap<>();
    }

    public AnnotationDescr(final String name,
                           final String value) {
        this(name);
        this.values.put(VALUE, value);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
        super.readExternal(in);
        this.name = (String) in.readObject();
        this.values = (Map<String, Object>) in.readObject();
        this.fullyQualifiedName = (String) in.readObject();
        this.duplicated = in.readBoolean();
        this.strict = in.readBoolean();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject(name);
        out.writeObject(values);
        out.writeObject(fullyQualifiedName);
        out.writeBoolean(duplicated);
        out.writeBoolean(strict);
    }

    public String getName() {
        return this.name;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public void setFullyQualifiedName(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
    }

    public boolean hasValue() {
        return !this.values.isEmpty();
    }

    public void setValue(final Object value) {
        this.values.put(VALUE, value);
    }

    public void setKeyValue(final String key,
                            final Object value) {
        this.values.put(key, value);
    }

    public Object getValue(final String key) {
        return this.values.get(key);
    }

    @Override
    public Map<String, Object> get() {
        return this.values;
    }

    /**
     * Returns the metadata value as a single object or a Map
     *
     * @return
     */
    public Object getValue() {
        Object single = getSingleValue();
        return single != null ? single : this.values;
    }

    public Map<String, Object> getValueMap() {
        return this.values;
    }

    public Object getSingleValue() {
        if (values.size() == 1 && values.containsKey(VALUE)) {
            return this.values.get(VALUE);
        } else {
            return null;
        }
    }

    public Object getSingleValueStripped() {
        if (values.size() == 1 && values.containsKey(VALUE)) {
            return unquote( this.values.get( VALUE ).toString() );
        } else {
            return null;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AnnotationDescr other = (AnnotationDescr) obj;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        return true;
    }

    public String getSingleValueAsString() {
        return getValueAsString( VALUE );
    }

    public String getValueAsString( String key ) {
        Object x = getValue( key );
        if ( x == null ) {
            return null;
        } else if ( x.getClass().isArray() ) {
            return Arrays.toString( (Object[]) x );
        } else {
            return x.toString();
        }
    }

    public boolean isDuplicated() {
        return duplicated;
    }

    public void setDuplicated() {
        this.duplicated = true;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }
}
