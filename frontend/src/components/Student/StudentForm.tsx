import React, { useState, useMemo } from "react";
import type { Student, Address, SchoolType, Course } from "../../services/studentService";
import StudentService from "../../services/studentService";
import "../../styles/StudentStyles/StudentForm.css";

interface Props {
    student?: Student | null;
    onClose: () => void;
    onSave: (student: Student) => void;
}

const emptyAddress: Address = {
    street: "",
    city: "",
    houseNbr: "",
    postalCode: "",
    country: "",
};

/* ===== frontend equivalent of SchoolCurriculum ===== */

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

const StudentForm: React.FC<Props> = ({ student, onClose, onSave }) => {
    const [form, setForm] = useState<Student>({
        matriculationNbr: student?.matriculationNbr || 0,
        firstName: student?.firstName || "",
        lastName: student?.lastName || "",
        email: student?.email || "",
        schoolType: student?.schoolType || "GS",
        mainCourse: student?.mainCourse ?? null,
        prefCourse1: student?.prefCourse1 ?? null,
        prefCourse2: student?.prefCourse2 ?? null,
        prefCourse3: student?.prefCourse3 ?? null,
        registred: student?.registred || false,
        oriented: student?.oriented || false,
        address: student?.address || { ...emptyAddress },
        addressSemester: student?.addressSemester || { ...emptyAddress },
        phone: student?.phone || "",
        birthDate: student?.birthDate || "",
        description: student?.description || "",
    });

    const allowedCourses = useMemo(
        () => ALLOWED_COURSES[form.schoolType],
        [form.schoolType]
    );

    const handleChange = (key: keyof Student, value: any) => {
        setForm(prev => ({ ...prev, [key]: value }));
    };

    const handleAddressChange = (
        key: keyof Address,
        value: string,
        type: "address" | "addressSemester"
    ) => {
        setForm(prev => ({
            ...prev,
            [type]: {
                ...prev[type],
                [key]: value,
            },
        }));
    };

    const handleSubmit = async () => {
        try {
            const saved = student
                ? await StudentService.updateStudent(student.matriculationNbr, form)
                : await StudentService.createStudent(form);

            onSave(saved);
        } catch (err) {
            console.error(err);
        }
    };

    const renderCourseOptions = () => (
        <>
            <option value="">-- Select --</option>
            {allowedCourses.map(course => (
                <option key={course} value={course}>
                    {course}
                </option>
            ))}
        </>
    );

    const renderCourseSelect = (
        id: string,
        label: string,
        value: Course | null,
        keyName: keyof Student
    ) => (
        <div className="student-field">
            <label htmlFor={id}>{label}</label>
            <select
                id={id}
                className="student-input"
                value={value ?? ""}
                onChange={e =>
                    handleChange(
                        keyName,
                        e.target.value === "" ? null : (e.target.value as Course)
                    )
                }
            >
                {renderCourseOptions()}
            </select>
        </div>
    );

    return (
        <div className="student-form-container">
            <h3 className="student-form-title">
                {student ? "Update Student" : "Create Student"}
            </h3>

            <div className="student-field">
                <label htmlFor="matriculationNbr">Matriculation Number</label>
                <input
                    id="matriculationNbr"
                    className="student-input"
                    type="number"
                    value={form.matriculationNbr}
                    onChange={e =>
                        handleChange("matriculationNbr", Number(e.target.value))
                    }
                />
            </div>

            <div className="student-field">
                <label htmlFor="firstName">First Name</label>
                <input
                    id="firstName"
                    className="student-input"
                    value={form.firstName}
                    onChange={e => handleChange("firstName", e.target.value)}
                />
            </div>

            <div className="student-field">
                <label htmlFor="lastName">Last Name</label>
                <input
                    id="lastName"
                    className="student-input"
                    value={form.lastName}
                    onChange={e => handleChange("lastName", e.target.value)}
                />
            </div>

            <div className="student-field">
                <label htmlFor="email">Email</label>
                <input
                    id="email"
                    className="student-input"
                    value={form.email}
                    onChange={e => handleChange("email", e.target.value)}
                />
            </div>

            <div className="student-field">
                <label htmlFor="schoolType">School Type</label>
                <select
                    id="schoolType"
                    className="student-input"
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

            {renderCourseSelect("mainCourse", "Main Course", form.mainCourse, "mainCourse")}
            {renderCourseSelect("prefCourse1", "Preferred Course 1", form.prefCourse1, "prefCourse1")}
            {renderCourseSelect("prefCourse2", "Preferred Course 2", form.prefCourse2, "prefCourse2")}
            {renderCourseSelect("prefCourse3", "Preferred Course 3", form.prefCourse3, "prefCourse3")}

            <div className="student-checkbox-row">
                <input
                    id="registred"
                    type="checkbox"
                    checked={form.registred}
                    onChange={e => handleChange("registred", e.target.checked)}
                />
                <label htmlFor="registred">Registered</label>
            </div>

            <div className="student-checkbox-row">
                <input
                    id="oriented"
                    type="checkbox"
                    checked={form.oriented}
                    onChange={e => handleChange("oriented", e.target.checked)}
                />
                <label htmlFor="oriented">Oriented</label>
            </div>

            <h4 className="student-address-title">Address</h4>
            {["street", "city", "houseNbr", "postalCode", "country"].map(key => (
                <div className="student-field full-width" key={key}>
                    <label htmlFor={`address-${key}`}>
                        {key.charAt(0).toUpperCase() + key.slice(1)}
                    </label>
                    <input
                        id={`address-${key}`}
                        className="student-input"
                        value={(form.address ?? emptyAddress)[key as keyof Address]}
                        onChange={e =>
                            handleAddressChange(
                                key as keyof Address,
                                e.target.value,
                                "address"
                            )
                        }
                    />
                </div>
            ))}

            <h4 className="student-address-title">Semester Address</h4>
            {["street", "city", "houseNbr", "postalCode", "country"].map(key => (
                <div className="student-field full-width" key={key}>
                    <label htmlFor={`semester-${key}`}>
                        {key.charAt(0).toUpperCase() + key.slice(1)}
                    </label>
                    <input
                        id={`semester-${key}`}
                        className="student-input"
                        value={(form.addressSemester ?? emptyAddress)[key as keyof Address]}
                        onChange={e =>
                            handleAddressChange(
                                key as keyof Address,
                                e.target.value,
                                "addressSemester"
                            )
                        }
                    />
                </div>
            ))}

            <div className="student-form-actions">
                <button className="student-btn" onClick={handleSubmit}>
                    {student ? "Update" : "Create"}
                </button>
                <button
                    className="student-btn student-btn-cancel"
                    onClick={onClose}
                >
                    Cancel
                </button>
            </div>
        </div>
    );
};

export default StudentForm;
