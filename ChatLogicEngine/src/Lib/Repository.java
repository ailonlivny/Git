package Lib;

import java.io.File;
import java.util.*;

public class Repository
{
    private String fullPath;
    private String name;
    private String remoteRepositoryName = null;
    private String remoteRepositoryPath = null;


    public Repository(String repositoryFullPath)
    {
        fullPath = repositoryFullPath;
        name = new File(fullPath).getName();
    }

    public void setRemoteRepositoryName(String RRname){
        remoteRepositoryName = RRname;
    }
    public void setRemoteRepositoryPath(String RRpath){
        remoteRepositoryPath = RRpath;
    }

    public String getFullPath()
    {
        return fullPath;
    }

    public String getName()
    {
        return name;
    }
}
