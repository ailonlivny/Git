package servlets;

import users.UserInSystem;
import utils.ServletUtils;
import com.google.gson.*;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UsersInSystemServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");
        Gson gson = new Gson();
        Map<String, UserInSystem> users = ServletUtils.getUserManaqer(getServletContext()).getUsers();
        List<String> usersNamesList = new ArrayList<>(users.keySet());
        usersNamesList.remove(SessionUtils.getUserName(request));
        String json = gson.toJson(usersNamesList);
        System.out.println(json);
        try(PrintWriter out=response.getWriter())
        {
            out.print(json);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
}
