/* eslint-disable @typescript-eslint/no-explicit-any */
import { useMemo, useState } from "react";
import StudentConfigService from "../../services/studentConfigService";
import "../../styles/InternshipsAssignment/InternshipAssignmentStudentForm.css";
import type { StudentConfigDto } from "../../services/studentConfigService";

/* ===== frontend equivalent of SchoolCurriculum ===== */

type SchoolType = "GS" | "MS";
type Course =
    | "GERMAN"
    | "GERMAN_AS_SECOND_LANGUAGE"
    | "ENGLISH"
    | "MATHEMATICS"
    | "GEOGRAPHY"
    | "HISTORY"
    | "SOCIAL_STUDIES"
    | "RELIGION_CATHOLIC"
    | "ART"
    | "MUSIC"
    | "SPORT"
    | "INFORMATICS"
    | "HOME_AND_GENERAL_STUDIES"
    | "LITERACY_ACQUISITION"
    | "WORK_STUDIES";

const ALLOWED_COURSES: Record<SchoolType, Course[]> = {
    GS: [
        "GERMAN",
        "GERMAN_AS_SECOND_LANGUAGE",
        "ENGLISH",
        "MATHEMATICS",
        "GEOGRAPHY",
        "HISTORY",
        "SOCIAL_STUDIES",
        "RELIGION_CATHOLIC",
        "ART",
        "MUSIC",
        "SPORT",
        "HOME_AND_GENERAL_STUDIES",
        "LITERACY_ACQUISITION",
    ],
    MS: [
        "GERMAN",
        "GERMAN_AS_SECOND_LANGUAGE",
        "ENGLISH",
        "MATHEMATICS",
        "GEOGRAPHY",
        "HISTORY",
        "SOCIAL_STUDIES",
        "RELIGION_CATHOLIC",
        "ART",
        "MUSIC",
        "SPORT",
        "INFORMATICS",
        "WORK_STUDIES",
    ],
};

interface Props {
    config?: StudentConfigDto;
    year: string;
    onClose: () => void;
    onSave: (savedConfig: StudentConfigDto) => void;
}

export default function StudentConfigForm({ config, year, onClose, onSave }: Props) {
    const [form, setForm] = useState<StudentConfigDto>({
        id: config?.id ?? 0,
        studentId: config?.studentId ?? 0,
        schoolType: config?.schoolType ?? "GS",
        pdpI: config?.pdpI ?? false,
        pdpII: config?.pdpII ?? false,
        zsp: config?.zsp ?? false,
        sfp: config?.sfp ?? false,
        mainCourse: config?.mainCourse ?? null,
        prefCourse1: config?.prefCourse1 ?? null,
        prefCourse2: config?.prefCourse2 ?? null,
        prefCourse3: config?.prefCourse3 ?? null,
        year: config?.year ?? year,
    });

    const allowedCourses = useMemo(
        () => ALLOWED_COURSES[form.schoolType as SchoolType],
        [form.schoolType]
    );

    const handleChange = <K extends keyof StudentConfigDto>(
        key: K,
        value: StudentConfigDto[K]
    ) => {
        setForm(prev => ({ ...prev, [key]: value }));
    };

    const handleSubmit = async () => {
        try {
            const saved = config?.id
                ? await StudentConfigService.updateConfig(form.id!, form)
                : await StudentConfigService.createConfig(form);

            onSave(saved);
        } catch (err) {
            console.error("Config save failed", err);
        }
    };

    const renderCourseSelect = (
        key: keyof StudentConfigDto,
        label: string
    ) => (
        <div key={key} className="student-config-field">
            <label>{label}</label>
            <select
                className="student-config-input"
                value={(form[key] as Course | null) ?? ""}
                onChange={e =>
                    handleChange(
                        key,
                        e.target.value === ""
                            ? null
                            : (e.target.value as any)
                    )
                }
            >
                <option value="">-- Select --</option>
                {allowedCourses.map(course => (
                    <option key={course} value={course}>
                        {course}
                    </option>
                ))}
            </select>
        </div>
    );

    return (
        <div className="student-config-form-container">
            <h3 className="student-config-form-title">
                {config?.id ? "Edit Configuration" : "Create Configuration"}
            </h3>

            <div className="student-config-split-layout">
                {/* Left Side */}
                <div className="student-config-left-section">
                    <div className="student-config-field">
                        <label>Student ID</label>
                        <input
                            className="student-config-input"
                            type="number"
                            value={form.studentId}
                            onChange={e =>
                                handleChange("studentId", Number(e.target.value))
                            }
                        />
                    </div>

                    <div className="student-config-field">
                        <label>Year</label>
                        <input
                            className="student-config-input"
                            type="text"
                            value={form.year}
                            onChange={e => handleChange("year", e.target.value)}
                            placeholder="e.g., 2024/2025"
                        />
                    </div>

                    {config && (
                        <div className="student-config-field">
                            <label>School Type</label>
                            <select
                                className="student-config-input"
                                value={form.schoolType}
                                onChange={e => {
                                    const newType = e.target.value as SchoolType;
                                    setForm(prev => ({
                                        ...prev,
                                        schoolType: newType,
                                        mainCourse: null,
                                        prefCourse1: null,
                                        prefCourse2: null,
                                        prefCourse3: null,
                                    }));
                                }}
                            >
                                <option value="GS">GS</option>
                                <option value="MS">MS</option>
                            </select>
                        </div>
                    )}

                    <div className="student-config-field" style={{ marginTop: "20px" }}>
                        <label style={{ marginBottom: "12px", display: "block" }}>
                            Internship Types
                        </label>
                        <div className="checkbox-group">
                            {["pdpI", "pdpII", "zsp", "sfp"].map(field => (
                                <div key={field} className="checkbox-label">
                                    <input
                                        type="checkbox"
                                        checked={form[field as keyof StudentConfigDto] as boolean}
                                        onChange={e =>
                                            handleChange(
                                                field as keyof StudentConfigDto,
                                                e.target.checked as any
                                            )
                                        }
                                    />
                                    <span>{field.toUpperCase()}</span>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Right Side */}
                {config && (
                    <div className="student-config-right-section">
                        {renderCourseSelect("mainCourse", "Main Course")}
                        {renderCourseSelect("prefCourse1", "Preferred Course 1")}
                        {renderCourseSelect("prefCourse2", "Preferred Course 2")}
                        {renderCourseSelect("prefCourse3", "Preferred Course 3")}
                    </div>
                )}
            </div>

            <div className="student-config-form-actions">
                <button className="student-config-btn" onClick={handleSubmit}>
                    {config ? "Update" : "Create"}
                </button>
                <button
                    className="student-config-btn student-config-btn-cancel"
                    onClick={onClose}
                >
                    Cancel
                </button>
            </div>
        </div>
    );
}
