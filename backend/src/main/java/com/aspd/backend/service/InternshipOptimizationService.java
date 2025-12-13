package com.aspd.backend.service;

import com.aspd.backend.model.*;
import com.aspd.backend.repository.*;
import com.aspd.backend.solver.InternshipSolution;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class InternshipOptimizationService {

    private final SolverManager<InternshipSolution, Long> solverManager;
    private final StudentConfigRepository studentConfigRepository;
    private final TeacherRepository teacherRepository;
    private final SchoolRepository schoolRepository;
    private final InternshipAssignmentRepository assignmentRepository;

    public InternshipOptimizationService(
            SolverManager<InternshipSolution, Long> solverManager,
            StudentConfigRepository studentConfigRepository,
            TeacherRepository teacherRepository,
            SchoolRepository schoolRepository,
            InternshipAssignmentRepository assignmentRepository) {
        this.solverManager = solverManager;
        this.studentConfigRepository = studentConfigRepository;
        this.teacherRepository = teacherRepository;
        this.schoolRepository = schoolRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Transactional
    public List<InternshipAssignment> generateAssignments(String schoolYear) {
        log.info("Starting optimization for school year: {}", schoolYear);

        List<StudentConfig> studentConfigs = studentConfigRepository.findByYear(schoolYear);
        List<InternshipAssignment> assignments = new ArrayList<>();
        for (StudentConfig config : studentConfigs) {
            if (config.isPdpI()) {
                assignments.add(createAssignment(config, PraktikumType.PDP_I));
            }
            if (config.isPdpII()) {
                assignments.add(createAssignment(config, PraktikumType.PDP_II));
            }
            if (config.isZsp()) {
                assignments.add(createAssignment(config, PraktikumType.ZSP));
            }
            if (config.isSfp()) {
                assignments.add(createAssignment(config, PraktikumType.SFP));
            }
        }
        List<Teacher> teachers = teacherRepository.findAll();
        List<School> schools = schoolRepository.findAll();

        // Creation of the problem the problem
        InternshipSolution problem = new InternshipSolution();
        problem.setSchoolYear(schoolYear);
        problem.setAvailableTeachers(teachers);
        problem.setAvailableSchools(schools);
        problem.setAssignments(assignments);

        // Solve with OptaPlanner!
        try {
            SolverJob<InternshipSolution, Long> solverJob =
                    solverManager.solve(System.currentTimeMillis(), problem);
            InternshipSolution solution = solverJob.getFinalBestSolution();

            log.info("Optimization complete! Score: {}", solution.getScore());
            if (solution.getScore() != null) {
                log.info("  Hard score: {}", solution.getScore().hardScore());
                log.info("  Soft score: {}", solution.getScore().softScore());
            }

            // Save the assignments
            List<InternshipAssignment> optimizedAssignments = solution.getAssignments();
            assignmentRepository.saveAll(optimizedAssignments);

            return optimizedAssignments;

        } catch (ExecutionException | InterruptedException e) {
            log.error("Optimization failed", e);
            throw new RuntimeException("Failed to optimize assignments", e);
        }
    }

    private InternshipAssignment createAssignment(StudentConfig config, PraktikumType type) {
        InternshipAssignment assignment = new InternshipAssignment();
        assignment.setStudentConfig(config);
        assignment.setPraktikumType(type);
        // Determine which course to teach based on praktikum type
        assignment.setCourseToTeach(determineCourse(config));
        // Teacher and school will be assigned by OptaPlanner
        assignment.setAssignedTeacher(null);
        assignment.setAssignedSchool(null);
        return assignment;
    }

    private Course determineCourse(StudentConfig config) {
        // I will keep it simpler by getting the main course for now
        return config.getMainCourse();
    }
}