/*******************************************************************************
 * Copyright (c) 2020-2021 the qc-atlas contributors.
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

package org.planqk.atlas.web.utils;

import java.util.UUID;

import org.planqk.atlas.web.controller.exceptions.InvalidRequestException;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.dtos.PatternRelationDto;

public final class ControllerValidationUtils {

    private ControllerValidationUtils() {
    }

    public static void checkIfAlgorithmIsInAlgorithmRelationDTO(
            UUID algorithmId, AlgorithmRelationDto algorithmRelationDto) {
        if (!algorithmRelationDto.getSourceAlgorithmId().equals(algorithmId)
                && !algorithmRelationDto.getTargetAlgorithmId().equals(algorithmId)) {
            throw new InvalidRequestException("AlgorithmId \"" + algorithmId + "\" does not match any Ids of the " +
                    "AlgorithmRelation request body");
        }
    }

    public static void checkIfAlgorithmIsInPatternRelationDTO(UUID algorithmId, PatternRelationDto patternRelationDto) {
        if (!patternRelationDto.getAlgorithmId().equals(algorithmId)) {
            throw new InvalidRequestException("AlgorithmId \"" + algorithmId + "\" does not match Id of the " +
                    "PatternRelation request body");
        }
    }
}
