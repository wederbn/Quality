package org.planqk.atlas.web.dtos;

import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.planqk.atlas.core.model.QuantumResourceDataType;
import org.planqk.atlas.core.model.QuantumResourceType;
import org.springframework.hateoas.RepresentationModel;


@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class QuantumResourceTypeDto extends RepresentationModel<QuantumResourceTypeDto> {

    private UUID id;
    private String name;
    private String description;
    private QuantumResourceDataType dataType;

    public static final class Converter {

        public static QuantumResourceTypeDto convert(final QuantumResourceType object) {
            final var dto = new QuantumResourceTypeDto();
            dto.setId(object.getId());
            dto.setName(object.getName());
            dto.setDescription(object.getDescription());
            dto.setDataType(object.getDataType());
            return dto;
        }

        public static QuantumResourceType convert(final QuantumResourceTypeDto object) {
            final var type = new QuantumResourceType();
            type.setName(object.getName());
            type.setDescription(object.getDescription());
            type.setDataType(object.getDataType());
            return type;
        }
    }
}
