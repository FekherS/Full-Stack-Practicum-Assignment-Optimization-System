package com.aspd.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class OptimizationResult {
    private String schoolYear;
    private int totalAssignments;
    private int assignedCount;
    private int unassignedCount;
    private List<AssignmentDto> assignments;
}

