package httpserver;

import java.util.UUID;

/**
 * @author maobing.dmb
 * @date 2017/11/10
 */
public class HttpSession {

    public static final String SESSIONID = "SESSIONID";

    public String getSessionID() {
        return UUID.randomUUID().toString();
    }

}
