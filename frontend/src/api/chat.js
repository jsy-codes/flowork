import api from './axios';

export const getRooms = () => api.get('/rooms');
export const createRoom = (data) => api.post('/rooms', data);
export const joinRoom = (roomId) => api.post(`/rooms/${roomId}/join`);
export const getMembers = (roomId) => api.get(`/rooms/${roomId}/members`);
export const getMessages = (roomId, cursor, size = 20) =>
    api.get(`/rooms/${roomId}/messages`, { params: { cursor, size } });