/*
 * Copyright 2006 Tim Wood
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lexicalscope.jewel.cli.validation;

import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.lexicalscope.jewel.cli.HelpRequestedException;
import com.lexicalscope.jewel.cli.ValidationErrorBuilder;
import com.lexicalscope.jewel.cli.specification.OptionSpecification;
import com.lexicalscope.jewel.cli.specification.OptionsSpecification;
import com.lexicalscope.jewel.cli.specification.ParsedOptionSpecification;

class ArgumentValidatorImpl<O> implements ArgumentValidator
{
    private final ValidationErrorBuilder validationErrorBuilder;

    private final Map<ParsedOptionSpecification, List<String>> validatedArguments = new LinkedHashMap<ParsedOptionSpecification, List<String>>();
    private final List<String> validatedUnparsedArguments = new ArrayList<String>();

    private final OptionsSpecification<O> specification;

    public ArgumentValidatorImpl(
            final OptionsSpecification<O> specification,
            final ValidationErrorBuilder validationErrorBuilder)
    {
        this.specification = specification;
        this.validationErrorBuilder = validationErrorBuilder;
    }

    @Override public void processOption(final String optionName, final List<String> values) {
        if (!specification.isSpecified(optionName))
        {
            validationErrorBuilder.unexpectedOption(optionName);
            return;
        }

        processOption(specification.getSpecification(optionName), values);
    }

    private void processOption(final ParsedOptionSpecification option, final List<String> values) {
        if (option.isHelpOption())
        {
            throw new HelpRequestedException(specification);
        }

        if (!option.allowedThisManyValues(values.size()))
        {
            validationErrorBuilder.wrongNumberOfValues(option, values);
            return;
        }
        else
        {
            checkAndAddValues(option, new ArrayList<String>(values));
        }
    }

    @Override public void processLastOption(final String optionName, final List<String> values) {
        if (!specification.isSpecified(optionName))
        {
            validationErrorBuilder.unexpectedOption(optionName);
            return;
        }

        final ParsedOptionSpecification optionSpecification = specification.getSpecification(optionName);

        processOption(optionSpecification, trimExcessOptions(values, optionSpecification));
    }

    private List<String> trimExcessOptions(
            final List<String> values,
            final ParsedOptionSpecification optionSpecification) {
        if (!specification.hasUnparsedSpecification()) {
            return values;
        }

        final int maximumArgumentConsumption = min(values.size(), optionSpecification.maximumArgumentConsumption());
        validatedUnparsedArguments.addAll(0, values.subList(maximumArgumentConsumption, values.size()));
        return new ArrayList<String>(values.subList(0, maximumArgumentConsumption));
    }

    @Override public OptionCollection finishedProcessing(final List<String> values) {
        validatedUnparsedArguments.addAll(values);
        validateUnparsedOptions();

        validationErrorBuilder.validate();

        for (final ParsedOptionSpecification mandatoryOptionSpecification : specification.getMandatoryOptions())
        {
            if (!validatedArguments.containsKey(mandatoryOptionSpecification))
            {
                validationErrorBuilder.missingOption(mandatoryOptionSpecification);
            }
        }
        validationErrorBuilder.validate();

        return new OptionCollectionImpl(specification, validatedArguments, validatedUnparsedArguments);
    }

    private void validateUnparsedOptions()
    {
        if (specification.hasUnparsedSpecification())
        {
            final OptionSpecification argumentSpecification = specification.getUnparsedSpecification();

            if (argumentSpecification.isOptional() && validatedUnparsedArguments.isEmpty())
            {
                // OK
            }
            else if (!argumentSpecification.allowedThisManyValues(validatedUnparsedArguments.size()))
            {
                validationErrorBuilder.wrongNumberOfValues(argumentSpecification, validatedUnparsedArguments);
            }
        }
        else if (!validatedUnparsedArguments.isEmpty())
        {
            validationErrorBuilder.unexpectedTrailingValue(validatedUnparsedArguments);
        }
    }

    private void checkAndAddValues(
            final ParsedOptionSpecification optionSpecification,
            final ArrayList<String> values)
    {
        if(!values.isEmpty())
        {
            final String pattern = optionSpecification.getPattern();
            for (final String value : values)
            {
                if (!patternMatches(pattern, value))
                {
                    validationErrorBuilder.patternMismatch(optionSpecification, value);
                    return;
                }
            }
        }
        validatedArguments.put(optionSpecification, new ArrayList<String>(values));
    }

    private boolean patternMatches(final String pattern, final String value)
    {
        return value.matches(pattern);
    }
}