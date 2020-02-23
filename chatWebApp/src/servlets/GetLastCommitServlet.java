package servlets;

import Lib.Commit;
import Lib.RepositoryManager;
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
import java.util.List;

public class GetLastCommitServlet extends HttpServlet
{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String userName = req.getParameter("userName");
        if (userName == null)
        {
            userName = SessionUtils.getUserName(req);
        }
        UserInSystem user = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager= user.getRepositoryManager();
        Commit commit = repositoryManager.getHead().activeCommit;
        Gson gson = new Gson();
        String json=gson.toJson(commit);
        try(PrintWriter out=resp.getWriter())
        {
            out.print(json);
        }
    }
}