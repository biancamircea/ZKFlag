package ro.mta.toggleserverapi.assemblers;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ro.mta.toggleserverapi.controllers.ToggleController;
import ro.mta.toggleserverapi.entities.Toggle;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ToggleModelAssembler implements RepresentationModelAssembler<Toggle, EntityModel<Toggle>> {


    @Override
    public EntityModel<Toggle> toModel(Toggle toggle) {
        EntityModel<Toggle> entityModel = EntityModel.of(toggle,
                linkTo(methodOn(ToggleController.class).getToggle(toggle.getId())).withSelfRel(),
                linkTo(methodOn(ToggleController.class).getAllToggles()).withRel("toggles"));

//        entityModel.add(linkTo(methodOn(ToggleController.class).getToggleProject(toggle.getId())).withRel("project"));
//        if(toggle.getProject()!=null){
//        }
        return entityModel;
    }

    @Override
    public CollectionModel<EntityModel<Toggle>> toCollectionModel(Iterable<? extends Toggle> entities) {
        List<EntityModel<Toggle>> entityModels = StreamSupport.stream(entities.spliterator(),false)
                .map(this::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(entityModels,
                linkTo(methodOn(ToggleController.class).getAllToggles()).withSelfRel());
    }
}
