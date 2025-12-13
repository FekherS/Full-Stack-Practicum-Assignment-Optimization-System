package com.aspd.backend.solver;

import com.aspd.backend.model.*;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import lombok.*;

import java.util.List;

@PlanningSolution
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// this is the entire problem : we have all the teachers, all the schools and the list of Internship assignment students,..)
public class InternshipSolution {

    // AVAILABLE RESOURCES (Facts)
    @ValueRangeProvider(id = "teacherRange")
    @ProblemFactCollectionProperty
    private List<Teacher> availableTeachers;

    @ValueRangeProvider(id = "schoolRange")
    @ProblemFactCollectionProperty
    private List<School> availableSchools;

    // ASSIGNMENTS TO OPTIMIZE
    @PlanningEntityCollectionProperty
    private List<InternshipAssignment> assignments;

    // SCORE (OptaPlanner calculates this)
    @PlanningScore
    private HardSoftScore score;

    private String schoolYear;
}