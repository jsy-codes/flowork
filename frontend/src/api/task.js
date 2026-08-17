import api from './axios';

export const createTask = (data) => api.post('/tasks', data);
export const updateTaskStatus = (taskId, status) =>
    api.patch(`/tasks/${taskId}/status`, { status });
export const getRoomTasks = (roomId) => api.get(`/rooms/${roomId}/tasks`);