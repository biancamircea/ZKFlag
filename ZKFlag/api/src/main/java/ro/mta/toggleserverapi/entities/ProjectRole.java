//package ro.mta.toggleserverapi.entities;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import ro.mta.toggleserverapi.enums.ProjectRoleType;
//
//import java.util.List;
//
//@Entity
//@Table(name = "Project_Roles")
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//public class ProjectRole {
//    @Id
//    @SequenceGenerator(
//            name = "project_roles_sequence",
//            sequenceName = "project_roles_sequence",
//            allocationSize = 1
//    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "project_roles_sequence"
//    )
//    private Long id;
//
//    @Column(unique = true)
//    @Enumerated(EnumType.STRING)
//    private ProjectRoleType roleType;
//
//    private String description;
//
//    @OneToMany(mappedBy = "projectRole", cascade = CascadeType.ALL)
//    private List<UserProject> userProjectList;
//}
