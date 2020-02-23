package Lib;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils
{

    public static void zip(String fullZipPath,String name,String data)
    {
        try
        {
            byte[] buffer = new byte[1024];
            FileOutputStream fos = new FileOutputStream(fullZipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            zos.putNextEntry(new ZipEntry(name));
            buffer = data.getBytes();
            zos.write(buffer, 0, buffer.length);
            zos.closeEntry();
            zos.close();
        }
        catch (IOException ioe)
        {
            System.out.println("Error creating zip file" + ioe);
        }
    }

    public static void unzip(String zipFilePath, String destDir)
    {
        File dir = new File(destDir);
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        byte[] buffer = new byte[1024];
        try
        {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
               // System.out.println("Unzipping to "+newFile.getAbsolutePath()); // TODO delete before sending
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e)
        {
            //Controller.CreateAlertDialog(null,"Error!!","Unzip fail");
        }
    }

    public static BlobInfo unzipString(String zipFilePath)
    {
        FileInputStream fis;
        String ret = "";
        String temp = "";
        String fileName = "";
        byte[] buffer = new byte[1024];
        try {
//            System.out.println(zipFilePath);
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                fileName = ze.getName();
                int len;
                while ((len = zis.read(buffer)) > 0)
                {
                    temp = new String(buffer);
                    ret += temp;
                }
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        return new BlobInfo(fileName,ret);
    }



    public static void clearCurrentWC(String path)
    {
        File directory;

        if(path.equals(""))
        {
            directory = new File(Settings.repositoryFullPath);
        }
        else
        {
            directory = new File(path);
        }

        File[] listOfItems = directory.listFiles();

        for(File item: listOfItems)
        {
            if(item.isDirectory())
            {
                if(!item.getName().equals(Settings.gitFolder))
                {
                    deleteSubFilesRec(item);
                }
            }
            else
            {
                Utils.deleteFile(item.getPath());
            }
        }
    }

    public static void clearAllFiles(String path)
    {
        File directory;
        directory = new File(path);

        File[] listOfItems = directory.listFiles();

        for(File item: listOfItems)
        {
            if(item.isDirectory())
            {
                deleteSubFilesRec(item);
            }
            else
            {
                deleteFile(item.getPath());
            }
        }
    }

    public static void clearCurrentWC()
    {
        clearCurrentWC("");
    }


    private static void deleteSubFilesRec(File folder)
    {
        File directory = new File(folder.getPath());
        File[] listOfItems = directory.listFiles();

        for (File item : listOfItems)
        {
            if (item.isDirectory())
            {
                deleteSubFilesRec(item);
            }
            else
            {
                deleteFile(item.getPath());
            }
        }
        deleteFile(folder.getPath());
    }

    static boolean deleteFile(String filePath)
    {
        try
        {
            System.gc();
            return Files.deleteIfExists(Paths.get(filePath));
        }
        catch (IOException e)
        {
            System.out.println(new File(filePath).exists());
            System.out.println("Could not delete file " + filePath);
            //Controller.CreateAlertDialog(null,"Error!!",e.getMessage());
            System.out.println( e.getMessage());
        }
        return false;
    }

    public static void createNewFile(String fileName, String content)
    {
        File file = new File(fileName);
        try
        {
            file.createNewFile();
            FileWriter writer = null;
            writer = new FileWriter(file);
            writer.write(content);
            writer.close();
        } catch (IOException e)
        {
            //Controller.CreateAlertDialog(null,"Error!!",e.getMessage());
        }
    }

    public static boolean createFolder(String folderPath)
    {
        File file = new File(folderPath);
        return file.mkdir();
    }

    public static void overrideReopsitory(String repo)
    {
        File directory = new File(repo);
        File[] listOfItems = directory.listFiles();
        for(File item: listOfItems){
            if(item.isDirectory())
            {
                deleteSubFilesRec(item);
            }
            else
            {
                Utils.deleteFile(item.getPath());
            }
        }
    }

}