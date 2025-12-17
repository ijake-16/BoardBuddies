export interface ApiResponse<T> {
    code: number;
    message: string;
    data: T;
}

export interface CrewDetail {
    crew_id: number;
    name: string;
    univ: string;
    reservation_day: string; // "FRIDAY"
    reservation_time: string; // "18:00"
    dailyCapacity: number;
    status: string; // "ACTIVE"
    created_at: string;
    updated_at: string;
    president_name: string;
    member_count: number;
    profile_image_url: string | null;
    isCapacityLimited: boolean;
}

export interface CrewSimple {
    crewId: number;
    crewName: string;
}

export interface UserDetail {
    userId: number;
    name: string;
    email: string;
    role: string;
    birthDate: string;
    school: string;
    studentId: string;
    gender: string;
    phoneNumber: string;
    profileImageUrl: string;
    socialId: string;
    socialProvider: string;
    isRegistered: boolean;
    createdAt: string;
    updatedAt: string;
    crew: CrewSimple;
}

export interface CrewMember {
    user_id: number;
    name: string;
    student_id: string;
    role: string; // "MEMBER", etc.
}
export interface CrewApplicant {
    id: number; // application_id
    user: {
        userId: number;
        name: string;
        studentId: string;
        profileImageUrl: string | null;
    }
    status: string; // "PENDING", etc.
    created_at: string;
}
