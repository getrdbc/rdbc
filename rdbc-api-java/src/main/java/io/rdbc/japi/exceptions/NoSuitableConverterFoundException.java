/*
 * Copyright 2016 rdbc contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rdbc.japi.exceptions;

public class NoSuitableConverterFoundException extends RdbcException {

    private final Object value;

    public NoSuitableConverterFoundException(Object value) {
        super(String.format(
                "No suitable converter was found for value '%s' of type %s",
                value,
                value.getClass()
        ));
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
