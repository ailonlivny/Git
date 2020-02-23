package Lib;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;

public class Settings {

    private static String currentUser = "Administrator";
    final static String delimiter = ",";
    final private static String magitFolder = "\\.magit";
    final private static String objectsFolder = magitFolder + "\\objects\\";
    final private static String branchFolder = magitFolder + "\\branches\\";
    final private static String activeBranchFile = "\\.magit\\HEAD";
    static String magicFullPath = "";
    static String repositoryFullPath = "";
    static String objectsFolderPath = "";
    static String branchFolderPath = "";
    static String activeBranchFilePath = "";
    final static String gitFolder = ".magit";

    private StringProperty repoFullPathProperty = new SimpleStringProperty(this,"repoFullPathProperty","");

    public String getRepoFullPathProperty()
    {
        return repoFullPathProperty.get();
    }

    public StringProperty repoFullPathPropertyProperty()
    {
        return repoFullPathProperty;
    }

    public void setRepoFullPathProperty(String repoFullPathProperty)
    {
        this.repoFullPathProperty.set(repoFullPathProperty);
    }

    public void setNewRepository(String repositoryPath)
    {
        repositoryFullPath = repositoryPath ;
        magicFullPath = repositoryFullPath + magitFolder;
        objectsFolderPath = repositoryFullPath + objectsFolder;
        branchFolderPath = repositoryFullPath + branchFolder;
        activeBranchFilePath = repositoryFullPath + activeBranchFile;
        setRepoFullPathProperty(repositoryFullPath);
    }

    public static String getObjectsFolder()
    {
        return objectsFolderPath;
    }

    public static String getMagicFullPath()
    {
        return magicFullPath;
    }

    public static String getBranchFolderPath()
    {
        File f = new File(branchFolderPath);
        File[] files = f.listFiles();
        for(File file : files)
        {
            if(file.isDirectory())
            {
                branchFolderPath = branchFolderPath + file.getName() + "\\";
            }
        }
        return branchFolderPath;
    }

    public static String getActiveBranchFilePath()
    {
        return activeBranchFilePath;
    }

    static String getUser()
    {
        return currentUser;
    }

    public static void setUser(String user)
    {
        currentUser = user;
    }
}