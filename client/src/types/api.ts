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
    applicationId: number;
    userId: number;
    userName: string;
    studentId: string;
    profileImageUrl?: string | null;
    status: string; // "PENDING", etc.
    created_at?: string; // Optional since it was missing in the log, but likely exists
}


export interface ReservationDetail {
    date: string;
    status: string;
    booked: number;
    waitingCount: number;
    capacity: number;
    member_list: {
        user_id: number;
        name: string;
        profile_image_url: string | null;
        teaching: boolean;
        role: string;
        phoneNumber?: string;
        registered_by_name?: string;
    }[];
    waiting_member_list: any[];
    my_reservation: any | null;
}

export interface MyReservation {
    date: string; // "YYYY-MM-DD"
    status: string; // "confirmed"
    reservation_id: number;
    crew_id?: number;
    created_at?: string;
    teaching: boolean;
    waiting_order?: number | null;
}

export interface MyCalendarResponse {
    my_reservations: MyReservation[];
    usage_count: number;
}

export interface CrewCalendarResponse {
    calendar: {
        date: string;
        occupancy_status: 'LOW' | 'MEDIUM' | 'HIGH';
    }[];
    my_reservations: {
        date: string;
        status: string;
        waiting_order: number | null;
    }[] | null;
}

export interface ReservationResponse {
    reservationId: number;
    status: string;
}

export interface CrewUpdateRequest {
    crewName: string;
    manager_list: number[]; // Array of user_ids
    crewPIN: number;
    reservation_day: string;
    reservation_time: string;
    dailyCapacity: number;
    isCapacityLimited: boolean;
}

export interface CrewUsageStatistic {
    user_id: number;
    name: string;
    usage_count: number;
    profile_image_url?: string | null; // Optional
}
