package ro.mta.toggleserverapi.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.*;

import java.util.List;

@Component
public class InstanceEnvironmentModelAssembler implements RepresentationModelAssembler<List<InstanceEnvironmentDTO>, EntityModel<InstanceEnvironmentsResponseDTO>> {
    @Override
    public EntityModel<InstanceEnvironmentsResponseDTO> toModel(List<InstanceEnvironmentDTO> instanceEnvironments) {
        InstanceEnvironmentsResponseDTO instanceEnvironmentsResponseDTO = new InstanceEnvironmentsResponseDTO();
        instanceEnvironmentsResponseDTO.setInstanceEnvironmentDTOList(instanceEnvironments);

        return EntityModel.of(instanceEnvironmentsResponseDTO);
    }
}
