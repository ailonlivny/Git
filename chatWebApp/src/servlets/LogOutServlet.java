package servlets;

import Lib.Utils;
import users.UserManager;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogOutServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    {
        UserManager userManager = ServletUtils.getUserManaqer(getServletContext());
        String userName = SessionUtils.getUserName(request);
        userManager.getUsers().get(userName).setLogedIn(false);
        SessionUtils.removeUserNameSession(request);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
}
