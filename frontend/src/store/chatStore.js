import { create } from 'zustand';

const useChatStore = create((set) => ({
    rooms: [],
    currentRoom: null,
    messages: [],
    notifications: [],

    setRooms: (rooms) => set({ rooms }),
    setCurrentRoom: (room) => set({ currentRoom: room, messages: [] }),
    addMessage: (message) => set((state) => ({ messages: [...state.messages, message] })),
    setMessages: (messages) => set({ messages }),
    addNotification: (notification) =>
        set((state) => ({ notifications: [notification, ...state.notifications] })),
}));

export default useChatStore;