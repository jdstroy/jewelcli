package com.lexicalscope.jewel.cli;

import com.lexicalscope.jewel.cli.specification.OptionSpecification;

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

class ValidationFailureUnableToConstructType extends ValidationFailureImpl
{
    private static final long serialVersionUID = 7263131866771297371L;

    public ValidationFailureUnableToConstructType(final OptionSpecification specification, final String message) {
        super(specification, String.format(
                ArgumentValidationException.m_messages.getString("validationError.UnableToConstructType"),
                message));
    }

    @Override public ValidationFailureType getFailureType() {
        return ValidationFailureType.UnableToConstructType;
    }
}