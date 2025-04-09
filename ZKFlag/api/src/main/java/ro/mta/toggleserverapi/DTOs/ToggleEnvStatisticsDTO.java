package ro.mta.toggleserverapi.DTOs;

import lombok.Data;

@Data
public class ToggleEnvStatisticsDTO {
    String environmentId;
    String environmentName;
    Integer trueCount;
    Integer falseCount;

    Double truePercentage;
    Double falsePercentage;
}
