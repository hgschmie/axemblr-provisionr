/*
 * Copyright (c) 2012 S.C. Axemblr Software Solutions S.R.L
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.axemblr.provisionr.commands.functions;

import com.axemblr.provisionr.api.Provisionr;
import com.google.common.base.Predicate;

public class ProvisionrPredicates {

    private ProvisionrPredicates() {
    }

    public static Predicate<Provisionr> withId(final String id) {
        return new Predicate<Provisionr>() {

            @Override
            public boolean apply(Provisionr candidate) {
                return candidate.getId().equals(id);
            }

            @Override
            public String toString() {
                return "WithId{id=" + id + "}";
            }
        };
    }
}
