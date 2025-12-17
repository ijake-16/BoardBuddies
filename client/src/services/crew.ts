import apiClient from '../lib/axios';
import { ApiResponse, CrewDetail } from '../types/api';

export const getCrewInfo = async (crewId: number): Promise<CrewDetail> => {
    const response = await apiClient.get<ApiResponse<CrewDetail>>(`/crews/${crewId}`);
    return response.data.data;
};
