package ro.mta.toggleserverapi.DTOs;

import lombok.Data;

@Data
public class PayloadDTO {
    private String enabledValue;
    private String disabledValue;
}
