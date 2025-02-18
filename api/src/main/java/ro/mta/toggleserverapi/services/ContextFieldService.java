package ro.mta.toggleserverapi.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.DTOs.ContextFieldDTO;
import ro.mta.toggleserverapi.DTOs.ContextFieldsResponseDTO;
import ro.mta.toggleserverapi.entities.ContextField;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.exceptions.ContextFieldNotFoundException;
import ro.mta.toggleserverapi.repositories.ContextFieldRepository;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ContextFieldService {
    private final ContextFieldRepository contextFieldRepository;
    private final ProjectService projectService;

    public List<ContextField> fetchAllByProjectId(Long projectId){
        return contextFieldRepository.findAllByProjectId(projectId);
    }

    public ContextField fetchByProjectIdAndContextId(Long projectId, Long contextId){
        return contextFieldRepository.findByIdAndProjectId(contextId, projectId)
                .orElseThrow(() -> new ContextFieldNotFoundException(contextId, projectId));
    }

    public ContextField fetchByProjectIdAndName(String name, Long projectId){
        return contextFieldRepository.findByNameAndProjectId(name, projectId)
                .orElseThrow(() -> new ContextFieldNotFoundException(name, projectId));
    }

    public ContextFieldsResponseDTO getAllFromProject(Long projectId) {
        List<ContextField> contextFields = fetchAllByProjectId(projectId);
        List<ContextFieldDTO> contextFieldDTOS = contextFields.stream()
                .map(ContextFieldDTO::toDTO)
                .collect(Collectors.toList());

        ContextFieldsResponseDTO contextFieldsResponseDTO = new ContextFieldsResponseDTO();
        contextFieldsResponseDTO.setContextFieldDTOS(contextFieldDTOS);
        return contextFieldsResponseDTO;
    }

    public ContextFieldDTO getFromProject(Long projectId, Long contextFieldId) {
        ContextField contextField = fetchByProjectIdAndContextId(projectId, contextFieldId);
        return ContextFieldDTO.toDTO(contextField);
    }



    public ContextFieldDTO saveToProject(ContextField contextField, Long projectId) {
        Project project = projectService.fetchProject(projectId);
        contextField.setProject(project);
        ContextField newContextField = contextFieldRepository.save(contextField);
        return ContextFieldDTO.toDTO(newContextField);
    }

    public ContextFieldDTO updateFromProject(ContextField contextField, Long projectId, Long contextFieldId) {
        return contextFieldRepository.findByIdAndProjectId(contextFieldId, projectId)
                .map(foundContextField -> {
                    foundContextField.setName(contextField.getName());
                    foundContextField.setDescription(contextField.getDescription());
                    return contextFieldRepository.save(foundContextField);
                })
                .map(ContextFieldDTO::toDTO)
                .orElseGet(() -> saveToProject(contextField, projectId));
    }

    @Transactional
    public void deleteFromProject(Long projectId, Long contextFieldId) {
        contextFieldRepository.deleteByIdAndProjectId(contextFieldId, projectId);
    }

}
