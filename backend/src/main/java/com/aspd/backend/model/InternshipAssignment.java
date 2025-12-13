package com.aspd.backend.model;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "internship_assignments")
@PlanningEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// this is what we need to optimize
public class InternshipAssignment {

    // This is our input : what we need for assignment

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_config_id", nullable = false)
    private StudentConfig studentConfig; // Which student, which year, which praktikum type

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PraktikumType praktikumType; // Which type (PDP_I, PDP_II, ..)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Course courseToTeach; // Which subject the student want to teach

    // This is what should be assigned

    @PlanningVariable(
            valueRangeProviderRefs = "teacherRange",
            nullable = true
    )
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_teacher_id")
    private Teacher assignedTeacher; // optaplanner will pick this

    @PlanningVariable(
            valueRangeProviderRefs = "schoolRange",
            nullable = true
    )
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_school_id")
    private School assignedSchool; // optaplanner will pick this

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private AssignmentStatus status = AssignmentStatus.PROPOSED;
}

