/********************************************************************************
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

package org.planqk.atlas.web.dtos;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.planqk.atlas.core.model.Algorithm;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.WRITE_ONLY;

/**
 * Data transfer object for Algorithms ({@link org.planqk.atlas.core.model.Algorithm}).
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
public class AlgorithmDto extends RepresentationModel<AlgorithmDto> {

    private Long id;

    private String name;

    private String inputFormat;

    private String outputFormat;

    // we do not embedded tags into the object (via @jsonInclude) - instead, we add a hateoas link to the associated tags
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    // annotate this for swagger as well, because swagger doesn't recognize the json property annotation
    @Schema(accessMode = WRITE_ONLY)
    private Set<TagDto> tags;

    private Object content;

    public static final class Converter {

        public static AlgorithmDto convert(final Algorithm object) {
            final AlgorithmDto dto = new AlgorithmDto();
            dto.setId(object.getId());
            dto.setName(object.getName());
            dto.setContent(object.getContent());
            dto.setTags(object.getTags().stream().map(TagDto.Converter::convert).collect(Collectors.toSet()));
            dto.setInputFormat(object.getInputFormat());
            dto.setOutputFormat(object.getInputFormat());
            return dto;
        }

        public static Algorithm convert(final AlgorithmDto object) {
            final Algorithm algo = new Algorithm();
            algo.setName(object.getName());
            algo.setContent(object.getContent());
            if (Objects.nonNull(object.getTags())) {
                algo.setTags(object.getTags().stream().map(TagDto.Converter::convert).collect(Collectors.toSet()));
            }
            algo.setInputFormat(object.getInputFormat());
            algo.setOutputFormat(object.getInputFormat());
            return algo;
        }
    }
}