package servlets;

import Lib.MainEngine;
import Lib.RepositoryManager;
import com.google.gson.Gson;
import exceptions.FirstCommitException;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PushServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setContentType("application/json;charset=UTF-8");
        String userName = SessionUtils.getUserName(req);
        boolean isRemote = false;

        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager = user.getRepositoryManager();
        MainEngine engine = repositoryManager.getEngine();

        if(engine.isRepoHasRemote())
        {
            try {
                String pathFrom = new String(Files.readAllBytes(Paths.get(repositoryManager.settings().repositoryFullPath + "\\.magit\\pathToSrc")));
                isRemote = true;
                engine.Push(pathFrom);
            } catch (FirstCommitException e) {
                System.out.println(e.getMessage());
            }
        }

        try(PrintWriter out=resp.getWriter())
        {
            Gson gson=new Gson();
            String json=gson.toJson(isRemote);
            out.println(json);
        }
    }
}

