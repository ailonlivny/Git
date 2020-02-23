package servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SaveFileServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json;charset=UTF-8");
        String filePath = request.getParameter("filePath");
        String content = request.getParameter("content");
        String originalContent = new String(Files.readAllBytes(Paths.get(filePath)));
        if(!originalContent.equals(content))
        {
            FileWriter fw = new FileWriter (filePath);
            fw.write(content);
            fw.close();
        }
    }
}
