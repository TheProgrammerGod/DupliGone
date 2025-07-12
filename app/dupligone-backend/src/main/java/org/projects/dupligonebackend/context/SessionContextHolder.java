package org.projects.dupligonebackend.context;

import java.util.UUID;

public class SessionContextHolder {

    private static final ThreadLocal<UUID> sessionIdHolder = new ThreadLocal<>();

    public static void setSessionId(UUID sessionId){
        sessionIdHolder.set(sessionId);
    }

    public static UUID getSessionId(){
        return sessionIdHolder.get();
    }

    public static void clear(){
        sessionIdHolder.remove();
    }

}
