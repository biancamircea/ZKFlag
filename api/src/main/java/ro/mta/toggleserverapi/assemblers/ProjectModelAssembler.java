package ro.mta.toggleserverapi.assemblers;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.controllers.ProjectController;
import ro.mta.toggleserverapi.entities.Project;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProjectModelAssembler implements RepresentationModelAssembler<Project, EntityModel<Project>> {
    @Override
    public EntityModel<Project> toModel(Project project) {
        return EntityModel.of(project,
                linkTo(methodOn(ProjectController.class).getProjectOverview(project.getHashId())).withSelfRel(),
                linkTo(methodOn(ProjectController.class).getAllProjects()).withRel("projects"));
    }

    @Override
    public CollectionModel<EntityModel<Project>> toCollectionModel(Iterable<? extends Project> entities) {
        List<EntityModel<Project>> entityModels = StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(entityModels,
                linkTo(methodOn(ProjectController.class).getAllProjects()).withSelfRel());
    }
}
