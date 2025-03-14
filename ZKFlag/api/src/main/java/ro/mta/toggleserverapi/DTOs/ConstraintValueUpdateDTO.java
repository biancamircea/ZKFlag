// src/main/java/ro/mta/toggleserverapi/DTOs/ConstraintValueUpdateDTO.java
package ro.mta.toggleserverapi.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class ConstraintValueUpdateDTO {
    private String contextName;
    private String operator;
    private List<String> values;

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}