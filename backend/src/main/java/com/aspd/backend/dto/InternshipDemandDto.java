package com.aspd.backend.dto;

import com.aspd.backend.model.Course;
import com.aspd.backend.model.SchoolType;

import java.util.EnumMap;
import java.util.Map;

public class InternshipDemandDto {

    private final SchoolType schoolType;

    // PDP counts (student-based, integer-ish but stored as double for consistency)
    private double pdpI;
    private double pdpII;

    // SFP/ZSP demand per course (course-based)
    private final Map<Course, Double> sfpDemand = new EnumMap<>(Course.class);
    private final Map<Course, Double> zspDemand = new EnumMap<>(Course.class);

    public InternshipDemandDto(SchoolType schoolType) {
        this.schoolType = schoolType;
    }

    // ===== Adders =====
    public void addPdpI(double val) {
        pdpI += val;
    }

    public void addPdpII(double val) {
        pdpII += val;
    }

    public void addSfp(Course course, double val) {
        if (course == null) return;
        sfpDemand.merge(course, val, Double::sum);
    }

    public void addZsp(Course course, double val) {
        if (course == null) return;
        zspDemand.merge(course, val, Double::sum);
    }

    // ===== Getters =====
    public SchoolType getSchoolType() {
        return schoolType;
    }

    public double getPdpI() {
        return pdpI;
    }

    public double getPdpII() {
        return pdpII;
    }

    public Map<Course, Double> getSfpDemand() {
        return sfpDemand;
    }

    public Map<Course, Double> getZspDemand() {
        return zspDemand;
    }

    @Override
    public String toString() {
        return "InternshipDemandDto{" +
                "schoolType=" + schoolType +
                ", pdpI=" + pdpI +
                ", pdpII=" + pdpII +
                ", sfpDemand=" + sfpDemand +
                ", zspDemand=" + zspDemand +
                '}';
    }
}
