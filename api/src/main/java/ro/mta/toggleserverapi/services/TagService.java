package ro.mta.toggleserverapi.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hashids.Hashids;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.DTOs.TagDTO;
import ro.mta.toggleserverapi.DTOs.TagsResponseDTO;
import ro.mta.toggleserverapi.entities.Project;
import ro.mta.toggleserverapi.entities.Tag;
import ro.mta.toggleserverapi.exceptions.TagNotFoundException;
import ro.mta.toggleserverapi.repositories.TagRepository;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TagService {
    private final ProjectService projectService;

    private final TagRepository tagRepository;

    public List<Tag> fetchAllByProjectId(Long projectId){
        return tagRepository.findAllByProjectId(projectId);
    }
    public Tag fetchByProjectIdAndTagId(Long projectId, Long tagId){
        return tagRepository.findByIdAndProjectId(tagId, projectId)
                .orElseThrow(() -> new TagNotFoundException(tagId, projectId));
    }
    public TagsResponseDTO getAllFromProject(Long projectId) {
        List<Tag> tags = fetchAllByProjectId(projectId);
        List<TagDTO> tagDTOs = tags.stream()
                .map(TagDTO::toDTO)
                .collect(Collectors.toList());

        TagsResponseDTO tagsResponseDTO = new TagsResponseDTO();
        tagsResponseDTO.setTagDTOs(tagDTOs);
        return tagsResponseDTO;
    }
    public TagDTO getFromProject(Long projectId, Long tagId) {
        Tag tag = fetchByProjectIdAndTagId(projectId, tagId);
        return TagDTO.toDTO(tag);
    }

    public TagDTO saveToProject(Tag tag, Long projectId) {
        Project project = projectService.fetchProject(projectId);
        tag.setProject(project);
        Tag savedTag = tagRepository.save(tag);

        if (savedTag.getHashId() == null || savedTag.getHashId().isEmpty()) {
            Hashids hashids = new Hashids("tag-salt", 8);
            savedTag .setHashId(hashids.encode(savedTag.getId()));

            savedTag = tagRepository.save(savedTag);
        }

        return TagDTO.toDTO(savedTag);
    }
    public TagDTO updateFromProject(Tag newTag, Long projectId, Long tagId) {
        return tagRepository.findByIdAndProjectId(tagId, projectId)
                .map(tag -> {
                    tag.setLabelName(newTag.getLabelName());
                    tag.setDescription(newTag.getDescription());
                    tag.setColor(newTag.getColor());
                    return tagRepository.save(tag);
                })
                .map(TagDTO::toDTO)
                .orElseGet(() -> saveToProject(newTag, projectId));
    }
    @Transactional
    public void deleteFromProject(Long projectId, Long tagId) {
        tagRepository.deleteByIdAndProjectId(tagId, projectId);
    }


}
