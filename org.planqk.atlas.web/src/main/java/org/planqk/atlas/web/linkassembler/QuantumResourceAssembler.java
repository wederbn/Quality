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

package org.planqk.atlas.web.linkassembler;

import org.planqk.atlas.web.controller.QuantumResourceController;
import org.planqk.atlas.web.controller.QuantumResourceTypeController;
import org.planqk.atlas.web.dtos.QuantumResourceDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SuppressWarnings("ConstantConditions")
@Component
public class QuantumResourceAssembler extends GenericLinkAssembler<QuantumResourceDto> {
    @Override
    public void addLinks(EntityModel<QuantumResourceDto> resource) {
        resource.add(links.linkTo(methodOn(QuantumResourceController.class)
                .deleteQuantumResource(resource.getContent().getId()))
                .withRel("delete"));
        resource.add(links.linkTo(methodOn(QuantumResourceController.class)
                .getQuantumResource(resource.getContent().getId()))
                .withSelfRel());
        resource.add(links.linkTo(methodOn(QuantumResourceTypeController.class)
                .getQuantumResourceType(resource.getContent().getType().getId()))
                .withRel("type"));
        // TODO (Maybe) add link to the entities linked to this quantum Resource, e.g. the Quantum Algorithms
    }
}