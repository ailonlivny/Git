package servlets;

import Lib.RepositoryManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class GetRepositoryAsJson extends HttpServlet
{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setContentType("application/json;charset=UTF-8");
        String userName = req.getParameter("userName");

        if (userName == null)
        {
            userName = SessionUtils.getUserName(req);
        }
        UserInSystem user=ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager=user.getRepositoryManager();
        try(PrintWriter out=resp.getWriter())
        {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//            Gson gson=new Gson();
            String json= gson.toJson(repositoryManager);
            out.println(json);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}