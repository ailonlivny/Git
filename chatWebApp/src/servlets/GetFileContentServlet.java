package servlets;

import Lib.*;
import com.google.gson.Gson;
import exceptions.FirstCommitException;
import org.apache.commons.codec.binary.StringUtils;
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

public class GetFileContentServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");
        String path = request.getParameter("path");
        String userName = SessionUtils.getUserName(request);
        UserInSystem user = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager= user.getRepositoryManager();
        try {
            repositoryManager.refreshObj();
        } catch (FirstCommitException e) {
            e.printStackTrace();
        }

//        System.out.println(repositoryManager.getHead().activeCommit);
//        System.out.println("----");
//        System.out.println(repositoryManager.getHead().activeCommit.rootFolder);
//        System.out.println("----");
//        System.out.println(repositoryManager.getHead().activeCommit.rootFolder.Files);
//        System.out.println("----");
//        System.out.println(repositoryManager.getHead().activeCommit.rootFolder.Files.get(path));
//        System.out.println("----");
//        System.out.println(path);
//        System.out.println("----");
//        System.out.println(repositoryManager.getHead().activeCommit.rootFolder.Files.get(path).content);
//        System.out.println("----");

//        String[] lines = path.split("\\\\");
//        String ret = "";
//
//        for(int i = 3 ; i<lines.length;i++)
//        {
//            ret = ret + "\\" + lines[i];
//        }
//
//        System.out.println(ret);
//        ret = ret.substring(1,ret.length());
//        System.out.println(ret);



        String content = ((Blob)(MainEngine.createWC(repositoryManager.settings().repositoryFullPath)).get(path)).content;
        System.out.println(content);
        try(PrintWriter out=response.getWriter())
        {
            Gson gson=new Gson();
            String json= gson.toJson(content);
            out.println(json);
        }
    }

}
