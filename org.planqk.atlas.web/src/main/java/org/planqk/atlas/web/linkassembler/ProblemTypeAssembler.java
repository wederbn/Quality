package org.planqk.atlas.web.linkassembler;

import java.util.UUID;

import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.controller.ProblemTypeController;
import org.planqk.atlas.web.dtos.ProblemTypeDto;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProblemTypeAssembler extends GenericLinkAssembler<ProblemTypeDto> {

    @Override
    public void addLinks(EntityModel<ProblemTypeDto> resource) {
        resource.add(links.linkTo(methodOn(ProblemTypeController.class).getProblemTypeById(getId(resource))).withSelfRel());
        resource.add(links.linkTo(methodOn(ProblemTypeController.class).getProblemTypeParentTree(getId(resource)))
                .withRel(Constants.PROBLEM_TYPE_PARENT_TREE));
    }

    private UUID getId(EntityModel<ProblemTypeDto> resource) {
        return resource.getContent().getId();
    }
}
