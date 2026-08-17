import { useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import useChatStore from '../store/chatStore';

const useWebSocket = (roomId, userId) => {
    const clientRef = useRef(null);
    const { addMessage, addNotification } = useChatStore();

    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (!token || !roomId) return;

        const client = new Client({
            webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
            connectHeaders: { Authorization: `Bearer ${token}` },
            onConnect: () => {
                // 채팅방 구독
                client.subscribe(`/topic/room.${roomId}`, (msg) => {
                    addMessage(JSON.parse(msg.body));
                });
                // 개인 알림 구독
                if (userId) {
                    client.subscribe(`/topic/user.${userId}`, (msg) => {
                        addNotification(msg.body);
                    });
                }
            },
            onStompError: (frame) => console.error('STOMP 에러:', frame),
        });

        client.activate();
        clientRef.current = client;

        return () => client.deactivate();
    }, [roomId, userId]);

    const sendMessage = (roomId, content) => {
        clientRef.current?.publish({
            destination: '/pub/chat.send',
            body: JSON.stringify({ roomId, content }),
        });
    };

    return { sendMessage };
};

export default useWebSocket;