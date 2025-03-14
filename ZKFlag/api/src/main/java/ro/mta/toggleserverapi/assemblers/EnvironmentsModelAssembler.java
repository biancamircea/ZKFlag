package ro.mta.toggleserverapi.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.DTOs.EnvironmentDTO;
import ro.mta.toggleserverapi.DTOs.EnvironmentsResponseDTO;
import ro.mta.toggleserverapi.controllers.EnvironmentController;
import ro.mta.toggleserverapi.entities.Environment;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EnvironmentsModelAssembler implements RepresentationModelAssembler<List<Environment>, EntityModel<EnvironmentsResponseDTO>> {

    @Override
    public EntityModel<EnvironmentsResponseDTO> toModel(List<Environment> environments) {
        List<EnvironmentDTO> environmentDTOS = environments.stream()
                .map(EnvironmentDTO::toDTO)
                .collect(Collectors.toList());

        EnvironmentsResponseDTO environmentsResponseDTO = new EnvironmentsResponseDTO();
        environmentsResponseDTO.setEnvironmentDTOS(environmentDTOS);
        return EntityModel.of(environmentsResponseDTO,
                linkTo(methodOn(EnvironmentController.class).getAllEnvironments()).withSelfRel());
    }
}
