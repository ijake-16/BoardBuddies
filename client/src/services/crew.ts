import apiClient from '../lib/axios';
import { ApiResponse, CrewDetail, CrewMember } from '../types/api';

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
