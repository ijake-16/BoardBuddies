import apiClient from '../lib/axios';
import { ApiResponse, CrewDetail, CrewMember, CrewApplicant, ReservationDetail } from '../types/api';

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
