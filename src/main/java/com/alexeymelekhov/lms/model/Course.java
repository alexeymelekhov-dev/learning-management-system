package com.alexeymelekhov.lms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 150)
    private String name;

    @NotBlank
    @Size(min = 10, max = 2000)
    private String description;

    @ManyToMany
    @JoinTable(
            name = "course_groups",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<Group> groups = new HashSet<>();

    public Course(String name, String description) {
        this.name = name;
        this.description = description;
        this.groups = new HashSet<>();
    }

    public Course(String name, String description, Set<Group> groups) {
        this.name = name;
        this.description = description;
        this.groups = groups;
    }
}
