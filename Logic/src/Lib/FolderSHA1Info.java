package Lib;

public class FolderSHA1Info
{
    String SHA1;
    String name;
    String lastModify;

    public String getSHA1()
    {
        return SHA1;
    }

    public String getName()
    {
        return name;
    }

    public String getLastModify()
    {
        return lastModify;
    }

    public FolderSHA1Info(String SHA1, String name, String lastModify)
    {
        this.SHA1 = SHA1;
        this.name = name;
        this.lastModify = lastModify;
    }
}
