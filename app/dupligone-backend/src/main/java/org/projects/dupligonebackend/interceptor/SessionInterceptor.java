package org.projects.dupligonebackend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.projects.dupligonebackend.context.SessionContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private static final String SESSION_HEADER = "X-Session-Id";

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SessionContextHolder.clear();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String sessionId = request.getHeader(SESSION_HEADER);

        if(sessionId == null || sessionId.isBlank()){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing X-Session-Id header");
            return false;
        }

        try{
            UUID sessionID = UUID.fromString(sessionId); //validate UUID format
            SessionContextHolder.setSessionId(sessionID);
        } catch (IllegalArgumentException e){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid X-Session-Id format");
            return false;
        }

        return true;
    }
}
