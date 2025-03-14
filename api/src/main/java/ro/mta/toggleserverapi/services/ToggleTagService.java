package ro.mta.toggleserverapi.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.mta.toggleserverapi.entities.Tag;
import ro.mta.toggleserverapi.entities.Toggle;
import ro.mta.toggleserverapi.entities.ToggleTag;
import ro.mta.toggleserverapi.entities.ToggleTagKey;
import ro.mta.toggleserverapi.exceptions.TagNotFoundException;
import ro.mta.toggleserverapi.repositories.TagRepository;
import ro.mta.toggleserverapi.repositories.ToggleRepository;
import ro.mta.toggleserverapi.repositories.ToggleTagRepository;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ToggleTagService {
    private final ToggleTagRepository toggleTagRepository;
    private final ToggleRepository toggleRepository;
    private final TagRepository tagRepository;

    public void saveToggleTag(Toggle toggle, Tag tag){
        ToggleTagKey toggleTagKey = new ToggleTagKey();
        toggleTagKey.setToggleId(toggle.getId());
        toggleTagKey.setTagId(tag.getId());

        ToggleTag toggleTag = new ToggleTag();
        toggleTag.setId(toggleTagKey);
        toggleTag.setToggle(toggle);
        toggleTag.setTag(tag);
        toggleTagRepository.save(toggleTag);
    }

    public Tag fetchTagFromProject(Long tagId, Long projectId){
        return tagRepository.findByIdAndProjectId(tagId, projectId)
                .orElseThrow(() -> new TagNotFoundException(tagId, projectId));
    }

    public List<Tag> fetchAllTagsByToggle(Toggle toggle){
        return toggleTagRepository.findAllByToggle(toggle)
                .stream()
                .map(toggleTag -> {
                    return toggleTag.getTag();
                })
                .collect(Collectors.toList());
    }

    public void assignTagToToggle(Toggle toggle, Long tagId, Long projectId) {
        Tag tag = fetchTagFromProject(tagId, projectId);
        saveToggleTag(toggle, tag);
    }

    @Transactional
    public void deleteTagFromToggle(Toggle toggle, Long tagId, Long projectId){
        Tag tag = fetchTagFromProject(tagId, projectId);
        toggleTagRepository.deleteByToggleAndTag(toggle, tag);
    }
}
