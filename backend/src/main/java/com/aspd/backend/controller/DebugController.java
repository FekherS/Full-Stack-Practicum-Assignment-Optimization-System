package com.aspd.backend.controller;

import com.aspd.backend.model.StudentConfig;
import com.aspd.backend.repository.SchoolRepository;
import com.aspd.backend.repository.StudentConfigRepository;
import com.aspd.backend.repository.TeacherRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private final StudentConfigRepository studentConfigRepository;
    private final TeacherRepository teacherRepository;
    private final SchoolRepository schoolRepository;

    public DebugController(StudentConfigRepository studentConfigRepository,
                           TeacherRepository teacherRepository,
                           SchoolRepository schoolRepository) {
        this.studentConfigRepository = studentConfigRepository;
        this.teacherRepository = teacherRepository;
        this.schoolRepository = schoolRepository;
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @GetMapping("/data-summary")
    public ResponseEntity<Map<String, Object>> getDataSummary(@RequestParam String schoolYear) {
        Map<String, Object> summary = new HashMap<>();

        // Check student configs
        List<StudentConfig> configs = studentConfigRepository.findByYear(schoolYear);
        summary.put("studentConfigsForYear", configs.size());
        summary.put("availableYears", studentConfigRepository.findAll().stream()
                .map(StudentConfig::getYear)
                .distinct()
                .collect(Collectors.toList()));

        // Check students
        summary.put("totalStudents", studentConfigRepository.count());

        // Check teachers
        summary.put("totalTeachers", teacherRepository.count());

        // Check schools
        summary.put("totalSchools", schoolRepository.count());

        // Sample student configs
        List<StudentConfig> allConfigs = studentConfigRepository.findAll();
        summary.put("sampleConfigs", allConfigs.stream()
                .limit(5)
                .map(c -> Map.of(
                        "id", c.getId(),
                        "year", c.getYear(),
                        "studentName", c.getStudent().getFirstName() + " " + c.getStudent().getLastName(),
                        "pdpI", c.isPdpI(),
                        "pdpII", c.isPdpII(),
                        "zsp", c.isZsp(),
                        "sfp", c.isSfp()
                ))
                .collect(Collectors.toList()));

        return ResponseEntity.ok(summary);
    }
}
