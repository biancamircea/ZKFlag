package ro.mta.toggleserverapi.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ApiTokens")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApiToken {
    @Id
    @SequenceGenerator(
            name = "api_token_sequence",
            sequenceName = "api_token_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "api_token_sequence"
    )
    private Long id;

    @NotBlank
    @NotNull
    private String secret;

    @NotBlank
    @NotNull
    private String name;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @NotNull
    private Project project;

    @ManyToOne
    @JoinColumn(name = "environment_id")
    @NotNull
    private Environment environment;

    @ManyToOne
    @JoinColumn(name = "instance_id")
    @NotNull
    private Instance instance;

    @NotNull
    private Long type;
}
