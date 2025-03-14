package ro.mta.toggleserverapi.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class ProjectAccessDTO {
    private List<UserProjectDTO> users;
}
