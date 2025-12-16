package com.aspd.backend.service;

import com.aspd.backend.common.util.SchoolCurriculum;
import com.aspd.backend.common.exception.NotFoundException;
import com.aspd.backend.dto.InternshipDemandDto;
import com.aspd.backend.dto.StudentConfigDto;
import com.aspd.backend.model.Course;
import com.aspd.backend.model.SchoolType;
import com.aspd.backend.model.Student;
import com.aspd.backend.model.StudentConfig;
import com.aspd.backend.repository.StudentConfigRepository;
import com.aspd.backend.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentConfigService {

    private final StudentConfigRepository configRepository;
    private final StudentRepository studentRepository;

    // ===================== CRUD =====================

    public StudentConfigDto createConfig(StudentConfigDto dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new NotFoundException("Student", dto.getStudentId()));

        validateCourses(
                student.getSchoolType(),
                student.getMainCourse(),
                student.getPrefCourse1(),
                student.getPrefCourse2(),
                student.getPrefCourse3()
        );

        StudentConfig config = new StudentConfig(
                student,
                dto.getYear(),
                dto.isPdpI(),
                dto.isPdpII(),
                dto.isZsp(),
                dto.isSfp()
        );

        configRepository.save(config);
        return toDto(config);
    }

    public StudentConfigDto getConfig(int studentId, String year) {
        StudentConfig config = configRepository
                .findByStudent_MatriculationNbrAndYear(studentId, year)
                .orElseThrow(() -> new NotFoundException("StudentConfig", studentId, "Year: " + year));

        return toDto(config);
    }

    public List<StudentConfigDto> getAllConfigs() {
        return configRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<StudentConfigDto> getConfigsByStudent(int studentId) {
        return configRepository.findByStudent_MatriculationNbr(studentId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<StudentConfigDto> getConfigsByYear(String year) {
        return configRepository.findByYear(year)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public StudentConfigDto updateConfig(Long id, StudentConfigDto dto) {
        StudentConfig config = configRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("StudentConfig", id));

        SchoolType schoolType = config.getStudent().getSchoolType();

        validateCourses(
                schoolType,
                dto.getMainCourse(),
                dto.getPrefCourse1(),
                dto.getPrefCourse2(),
                dto.getPrefCourse3()
        );

        config.setYear(dto.getYear());
        config.setMainCourse(dto.getMainCourse());
        config.setPrefCourse1(dto.getPrefCourse1());
        config.setPrefCourse2(dto.getPrefCourse2());
        config.setPrefCourse3(dto.getPrefCourse3());
        config.setPdpI(dto.isPdpI());
        config.setPdpII(dto.isPdpII());
        config.setZsp(dto.isZsp());
        config.setSfp(dto.isSfp());

        configRepository.save(config);
        return toDto(config);
    }

    public void deleteConfig(Long id) {
        if (!configRepository.existsById(id)) {
            throw new NotFoundException("StudentConfig", id);
        }
        configRepository.deleteById(id);
    }

    public List<String> getAllYears() {
        return configRepository.findDistinctYears();
    }

    // ===================== Validation =====================

    private void validateCourses(
            SchoolType schoolType,
            Course main,
            Course pref1,
            Course pref2,
            Course pref3
    ) {
        validateCourse(schoolType, main, "mainCourse");
        validateCourse(schoolType, pref1, "prefCourse1");
        validateCourse(schoolType, pref2, "prefCourse2");
        validateCourse(schoolType, pref3, "prefCourse3");
    }

    private void validateCourse(SchoolType schoolType, Course course, String fieldName) {
        if (course == null) return;
        if (!SchoolCurriculum.isAllowed(schoolType, course)) {
            throw new IllegalArgumentException(
                    "Course " + course + " is not allowed for school type " +
                            schoolType + " (" + fieldName + ")"
            );
        }
    }

    private StudentConfigDto toDto(StudentConfig config) {
        return new StudentConfigDto(
                config.getId(),
                config.getStudent().getMatriculationNbr(),
                config.getYear(),
                config.getMainCourse(),
                config.getPrefCourse1(),
                config.getPrefCourse2(),
                config.getPrefCourse3(),
                config.getSchoolType(),
                config.isPdpI(),
                config.isPdpII(),
                config.isZsp(),
                config.isSfp()
        );
    }

    // ===================== Internship Demand Tally =====================

    public Map<SchoolType, InternshipDemandDto> tallyInternshipDemand(String year) {
        List<StudentConfig> configs = configRepository.findByYear(year);
        Map<SchoolType, InternshipDemandDto> tally = new EnumMap<>(SchoolType.class);

        // initialize DTO for each school type
        for (SchoolType type : SchoolType.values()) {
            tally.put(type, new InternshipDemandDto(type));
        }

        for (StudentConfig config : configs) {
            SchoolType type = config.getSchoolType();
            InternshipDemandDto dto = tally.get(type);

            // PDP I/II (student-based)
            if (config.isPdpI()) dto.addPdpI(1);
            if (config.isPdpII()) dto.addPdpII(1);

            // SFP (course-based, main course only)
            if (config.isSfp() && config.getMainCourse() != null) {
                dto.addSfp(config.getMainCourse(), 1);
            }

            // ZSP (fractional by courses)
            if (config.isZsp()) {
                dto.addZsp(config.getMainCourse(), 0.5);
                dto.addZsp(config.getPrefCourse1(), 0.25);
                dto.addZsp(config.getPrefCourse2(), 0.15);
                dto.addZsp(config.getPrefCourse3(), 0.10);
            }
        }

        return tally;
    }
}
