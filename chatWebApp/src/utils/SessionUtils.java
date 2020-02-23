package utils;
import Lib.RepositoryManager;
import constants.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils
{
    public static String getUserName(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constants.USERNAME) == null)
        {
            return null;
        }
        return session.getAttribute(Constants.USERNAME).toString();
    }

    public static void removeUserNameSession(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(Constants.USERNAME);
    }
}