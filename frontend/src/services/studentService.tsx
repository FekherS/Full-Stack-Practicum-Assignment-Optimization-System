import apiService from './api';

export type SchoolType = "GS" | "MS";


export type Course =
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


export type PraktikumType = "PDP_I" | "PDP_II" | "ZSP" | "SFP";


export interface Address {
  street?: string;
  city?: string;
  houseNbr?: string;
  postalCode?: string;
  country?: string;
}

export interface Student {
  matriculationNbr: number;
  firstName: string;
  lastName: string;
  email: string;
  schoolType: SchoolType;
  mainCourse: Course | null;
  prefCourse1: Course | null;
  prefCourse2: Course | null;
  prefCourse3: Course | null;
  registred: boolean;
  oriented: boolean;
  address?: Address;
  addressSemester?: Address;
  phone?: string;
  birthDate?: string; // use string for ISO date
  description?: string;
}

export interface CompletedInternship {
  id: number;
  studentId: number;
  teacherId?: number;
  schoolId?: number;
  type: PraktikumType;
  course: Course;
  startDate: string; // ISO date string
  endDate: string;   // ISO date string
  description?: string;
}


class StudentService {
    async getAllStudent(): Promise<Student[]> {
    return apiService.get<Student[]>('/api/students');
  }

    async createStudent(Student: Student): Promise<Student> {
    return apiService.post<Student>('/api/students', Student);
  }

    async updateStudent(id: number, Student: Partial<Student>): Promise<Student> {
    return apiService.put<Student>(`/api/students/${id}`, Student);
  }

  async deleteStudent(id: number): Promise<void> {
    return apiService.delete<void>(`/api/students/${id}`);
  }
}

export default new StudentService();