import api from './axios';

export const getContributions = (roomId) => api.get(`/rooms/${roomId}/contributions`);
export const getRoomHealth = (roomId) => api.get(`/rooms/${roomId}/health`);
export const exportCsv = (roomId) =>
    api.get(`/rooms/${roomId}/contributions/export`, { responseType: 'blob' });