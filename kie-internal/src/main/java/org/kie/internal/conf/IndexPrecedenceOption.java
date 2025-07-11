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
package org.kie.internal.conf;

import org.kie.api.conf.OptionKey;
import org.kie.api.conf.SingleValueKieBaseOption;
import org.kie.api.conf.SingleValueRuleBaseOption;

/**
 * An Enum for Index Precedence option.
 *
 * drools.indexPrecedence = &lt;pattern|equality&gt;
 *
 * When creating indexes gives precedence to the equality constraints (default)
 * or to the first indexable constraint in the pattern.
 *
 * DEFAULT = equality
 */
public enum IndexPrecedenceOption implements SingleValueRuleBaseOption {

    PATTERN_ORDER("pattern"),
    EQUALITY_PRIORITY("equality");

    /**
     * The property name for the index precedence option
     */
    public static final String PROPERTY_NAME = "drools.indexPrecedence";

    public static OptionKey<IndexPrecedenceOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    private String             string;

    IndexPrecedenceOption(String mode) {
        this.string = mode;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public String getValue() {
        return string;
    }

    public String toString() {
        return "IndexPrecedenceOption( "+string+ " )";
    }

    public String toExternalForm() {
        return this.string;
    }

    public static IndexPrecedenceOption determineIndexPrecedence(String mode) {
        if ( PATTERN_ORDER.getValue().equalsIgnoreCase( mode ) ) {
            return PATTERN_ORDER;
        } else if ( EQUALITY_PRIORITY.getValue().equalsIgnoreCase( mode ) ) {
            return EQUALITY_PRIORITY;
        }
        throw new IllegalArgumentException( "Illegal enum value '" + mode + "' for IndexPrecedence" );
    }

}
