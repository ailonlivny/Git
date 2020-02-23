package servlets;
import Lib.RepositoryManager;
import exceptions.*;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadRepoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        Collection<Part> parts = request.getParts();
        StringBuilder fileContent = new StringBuilder();
        for (Part part : parts)
        {
            fileContent.append(readFromInputStream(part.getInputStream()));
        }

        UserInSystem currentUser= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(SessionUtils.getUserName(request));
        RepositoryManager repositoryManager= currentUser.getRepositoryManager();
        InputStream targetStream = new ByteArrayInputStream(fileContent.toString().getBytes());
        try
        {
            repositoryManager.isXmlValid(targetStream,currentUser.getUser().getName());

        } catch (JAXBException | InvocationTargetException | XmlException | IllegalAccessException | NoSuchMethodException e) {
            System.out.println(e.getMessage());
            response.sendError(403,"Exception by trying to read XML file");
            return;
        }
        try
        {
            File f = new File(repositoryManager.getXmlLoader().getrepositoryPath());
            f.mkdir();
            repositoryManager.getXmlLoader().loadRepo();
            currentUser.addRepository(repositoryManager.getActiveRepository());
            try(PrintWriter out=response.getWriter()){
                out.print(repositoryManager.getActiveRepository().getName());
            }
        } catch (UncommittedChangesError | InvalidBranchNameError | FirstCommitException | NoRepositoryExeption | NoChangesMadeException e) {
            response.sendError(403,e.getMessage());
        }

    }

    private String readFromInputStream(InputStream inputStream)
    {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }
}
