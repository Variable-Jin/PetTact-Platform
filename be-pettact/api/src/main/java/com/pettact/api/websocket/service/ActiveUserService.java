package com.pettact.api.websocket.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActiveUserService {

    private final Set<String> sessions = ConcurrentHashMap.newKeySet();

    public void addSession(String sessionId) {
        sessions.add(sessionId);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public int getActiveUserCount() {
        return sessions.size();
    }
}
