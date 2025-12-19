import apiClient from '../lib/axios';
import { ApiResponse, CrewDetail, CrewMember, CrewApplicant, ReservationDetail, ReservationResponse, CrewCalendarResponse, CrewUpdateRequest, CrewUsageStatistic, MyCalendarResponse } from '../types/api';

export const getCrewInfo = async (crewId: number): Promise<CrewDetail> => {
    const response = await apiClient.get<ApiResponse<CrewDetail>>(`/crews/${crewId}`);
    return response.data.data;
};

export const getCrewMembers = async (crewId: number): Promise<CrewMember[]> => {
    const response = await apiClient.get<ApiResponse<CrewMember[]>>(`/crews/${crewId}/members`);
    return response.data.data;
};

export const getCrewManagers = async (crewId: number): Promise<CrewMember[]> => {
    const response = await apiClient.get<ApiResponse<CrewMember[]>>(`/crews/${crewId}/managers`);
    return response.data.data;
};

export const getApplicants = async (crewId: number): Promise<CrewApplicant[]> => {
    const response = await apiClient.get<ApiResponse<CrewApplicant[]>>(`/crews/${crewId}/applications`);
    return response.data.data;
};

export const manageApplicant = async (crewId: number, applicationId: number, decision: number): Promise<void> => {
    // decision: 1 for approve, 0 for reject
    await apiClient.post(`/crews/${crewId}/applications/${applicationId}/approve`, { decision });
};

export const applyToCrew = async (crewId: number, crewPIN: string): Promise<void> => {
    await apiClient.post(`/crews/${crewId}/applications`, { crewPIN });
};

export const getReservationDetail = async (crewId: number, date: string): Promise<ReservationDetail> => {
    const response = await apiClient.get<ApiResponse<ReservationDetail>>(`/crews/${crewId}/reservations/detail?date=${date}`);
    return response.data.data;
};

export const createReservation = async (crewId: number, dates: string[], guestInfo?: { name: string, phoneNumber: string }): Promise<ReservationResponse> => {
    const response = await apiClient.post<ApiResponse<ReservationResponse>>(`/crews/${crewId}/reservations`, { dates, guestInfo });
    return response.data.data;
};

export const cancelReservation = async (crewId: number, dates: string[]): Promise<null> => {
    const response = await apiClient.delete<ApiResponse<null>>(`/crews/${crewId}/reservations`, {
        data: { dates }
    });
    return response.data.data;
};

export const getCrewCalendar = async (crewId: number, date: string, showMySchedule: boolean = false): Promise<CrewCalendarResponse> => {
    const response = await apiClient.get<ApiResponse<CrewCalendarResponse>>(`/crews/${crewId}/calendar?date=${date}&showMySchedule=${showMySchedule}`);
    return response.data.data;
};

export const updateCrew = async (crewId: number, data: CrewUpdateRequest): Promise<void> => {
    await apiClient.patch(`/crews/${crewId}`, data);
};

export const promoteMember = async (crewId: number, userId: number): Promise<void> => {
    await apiClient.post(`/crews/${crewId}/managers/${userId}`);
};

export const demoteManager = async (crewId: number, userId: number): Promise<void> => {
    await apiClient.delete(`/crews/${crewId}/managers/${userId}`);
};


export const getCrewUsageStatistics = async (
    crewId: number,
    sortBy: 'name' | 'usageCount' = 'name',
    sortOrder: 'asc' | 'desc' = 'asc',
    search: string = ''
): Promise<CrewUsageStatistic[]> => {
    const response = await apiClient.get<ApiResponse<CrewUsageStatistic[]>>(`/crews/${crewId}/usage-statistics`, {
        params: {
            sortBy,
            sortOrder,
            search
        }
    });
    return response.data.data;
};

export const applyForTeaching = async (crewId: number, reservationId: number): Promise<void> => {
    await apiClient.post(`/crews/${crewId}/reservations/${reservationId}/teaching`);
};

export const withdrawFromTeaching = async (crewId: number, reservationId: number): Promise<void> => {
    await apiClient.delete(`/crews/${crewId}/reservations/${reservationId}/teaching`);
};

export const getMyCrewCalendar = async (crewId: number, date: string): Promise<MyCalendarResponse> => {
    const response = await apiClient.get<ApiResponse<MyCalendarResponse>>(`/crews/${crewId}/calendar/my?date=${date}`);
    return response.data.data;
};

