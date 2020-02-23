package servlets;

import Lib.PRManager;
import com.google.gson.Gson;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class GetPRServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setContentType("application/json;charset=UTF-8");
        String userName = SessionUtils.getUserName(req);
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        PRManager prManager = user.getPRManager();

        try(PrintWriter out=resp.getWriter())
        {
            Gson gson=new Gson();
            String json=gson.toJson(prManager.getPullRequestMap().values());
            out.println(json);
        }
    }
}
