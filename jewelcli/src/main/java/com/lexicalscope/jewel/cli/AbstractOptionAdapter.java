package com.lexicalscope.jewel.cli;

import static com.lexicalscope.fluentreflection.FluentReflection.type;
import static com.lexicalscope.fluentreflection.ReflectionMatchers.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.lexicalscope.fluentreflection.FluentClass;
import com.lexicalscope.fluentreflection.FluentMethod;

/*
 * Copyright 2011 Tim Wood
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

abstract class AbstractOptionAdapter implements OptionAdapter {
    private final FluentClass<?> klass;
    protected final FluentMethod method;
    private final FluentClass<?> methodType;

    AbstractOptionAdapter(
            final FluentClass<?> klass,
            final FluentMethod method) {
        this.klass = klass;
        this.method = method;
        if (isMutator().matches(method))
        {
            this.methodType = method.args().get(0);
        }
        else
        {
            this.methodType = method.type();
        }
    }

    @Override public final FluentMethod method() {
        return method;
    }

    @Override public final FluentMethod correspondingOptionalityMethod() {
        if (isMutator().matches(method))
        {
            return null;
        }

        final List<FluentMethod> methods =
                klass.methods(
                        hasName(addPrefix("is", method.property())).and(isExistence()));
        if (!methods.isEmpty()) {
            return methods.get(0);
        }
        return null;
    }

    private String addPrefix(final String prefix, final String name) {
        return prefix + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    @Override public final boolean isMultiValued() {
        return methodType.isType(reflectingOn(Collection.class)) && (methodType.assignableFrom(List.class) || methodType.assignableFrom(Set.class));
    }

    @Override public final FluentClass<? extends Object> getValueType() {
        final FluentClass<? extends Object> valueType =
                isMultiValued()
                        ? methodType.asType(reflectingOn(Collection.class)).typeArgument(0)
                        : methodType;

        return reflectingOn(Object.class).matches(valueType)
                ? type(String.class)
                : valueType;
    }

    @Override public final boolean hasDefaultValue() {
        return !(defaultValue().length == 1 && defaultValue()[0].equals(Option.stringToMarkNoDefault));
    }
}
