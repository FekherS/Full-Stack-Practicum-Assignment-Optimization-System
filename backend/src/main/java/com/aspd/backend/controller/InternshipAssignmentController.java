package com.aspd.backend.controller;

import com.aspd.backend.dto.AssignmentDto;
import com.aspd.backend.dto.OptimizationResult;
import com.aspd.backend.model.*;
import com.aspd.backend.service.InternshipOptimizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/internships")
public class InternshipAssignmentController {

    private final InternshipOptimizationService optimizationService;

    public InternshipAssignmentController(InternshipOptimizationService optimizationService) {
        this.optimizationService = optimizationService;
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @PostMapping("/optimize")
    public ResponseEntity<OptimizationResult> optimizeAssignments(@RequestParam(name = "schoolYear") String schoolYear) {
        log.info("Received optimization request for year: {}", schoolYear);

        List<InternshipAssignment> assignments = optimizationService.generateAssignments(schoolYear);

        OptimizationResult result = new OptimizationResult();
        result.setSchoolYear(schoolYear);
        result.setTotalAssignments(assignments.size());
        result.setAssignedCount((int) assignments.stream()
                .filter(a -> a.getAssignedTeacher() != null)
                .count());
        result.setUnassignedCount((int) assignments.stream()
                .filter(a -> a.getAssignedTeacher() == null)
                .count());
        result.setAssignments(assignments.stream()
                .map(this::toDto)
                .collect(Collectors.toList()));

        // Check if solution is feasible
        if (result.getUnassignedCount() > 0) {
            log.warn("Optimization found {} unassigned students - insufficient teachers!",
                    result.getUnassignedCount());
        }

        return ResponseEntity.ok(result);
    }

    private AssignmentDto toDto(InternshipAssignment assignment) {
        AssignmentDto dto = new AssignmentDto();
        dto.setId(assignment.getId());
        dto.setStudentName(assignment.getStudentConfig().getStudent().getFirstName() + " " +
                assignment.getStudentConfig().getStudent().getLastName());
        dto.setPraktikumType(assignment.getPraktikumType().toString());
        dto.setCourse(assignment.getCourseToTeach().toString());

        if (assignment.getAssignedTeacher() != null) {
            dto.setTeacherName(assignment.getAssignedTeacher().getFirstName() + " " +
                    assignment.getAssignedTeacher().getLastName());
        }

        if (assignment.getAssignedSchool() != null) {
            dto.setSchoolName(assignment.getAssignedSchool().getName());
        }

        dto.setStatus(String.valueOf(assignment.getStatus()));

        return dto;
    }
}