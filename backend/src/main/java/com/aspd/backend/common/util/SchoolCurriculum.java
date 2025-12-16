package com.aspd.backend.common.util;

import com.aspd.backend.model.Course;
import com.aspd.backend.model.SchoolType;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

public final class SchoolCurriculum {

    private static final EnumMap<SchoolType, Set<Course>> ALLOWED_COURSES =
            new EnumMap<>(SchoolType.class);

    static {
        ALLOWED_COURSES.put(SchoolType.GS, EnumSet.of(
                Course.GERMAN,
                Course.GERMAN_AS_SECOND_LANGUAGE,
                Course.ENGLISH,
                Course.MATHEMATICS,
                Course.GEOGRAPHY,
                Course.HISTORY,
                Course.SOCIAL_STUDIES,
                Course.RELIGION_CATHOLIC,
                Course.ART,
                Course.MUSIC,
                Course.SPORT,
                Course.HOME_AND_GENERAL_STUDIES,
                Course.LITERACY_ACQUISITION
        ));

        ALLOWED_COURSES.put(SchoolType.MS, EnumSet.of(
                Course.GERMAN,
                Course.GERMAN_AS_SECOND_LANGUAGE,
                Course.ENGLISH,
                Course.MATHEMATICS,
                Course.GEOGRAPHY,
                Course.HISTORY,
                Course.SOCIAL_STUDIES,
                Course.RELIGION_CATHOLIC,
                Course.ART,
                Course.MUSIC,
                Course.SPORT,
                Course.INFORMATICS,
                Course.WORK_STUDIES
        ));
    }

    private SchoolCurriculum() {}

    public static boolean isAllowed(SchoolType schoolType, Course course) {
        System.out.println("school type is, ");
        System.out.println(schoolType);
        System.out.println("course is, ");
        System.out.println(course);
        System.out.println("result is, ");
        System.out.println(ALLOWED_COURSES.get(schoolType).contains(course));
        return ALLOWED_COURSES.get(schoolType).contains(course);
    }
}
