package httpserver;

import java.util.HashMap;

/**
 * @author maobing.dmb
 * @date 2017/11/10
 */
public class HttpSessionManager {

    private static final HashMap<String, HttpSession> SESSION_HASH_MAP = new HashMap<String, HttpSession>();

    public static String getSessionId() {
        synchronized (SESSION_HASH_MAP) {
            HttpSession httpSession = new HttpSession();
            SESSION_HASH_MAP.put(httpSession.getSessionID(), httpSession);
            return httpSession.getSessionID();
        }
    }

    public static boolean isHasJsessionId(String sessionId) {
        synchronized (SESSION_HASH_MAP) {
            return SESSION_HASH_MAP.containsKey(sessionId);
        }
    }

}
