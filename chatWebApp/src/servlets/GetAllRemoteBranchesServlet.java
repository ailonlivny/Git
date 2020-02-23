package servlets;

import Lib.RepositoryManager;
import com.google.gson.Gson;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class GetAllRemoteBranchesServlet extends HttpServlet
{

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");
        String userName = SessionUtils.getUserName(request);
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager= user.getRepositoryManager();
        String fullRepoPath = repositoryManager.settings().repositoryFullPath;
        String[] branches = null;
        if(new File(fullRepoPath+ "\\.magit\\pathToSrc").exists())
        {
            String pathFrom = new String(Files.readAllBytes(Paths.get( fullRepoPath+ "\\.magit\\pathToSrc")));
            File f = new File(pathFrom + "\\.magit\\branches");
            branches = f.list();
        }


        try(PrintWriter out=response.getWriter())
        {
            Gson gson=new Gson();
            String json=gson.toJson(branches);
            out.println(json);
        }
    }

}
