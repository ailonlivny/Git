package servlets;

import Lib.MainEngine;
import Lib.RepositoryManager;
import Lib.Settings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.FirstCommitException;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class PullServlet extends HttpServlet
{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        resp.setContentType("application/json;charset=UTF-8");
        String userName = req.getParameter("userName");
        boolean isRemote = false;

        if (userName == null)
        {
            userName = SessionUtils.getUserName(req);
        }
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager = user.getRepositoryManager();
        MainEngine engine = repositoryManager.getEngine();
        String fullRepoPath = repositoryManager.settings().repositoryFullPath;
        if(engine.isRepoHasRemote())
        {
            try {
                engine.fetch(fullRepoPath);
                engine.pull(fullRepoPath);
            } catch (FirstCommitException e) {
                System.out.println(e.getMessage());
            }
            isRemote = true;
        }

        try(PrintWriter out=resp.getWriter())
        {
            Gson gson=new Gson();
            String json=gson.toJson(isRemote);
            out.println(json);
        }
    }
}
