package Lib;

public class Repository
{
    private String fullPath;
    private String remoteRepositoryName = null;
    private String remoteRepositoryPath = null;

    public Repository(String repositoryFullPath)
    {
        fullPath = repositoryFullPath;
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
}
