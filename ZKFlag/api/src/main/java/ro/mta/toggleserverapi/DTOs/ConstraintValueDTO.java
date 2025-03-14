package ro.mta.toggleserverapi.DTOs;

import lombok.Data;

@Data
public class ConstraintValueDTO {
    private Long id;
    private String value;
    private Long toggle_environment_id;
    private Long constraint_id;
}
