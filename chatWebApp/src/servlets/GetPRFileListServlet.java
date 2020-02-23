package servlets;

import Lib.PR;
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

public class GetPRFileListServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");
        String msg = request.getParameter("msg");
        String userName = SessionUtils.getUserName(request);
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        PRManager prManager = user.getPRManager();

        try(PrintWriter out=response.getWriter())
        {
            Gson gson=new Gson();
            String json=gson.toJson(prManager.getPullRequestMap().get(msg).getChanges());
            out.println(json);
        }

    }
}
