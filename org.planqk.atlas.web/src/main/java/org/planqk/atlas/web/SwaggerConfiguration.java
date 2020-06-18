/*******************************************************************************
 * Copyright (c) 2020 University of Stuttgart
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
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
 *******************************************************************************/
package org.planqk.atlas.web;

import java.util.Map;

import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ClassicAlgorithmDto;
import org.planqk.atlas.web.dtos.QuantumAlgorithmDto;
import org.planqk.atlas.web.utils.EntityModelConverter;
import org.planqk.atlas.web.utils.LinkRemoverModelConverter;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * This configuration contains the Swagger / SpringDoc configurations we need.
 *
 * This includes custom ModelConverters and type overrides.
 */
@Configuration
public class SwaggerConfiguration {
    @Bean
    @Lazy(false)
    public LinkRemoverModelConverter linkRemoverModelConverter() {
        final var converter = new LinkRemoverModelConverter();
        ModelConverters.getInstance().addConverter(converter);
        return converter;
    }

    @Bean
    @Lazy(false)
    public EntityModelConverter entityModelConverter() {
        final var converter = new EntityModelConverter(Map.of(
                AlgorithmDto.class, AlgorithmSchema.class
                ));
        ModelConverters.getInstance().addConverter(converter);
        return converter;
    }

    // The private classes below provide custom schemas for certain types used in our public API.
    // Setting these annotations on the correct types is not always possible, because we could end up with
    // reference cycles, for example:
    //
    // AlgorithmDto -- (via oneOf) --> ClassicAlgorithmDto -- (extends) --> AlgorithmDto

    @Schema(
            name = "AlgorithmDto",
            title = "quantum/classic algorithm",
            description = "Either a quantum or a classic algorithm",
            oneOf = {ClassicAlgorithmDto.class, QuantumAlgorithmDto.class},
            discriminatorMapping = {
                    @DiscriminatorMapping(value = "CLASSIC", schema = ClassicAlgorithmDto.class),
                    @DiscriminatorMapping(value = "QUANTUM", schema = QuantumAlgorithmDto.class),
            }
    )
    private static class AlgorithmSchema {
    }
}