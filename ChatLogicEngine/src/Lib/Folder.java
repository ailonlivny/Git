package Lib;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class Folder extends Item
{
    public Map<String,Blob> Files;
    Map<String,Folder> Folders;

    Folder(String name, Map<String,Blob> Files,Map<String,Folder> Folders,String fullPath, String currentSHA1,String userLastModified,String lastModified )
    {
        typeItem = "Folder";
        this.name = name;
        this.Files = Files;
        this.Folders = Folders;
        this.fullPath = fullPath;
        this.currentSHA1=currentSHA1;
        this.userLastModified = userLastModified;
        this.lastModified = lastModified;
        String[] array = this.fullPath.split("\\\\");
        String relativePath = "";
        for(int i = 4; i<array.length;i++)
        {
            relativePath += array[i] + "\\";
        }
        this.relativePath = relativePath;
    }

    Folder (String name,String fullPath, String currentSHA1)
    {
        typeItem = "Folder";
        this.name = name;
        this.currentSHA1 = currentSHA1;
        this.fullPath = fullPath;
        String[] array = this.fullPath.split("\\\\");
        String relativePath = "";
        for(int i = 4; i<array.length;i++)
        {
            relativePath += array[i] + "\\";
        }
        this.relativePath = relativePath;
    }

    public String getSHA1()
    {
        return this.currentSHA1;
    }

    public void printFolderInfo()
    {
        //System.out.println(this.name + Lib.Settings.delimiter + this.typeItem + Lib.Settings.delimiter + this.getSHA1() + Lib.Settings.delimiter + this.userLastModified + Lib.Settings.delimiter + lastModified);

        for(Folder folder : Folders.values())
        {
            folder.printFolderInfo();
        }

        for(Blob blob : Files.values())
        {
            blob.printFileInfo();
        }
    }

    public Map<String,Item> createMapFromTree(int ByteNumbers)
    {
        Map<String,Item> ret = new HashMap<>();

        for(Folder folder : Folders.values())
        {
            ret.putAll(folder.createMapFromTree(ByteNumbers));
        }

        ret.put(fullPath.substring(ByteNumbers), this);

        for(Blob blob : Files.values())
        {
            ret.put(blob.fullPath.substring(ByteNumbers),blob);
        }

        return ret;
    }

    public Map<String,Item> createMapFromTree(){
        return createMapFromTree(0);
    }
}
