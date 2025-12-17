import apiClient from '../lib/axios';
import { ApiResponse, UserDetail } from '../types/api';

export const getUserInfo = async (): Promise<UserDetail> => {
    const response = await apiClient.get<ApiResponse<UserDetail>>('/users/me');
    return response.data.data;
};
