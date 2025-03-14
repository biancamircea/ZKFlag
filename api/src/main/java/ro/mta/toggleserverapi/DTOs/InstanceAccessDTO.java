package ro.mta.toggleserverapi.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class InstanceAccessDTO {
    private List<UserInstanceDTO> users;
}
