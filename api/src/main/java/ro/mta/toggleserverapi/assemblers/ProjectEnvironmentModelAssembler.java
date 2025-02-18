package ro.mta.toggleserverapi.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.ProjectEnvironmentDTO;
import ro.mta.toggleserverapi.DTOs.ProjectEnvironmentsResponseDTO;
import ro.mta.toggleserverapi.entities.ProjectEnvironment;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectEnvironmentModelAssembler implements RepresentationModelAssembler<List<ProjectEnvironmentDTO>, EntityModel<ProjectEnvironmentsResponseDTO>>
{
    @Override
    public EntityModel<ProjectEnvironmentsResponseDTO> toModel(List<ProjectEnvironmentDTO> projectEnvironments) {
        ProjectEnvironmentsResponseDTO projectEnvironmentsResponseDTO = new ProjectEnvironmentsResponseDTO();
        projectEnvironmentsResponseDTO.setProjectEnvironmentDTOList(projectEnvironments);

        return EntityModel.of(projectEnvironmentsResponseDTO);
    }
}
