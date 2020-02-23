package servlets;

import Lib.Commit;
import Lib.RepositoryManager;
import com.google.gson.Gson;
import exceptions.FirstCommitException;
import notification.Notification;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GetCommitsServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        String userName = SessionUtils.getUserName(request);
        UserInSystem user = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager= user.getRepositoryManager();
        try {
            if(repositoryManager.getEngine().isRepoHasRemote())
            {
                repositoryManager.refreshObj(true);
            }
            else
            {
                repositoryManager.refreshObj(false);
            }

        } catch (FirstCommitException e) {
            e.printStackTrace();
        }
        repositoryManager.updateBranchList();
        List<Commit> commits = repositoryManager.getCommitsFromHeadBranch();
        Gson gson = new Gson();
        String json=gson.toJson(commits);
        try(PrintWriter out=response.getWriter())
        {
            out.print(json);
        }
    }
}
