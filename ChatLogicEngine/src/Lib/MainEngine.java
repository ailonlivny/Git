package Lib;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import exceptions.*;
import fromXml.MagitSingleBranch;
import fromXml.MagitSingleCommit;
import fromXml.PrecedingCommits;
import org.apache.commons.codec.digest.DigestUtils;
import puk.team.course.magit.ancestor.finder.AncestorFinder;


public class MainEngine
{


    RepositoryManager repositoryManager;
    static XmlLoader xmlLoader;
    boolean firstTime = true;
    int numOfBranches;

    public void setRepositoryManager(RepositoryManager repositoryManager)
    {
        this.repositoryManager = repositoryManager;
    }

    public static XmlLoader getXmlLoader()
    {
        return xmlLoader;
    }

    public RepositoryManager getRepositoryManager()
    {
        return repositoryManager;
    }

    public MainEngine()
    {

    }

    public String getUser()
    {
        return Settings.getUser();
    }

    public void createNewRepository(String newRepositoryPath) throws IOException
    {
        repositoryManager.commitSet.clear();
        repositoryManager.createNewRepository(newRepositoryPath);
    }

    public void changeUserName(String userName)
    {
        Settings.setUser(userName);
    }

    public void commit(String commitMsg, String secondPrevCommit,String dateFromXml) throws IOException, FirstCommitException, NoChangesMadeException {
        boolean isFirst = isFirstCommit();
        String time = getCurrTime();

        if(!dateFromXml.equals("0"))
        {
            time = dateFromXml;
        }
        if(isFirst)
        {
            FolderSHA1Info Info = createObjects(Settings.repositoryFullPath);
            String SHA1 = makeCommit(Info.SHA1, "0","0", time, Settings.getUser(),commitMsg,Info.lastModify);
            createBranchWithSHA1("master",SHA1);
            updateHead("master");
        }
        else
        {
            if(repositoryManager.WorkingCopyStatus().equals(""))
            {
                throw new NoChangesMadeException("There were no changes made from the last commit");
            }
            FolderSHA1Info Info = createObjects(Settings.repositoryFullPath);
            String prevCommit = "";
            prevCommit = getActiveCommitSHA1();
            String SHA1 = makeCommit(Info.SHA1, prevCommit, secondPrevCommit, time, Settings.getUser(),commitMsg,Info.lastModify);
            updateBranchCommit(SHA1);
        }

    }

    public void commitMerge(String commitMsg, String secondPrevCommit) throws IOException
    {
        FolderSHA1Info Info = createObjects(Settings.repositoryFullPath);
        String prevCommit = "";
        prevCommit = getActiveCommitSHA1();
        String SHA1 = makeCommit(Info.SHA1, prevCommit, secondPrevCommit, getCurrTime(), Settings.getUser(),commitMsg,Info.lastModify);
        updateBranchCommit(SHA1);
    }

    public void commit(String commitMsg) throws IOException, FirstCommitException, NoChangesMadeException {
        commit(commitMsg , "0");
    }

    public void commit(String commitMsg,String prevCommit) throws IOException, FirstCommitException, NoChangesMadeException {
        commit(commitMsg , prevCommit,"0");
    }

    public  void commit(MagitSingleCommit singleCommit) throws IOException, FirstCommitException, NoChangesMadeException {
        String secondPrevCommit = "0";

        if(singleCommit.getPrecedingCommits() == null)
        {

        }
        else if(singleCommit.getPrecedingCommits().getPrecedingCommit().isEmpty())
        {

        }
        else if(singleCommit.getPrecedingCommits().getPrecedingCommit().size() == 2)
        {
            for (PrecedingCommits.PrecedingCommit precedingCommit : singleCommit.getPrecedingCommits().getPrecedingCommit())
            {
                if (precedingCommit.getId() != getActiveCommitSHA1()) {
                    secondPrevCommit = precedingCommit.getId();
                }
            }
        }

        commit(singleCommit.getMessage(), secondPrevCommit , singleCommit.getDateOfCreation());

    }

    public static void updateBranchCommit(String SHA1) throws IOException
    {
        String currentBranch = new String(Files.readAllBytes(Paths.get(Settings.getActiveBranchFilePath())));
        String content = new String(Files.readAllBytes(Paths.get(Settings.getBranchFolderPath() + currentBranch)));
        if(content.contains("RTB"))
        {
            SHA1 = SHA1 + "\n" + "RTB";
        }
        FileWriter fw = new FileWriter (Settings.getBranchFolderPath() + "\\" + currentBranch);
        fw.write(SHA1);
        fw.close();
    }


    public void updateChooserBranch(String SHA1, String branch) throws IOException
    {
        FileWriter fw = new FileWriter (Settings.getBranchFolderPath() + "\\" + branch);
        fw.write(SHA1);
        fw.close();
    }

    public static String getActiveCommitSHA1() throws IOException
    {
        String currentBranch = new String(Files.readAllBytes(Paths.get(Settings.getActiveBranchFilePath())));
        String currentCommit = new String(Files.readAllBytes(Paths.get(Settings.getBranchFolderPath() + currentBranch)));
        int index1 = currentCommit.lastIndexOf("\n");
        if(index1 != -1)
        {
            currentCommit = currentCommit.substring(0,index1);
        }
        return currentCommit;
    }

    public static String makeCommit(String rootSHA1, String prevCommit, String secondePrevCommit, String dateCreated, String username, String msg, String dateModify)
    {
        String data = rootSHA1 + "\n" + prevCommit + "\n" + secondePrevCommit + "\n" + dateCreated + "\n" + username  + "\n" +  msg;
        String SHA1 = DigestUtils.sha1Hex(data);
        Utils.zip(Settings.objectsFolderPath + SHA1 + ".zip", SHA1, data);
        return SHA1;
    }

    public static boolean isFirstCommit()
    {
        File f = new File(Settings.getObjectsFolder());
        String[] files = f.list();

        if(files.length == 0)
        {
            return true;
        }

        return false;
    }

    public static FolderSHA1Info createObjects(String path) throws IOException
    {
        File f = new File(path);
        String[] files = f.list();
        String data = "";
        List<String> dataSHA1List = new ArrayList<String>();
        String dataSHA1 = "";

        if(!f.isDirectory())
        {
            data = new String(Files.readAllBytes(Paths.get(path)));
            dataSHA1List.add(data);
        }
        else
        {
            for(String file :files)
            {
                String thisPath = path + "/" + file;
                File f2 = new File(thisPath);
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-hh:mm:ss:sss");
                String lastModifay = dateFormat.format(f2.lastModified());

                if(thisPath.endsWith(".magit"))
                {
                    continue;
                }

                if(!f2.isDirectory())
                {
                    String sonSHA1 = createObjects(thisPath).SHA1;
                    dataSHA1List.add(file + Settings.delimiter + sonSHA1 + Settings.delimiter + "file" + "\n");
                    data = data + file + Settings.delimiter + sonSHA1 + Settings.delimiter + "file" + Settings.delimiter + Settings.getUser() + Settings.delimiter + lastModifay + "\n";
                }
                else
                {
                    String sonSHA1 = createObjects(thisPath).SHA1;
                    dataSHA1List.add(file + Settings.delimiter + sonSHA1 + Settings.delimiter + "folder" + "\n");
                    data = data + file + Settings.delimiter + sonSHA1 + Settings.delimiter + "folder" + Settings.delimiter + Settings.getUser() + Settings.delimiter + lastModifay + "\n";
                }
            }
        }

        Collections.sort(dataSHA1List);
        dataSHA1 = String.join("",dataSHA1List);
        String SHA1 = DigestUtils.sha1Hex(dataSHA1);
        File tempFile = new File(Settings.objectsFolderPath + SHA1 + ".zip");

        if(!tempFile.exists())
        {
            Utils.zip(Settings.objectsFolderPath + SHA1 + ".zip",f.getName(), data);
        }
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-hh:mm:ss:sss");
        String fLastModifay = dateFormat.format(f.lastModified());
        return new FolderSHA1Info(SHA1,Settings.getUser(),fLastModifay);
    }

    public static String getCurrTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-hh:mm:ss:sss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private static void createBranchWithSHA1(String branchName, String SHA1) throws IOException
    {
        FileWriter fw = new FileWriter (Settings.getBranchFolderPath() + "\\" + branchName);
        fw.write(SHA1);
        fw.close();
    }

    private static void updateHead(String branchHead) throws IOException
    {
        FileWriter fw = new FileWriter (Settings.getActiveBranchFilePath());
        fw.write(branchHead);
        fw.close();
    }

    public void switchRepository(String repositoryPath) throws IOException, FirstCommitException
    {

        File f = new File(repositoryPath + "\\" + ".magit");

        if(f.exists())
        {
            repositoryManager.commitSet.clear();
            repositoryManager.settings.setNewRepository(repositoryPath);
            repositoryManager.refreshObj();
        }
        else
        {
            throw new FileNotFoundException("The repository is not exist");
        }
    }

    public void showCurrentCommitFileSystemInformation() throws IOException, FirstCommitException
    {
        repositoryManager.refreshObj();
        repositoryManager.getHead().activeCommit.rootFolder.printFolderInfo();
    }


    public String showAllBranchInSystem() throws IOException
    {
        File f = new File(Settings.getBranchFolderPath());
        String[] files = f.list();
        String head = getCurrentHead();
        String ret = "";

        for(String file : files)
        {
            if(file.equals(head))
            {
                ret = ret + "Lib.Head->";
            }
            ret = ret + file + "\n";
            String commitSHA1 = new String(Files.readAllBytes(Paths.get(Settings.getBranchFolderPath() + file)));
            ret = ret + commitSHA1 + "\n";


//            Lib.Utils.unzip(Lib.Settings.getObjectsFolder() + commitSHA1 + ".zip", Lib.Settings.getMagicFullPath());
//            BufferedReader reader;
//
//            try
//            {
//                String line = "";
//                reader = new BufferedReader(new FileReader(Lib.Settings.getMagicFullPath() + "\\" + commitSHA1));
//                for(int i=0; i<5; i++)
//                {
//                    line = reader.readLine();
//                }
//                ret = ret + line + "\n";
//                reader.close();
//            } catch (IOException e)
//            {
//                e.printStackTrace();
//            }
            ret = ret + "\n";
//            File f2 = new File(Lib.Settings.getMagicFullPath() + "\\" + commitSHA1);
//            f2.delete();
        }

        return ret;
    }

    public ArrayList<String> getAllBranchnames() throws IOException
    {
        File f = new File(Settings.getBranchFolderPath());
        String[] files = f.list();
        ArrayList<String> ret = new ArrayList<String>();

        for(String file : files)
        {
            if(!file.contains("RB"))
            {
                ret.add(file);
            }
        }
        return ret;
    }

    public static String getCurrentHead() throws IOException
    {
        String currentHead = new String(Files.readAllBytes(Paths.get(Settings.getActiveBranchFilePath())));
        return currentHead;
    }

    public void deleteBranch(String branchToDelete) throws IOException, BranchNotExistException, MasterBranchExeption
    {

            String head = getCurrentHead();
            if (!(branchToDelete.equals(head)))
            {
                File f = new File(Settings.getBranchFolderPath() + branchToDelete);
                if (f.exists())
                {
                    f.delete();
                }
                else
                {
                    throw new BranchNotExistException("The branch does not exists");
                }
            }
            else
            {
                throw new MasterBranchExeption("The branch is the HEAD");
            }


    }

    public Boolean createBranch(String branchName) throws IOException
    {
        if(!isBranchExist(branchName))
        {
            File f = new File(Settings.getBranchFolderPath() + branchName);
            f.createNewFile();
            FileWriter writer = new FileWriter(f);
            writer.write(getActiveCommitSHA1());
            writer.close();
            return true;
        }
        else
        {
            return false;
        }
    }

    public static Boolean createBranch(MagitSingleBranch branch, String head) throws IOException
    {
        String type = "";
        String activeSHA1 = getActiveCommitSHA1();
        boolean retBool = false;

        if(branch.isIsRemote())
        {
            int index = branch.getName().lastIndexOf("\\");
            if(index != -1)
            {
                branch.setName(branch.getName().substring(index + 1)+"RB");
            }
            else
            {
                branch.setName(branch.getName()+"RB");
            }
            type = "\nRB";
        }
        else
        {
            if(branch.isTracking())
            {
                type = "\nRTB";
            }
        }

        if(!isBranchExist(branch.getName()))
        {
            File f = new File(Settings.getBranchFolderPath() + branch.getName());
            f.createNewFile();
            FileWriter writer = new FileWriter(f);
            writer.write(activeSHA1 + type);
            writer.close();
            retBool = true;
        }
        else if(branch.getName().equals("master") && branch.isTracking())
        {
            FileWriter writer = new FileWriter(Settings.getBranchFolderPath() + branch.getName());
            writer.write( activeSHA1 + type);
            writer.close();
            retBool = true;
        }
        else
        {
            retBool =  false;
        }

        if(branch.getName().equals(head))
        {
            updateHead(head);
        }
        return retBool;
    }

    public static boolean isBranchExist(String branchName)
    {
        File f = new File(Settings.getBranchFolderPath() + branchName);
        return f.exists();
    }

    public static Map<String,Item> createWC(String path) throws IOException
    {
        File file = new File(path);
        File[] dir = file.listFiles();
        Map<String,Item> ret = new HashMap<>();
        String data = "";
        List<String> SHA1List = new ArrayList<String>();

        for(File subFile : dir)
        {
            if(subFile.getPath().endsWith(".magit"))
            {
                continue;
            }

            if(subFile.isDirectory())
            {
                Map<String,Item> temp = createWC(subFile.getPath());
                ret.putAll(temp);
                String sonSHA1 = temp.get(subFile.getPath()).currentSHA1;
                SHA1List.add(subFile.getName() + Settings.delimiter + sonSHA1 + Settings.delimiter + "folder" + "\n");
            }
            else
            {
                Blob blob = new Blob (subFile.getName(),subFile.getPath(),DigestUtils.sha1Hex(new String(Files.readAllBytes(Paths.get(subFile.getPath())))),new String(Files.readAllBytes(Paths.get(subFile.getPath()))));
                ret.put(subFile.getPath(),blob);
                String sonSHA1 = blob.currentSHA1;
                SHA1List.add(subFile.getName() + Settings.delimiter + sonSHA1 + Settings.delimiter + "file" + "\n");
            }
        }

        Collections.sort(SHA1List);
        data = String.join("",SHA1List);
        Folder folder = new Folder(file.getName(),file.getPath(),DigestUtils.sha1Hex(data));
        ret.put(file.getPath(),folder);
        return ret;
    }



    public String WorkingCopyStatus() throws IOException, FirstCommitException
    {
        repositoryManager.refreshObj();
        return repositoryManager.WorkingCopyStatus();
    }


    public void showCurrentBranchHistory() throws IOException, FirstCommitException
    {
        repositoryManager.refreshObj();
        repositoryManager.showCurrentBranchHistory();
    }

    public void checkoutBranch(String branchName) throws IOException, FirstCommitException
    {
        repositoryManager.refreshObj();
        repositoryManager.switchHeadBranch(branchName);
        Utils.clearCurrentWC();
        repositoryManager.replaceWC();
    }

//    public void isXmlValid(String xmlPath) throws XmlException
//    {
//        xmlLoader = new XmlLoader(xmlPath);
//        xmlLoader.checkValidXml();
////        return xmlLoader.checkRepoLocation();
//
//    }

    public void loadRepositoyFromXML() throws UncommittedChangesError, InvalidBranchNameError, IOException, FirstCommitException, NoRepositoryExeption, NoChangesMadeException {
        xmlLoader.loadRepo();
    }

    public boolean isRepositoryExistInSystem() throws NoRepositoryExeption
    {
        if(Settings.repositoryFullPath.equals(""))
        {
            throw new NoRepositoryExeption("There is no repository defined in the system");
        }
        return true;
    }

    public void resetBranch(String sha1FromUser) throws IOException, NoCommitSHA1Exeption
    {
        File f = new File(Settings.getObjectsFolder() + sha1FromUser +".zip");
        if(!f.exists())
        {
            throw new NoCommitSHA1Exeption("Lib.Commit with this SHA1 is not exist");
        }
        BlobInfo CommitContent = Utils.unzipString(Settings.getObjectsFolder() + sha1FromUser + ".zip");
        String[] lines = CommitContent.content.split("\n");
        File f2 = new File(Settings.getObjectsFolder() + lines[0] + ".zip");
        if(!f2.exists())
        {
            throw new NoCommitSHA1Exeption("Lib.Commit with this SHA1 is not exist");
        }
        String branchHead = new String(Files.readAllBytes(Paths.get(Settings.getActiveBranchFilePath())));
        FileWriter fw = new FileWriter (Settings.getBranchFolderPath() + branchHead);
        fw.write(sha1FromUser);
        fw.close();
    }

    public ArrayList<Conflict> merge(String Our,String theirs,Boolean ShouldICommit) throws IOException
    {
        try {
            checkoutBranch(theirs);
            checkoutBranch(Our);
        } catch (FirstCommitException e) {
            //Controller.CreateAlertDialog(null,"Error!!",e.getMessage());
        }
        ShouldICommit = true;
        ArrayList<Conflict> conflicts = new ArrayList<>();
        String ourCommitSHA1 = new String(Files.readAllBytes(Paths.get(Settings.getBranchFolderPath() + Our)));
        String theirsCommitSHA1 = new String(Files.readAllBytes(Paths.get(Settings.getBranchFolderPath() + theirs)));
        Commit ourCommit = repositoryManager.commitSet.get(ourCommitSHA1);
        Commit theirsCommit = repositoryManager.commitSet.get(theirsCommitSHA1);
        AncestorFinder ancestorFinder = new AncestorFinder(SHA1->repositoryManager.commitSet.get(SHA1));
        String ancestor = ancestorFinder.traceAncestor(ourCommitSHA1,theirsCommitSHA1);
        Commit ancestorCommit = repositoryManager.commitSet.get(ancestor);
        Map<String,Item> ourCommitMap = ourCommit.rootFolder.createMapFromTree();
        Map<String,Item> theirsCommitMap = theirsCommit.rootFolder.createMapFromTree();
        Map<String,Item> ancestorCommitMap = ancestorCommit.rootFolder.createMapFromTree();

        if(ancestor.equals(theirsCommitSHA1))
        {
            ShouldICommit = false;
            return conflicts;
        }
        if(ancestor.equals(ourCommitSHA1))
        {
            ShouldICommit = false;
            updateChooserBranch(theirsCommitSHA1,Our);
            return conflicts;
        }

        for(String PathItem : ourCommitMap.keySet())
        {
            Item TheirsItem = theirsCommitMap.get(PathItem);
            Item ancestorItem = ancestorCommitMap.get(PathItem);
            Item oursItem = ourCommitMap.get(PathItem);
            if(oursItem.typeItem.equals("Lib.Folder"))
            {
                continue;
            }

            if(TheirsItem == null && ancestorItem != null && ancestorItem.currentSHA1.equals(oursItem.currentSHA1))
            {
                File f = new File(PathItem);
                f.delete();
            }
            if(TheirsItem == null && ancestorItem != null && !ancestorItem.currentSHA1.equals(oursItem.currentSHA1))
            {
                Conflict newConflict = new Conflict(MergeConflicts.OURSEXIST,PathItem,((Blob)oursItem).content,"",((Blob)ancestorItem).content);
                conflicts.add(newConflict);
            }
            if(TheirsItem != null && ancestorItem == null && !TheirsItem.currentSHA1.equals(oursItem.currentSHA1))
            {
                Conflict newConflict = new Conflict(MergeConflicts.COMPERISON,PathItem,((Blob)oursItem).content,((Blob)TheirsItem).content,"");
                conflicts.add(newConflict);
                //our did change, theirs change, conflict!! ***
            }
            if(TheirsItem != null && ancestorItem != null && ancestorItem.currentSHA1.equals(oursItem.currentSHA1) && !TheirsItem.currentSHA1.equals(oursItem.currentSHA1) )
            {
                File f = new File(PathItem);
                f.delete();
                FileWriter fw = new FileWriter (PathItem);
                fw.write(((Blob)TheirsItem).content);
                fw.close();
            }
            if(TheirsItem != null && ancestorItem != null && !TheirsItem.currentSHA1.equals(oursItem.currentSHA1) && !TheirsItem.currentSHA1.equals(ancestorItem.currentSHA1) && !ancestorItem.currentSHA1.equals(oursItem.currentSHA1))
            {
                Conflict newConflict = new Conflict(MergeConflicts.COMPERISON,PathItem,((Blob)oursItem).content,((Blob)TheirsItem).content,((Blob)ancestorItem).content);
                conflicts.add(newConflict);
                //our did change, theirs changed, conflict!! ***
            }
        }

        for(String PathItem : theirsCommitMap.keySet())
        {
            Item TheirsItem = theirsCommitMap.get(PathItem);
            Item ancestorItem = ancestorCommitMap.get(PathItem);
            Item oursItem = ourCommitMap.get(PathItem);

            if(TheirsItem.typeItem.equals("Lib.Folder"))
            {
                continue;
            }

            if(oursItem == null && ancestorItem != null && !ancestorItem.currentSHA1.equals(TheirsItem.currentSHA1))
            {
                Conflict newConflict = new Conflict(MergeConflicts.THEIRSEXIST,PathItem,"",((Blob)TheirsItem).content,((Blob)ancestorItem).content);
                conflicts.add(newConflict);
                //our deleted, theirs changed , conflict!!
            }
            if(oursItem == null && ancestorItem == null )
            {
                FileWriter fw = new FileWriter (PathItem);
                fw.write(((Blob)TheirsItem).content);
                fw.close();
            }
        }

        return conflicts;
    }

    public void CopyDeltaFromTo(String fromPath, String toPath, String fatherCommitSHA1, String sonCommitSHA1) throws IOException
    {
        Path from = Paths.get(fromPath + "\\.magit\\objects\\" + sonCommitSHA1 + ".zip");
        Path to = Paths.get(toPath + "\\.magit\\objects\\" + sonCommitSHA1 + ".zip");

        try {
            Files.copy(from,to);
        } catch (FileAlreadyExistsException e)
        {

        }

        Commit sonCommit = repositoryManager.createCommitObj(sonCommitSHA1,fromPath);
        CopyFolderFromTo(sonCommit.rootFolder,fromPath,toPath,sonCommit);

        if(fatherCommitSHA1.equals(sonCommitSHA1))
        {
            return;
        }
        if(sonCommit.previousCommit != null)
        {
            CopyDeltaFromTo(fromPath,toPath,fatherCommitSHA1,sonCommit.previousCommit.commitSHA1);
        }
        if(sonCommit.secondPreviousCommit != null)
        {
            CopyDeltaFromTo(fromPath,toPath,fatherCommitSHA1,sonCommit.secondPreviousCommit.commitSHA1);
        }
    }

    public void CopyFolderFromTo(Folder folder,String fromPath, String toPath,Commit sonCommit) throws IOException
    {
        String SHA1 = folder.getSHA1();

        Path from = Paths.get(fromPath + "\\.magit\\objects\\" + SHA1 + ".zip");
        Path to = Paths.get(toPath + "\\.magit\\objects\\" + SHA1 + ".zip");

        try {
            Files.copy(from,to);

            if(folder == sonCommit.rootFolder)
            {
                rewriteRootFolder(toPath,fromPath,SHA1);
            }

        } catch (FileAlreadyExistsException e)
        {

        }

        for(String blobSHA1 : folder.Files.keySet())
        {
            Path from1 = Paths.get(fromPath + "\\.magit\\objects\\" + blobSHA1 + ".zip");
            Path to1 = Paths.get(toPath + "\\.magit\\objects\\" + blobSHA1 + ".zip");

            try {
                Files.copy(from1,to1);
            } catch (FileAlreadyExistsException e)
            {

            }
        }

        for(Folder folder1 : folder.Folders.values())
        {
            CopyFolderFromTo(folder1,fromPath,toPath,sonCommit);
        }
    }

    private void rewriteRootFolder(String toPath, String fromPath, String SHA1) throws IOException
    {
        Utils.unzip(toPath + "\\.magit\\objects\\" + SHA1 + ".zip",toPath + "\\.magit\\objects");
        int index = toPath.lastIndexOf("\\");
        String destRootFolderName = toPath.substring(index + 1);
        index = fromPath.lastIndexOf("\\");
        String SrcRootFolderName = fromPath.substring(index + 1);
        String dataRootFile = new String(Files.readAllBytes(Paths.get(toPath + "\\.magit\\objects\\" + SrcRootFolderName)));
        File f = new File(toPath + "\\.magit\\objects\\" + SHA1 + ".zip");
        f.delete();
        Utils.zip(toPath + "\\.magit\\objects\\" + SHA1 + ".zip",destRootFolderName,dataRootFile);
        File f1 = new File(toPath + "\\.magit\\objects\\" + SrcRootFolderName);
        f1.delete();
    }

    public void clone(String pathFrom, String pathTo) throws IOException, FirstCommitException, NoRepositoryExeption
    {
        if(!isMagitExistInRepo(pathFrom))
        {
            throw new NoRepositoryExeption("there is no repository in the system");
        }
        if(!isMagitExistInRepo(pathTo))
        {
            createNewRepositoryClone(pathTo);
        }

        String activeRepo = Settings.repositoryFullPath;
        switchRepository(pathFrom);
        File f = new File(pathFrom + "\\.magit\\branches");
        String[] files = f.list();
        String SHA1RandomCommit = new String(Files.readAllBytes(Paths.get(pathFrom + "\\.magit\\branches\\" + files[0])));
        String firstCommit = getFirstCommitSHA1(SHA1RandomCommit,pathFrom);
        String[] array = pathFrom.split("\\\\");
        String repoName = array[array.length-1];

        for(String file : files)
        {
            String commitSHA1 = new String(Files.readAllBytes(Paths.get(pathFrom + "\\.magit\\branches\\" + file)));
            CopyDeltaFromTo(pathFrom, pathTo, firstCommit,commitSHA1);
            writeContentToFileInMerge(pathTo + "\\.magit\\branches\\" + repoName+ "\\" + file + "RB",commitSHA1 + "\n" + "RB");
            writeContentToFileInMerge(pathTo + "\\.magit\\branches\\" + repoName+ "\\" + file,commitSHA1 + "\n" + "RTB");
        }

        Path from = Paths.get(pathFrom + "\\.magit\\HEAD");
        Path to = Paths.get(pathTo + "\\.magit\\HEAD");
        File headFile = new File(pathTo + "\\.magit\\HEAD");

        if(headFile.exists())
        {
            headFile.delete();
        }

        try {
            Files.copy(from,to);
        } catch (FileAlreadyExistsException e)
        {

        }

        switchRepository(pathTo);
        Utils.clearCurrentWC();
        repositoryManager.replaceWC();
        switchRepository(activeRepo);
        FileWriter fw = new FileWriter (pathFrom + "\\" + ".magit" + "\\" + "pathToDest");
        fw.write(pathTo);
        fw.close();
        FileWriter fw1 = new FileWriter (pathTo + "\\" + ".magit" + "\\" + "pathToSrc");
        fw1.write(pathFrom);
        fw1.close();
        repositoryManager.setPathToDest(pathTo);
        repositoryManager.setPathToSrc(pathFrom);
    }

    private void createNewRepositoryClone(String pathTo) throws IOException
    {
        String activeRepo = Settings.repositoryFullPath;
        createNewRepository(pathTo);
        repositoryManager.settings.setNewRepository(activeRepo);
    }

    public String getFirstCommitSHA1(String SHA1Commit,String fromPath)
    {
//        Commit commit = repositoryManager.createCommitObj(SHA1Commit,fromPath);
        Commit commit = repositoryManager.getHead().activeCommit;
        Commit sonCommit = commit.previousCommit;
        while(sonCommit != null)
        {
            commit = sonCommit;
            sonCommit = sonCommit.previousCommit;
        }
        return commit.commitSHA1;
    }


    public void writeContentToFileInMerge(String conflictPath, String content) throws IOException
    {
        File f = new File(conflictPath);
        if(f.exists())
        {
            FileWriter fw = new FileWriter (conflictPath);
            fw.write(content);
            fw.close();
        }
        else
        {
            File file = new File(conflictPath);
            file.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
        }

    }

    boolean isMagitExistInRepo(String path)
    {
        File f = new File(path + "\\" + ".magit");
        return f.exists();
    }

    public void fetch(String pathTo) throws IOException, FirstCommitException
    {
//        String activeRepo = Settings.repositoryFullPath;
//        repositoryManager.refreshObj(true);
//        String pathFrom = new String(Files.readAllBytes(Paths.get(pathTo + "\\.magit\\pathToSrc")));
//        File f = new File(pathFrom + "\\.magit\\branches");
//        String[] files = f.list();
//        String[] array = pathFrom.split("\\\\");
//        String repoNameSrc = array[array.length-1];
//
//        for(String branch : files)
//        {
//            String sonSHA1 = new String(Files.readAllBytes(Paths.get(pathFrom + "\\.magit\\branches" + "\\" + branch)));
//            String fatherSHA1 = new String(Files.readAllBytes(Paths.get(pathTo + "\\.magit\\branches" + "\\" + repoNameSrc + "\\" + branch + "RB")));
//            int index = fatherSHA1.lastIndexOf("\n");
//            fatherSHA1 = fatherSHA1.substring(0,index);
//            if(!sonSHA1.equals(fatherSHA1))
//            {
//                CopyDeltaFromTo(pathFrom, pathTo, fatherSHA1, sonSHA1);
//                FileWriter fw = new FileWriter (pathTo + "\\.magit\\branches" + "\\" + repoNameSrc + "\\" + branch + "RB" );
//                fw.write(sonSHA1 + "\n" + "RB");
//                fw.close();
//            }
//        }
//
//        switchRepository(pathTo);
//       // repositoryManager.refreshObj(true);
//        Utils.clearCurrentWC();
//        repositoryManager.replaceWC();
//        switchRepository(activeRepo);

        String activeRepo = Settings.repositoryFullPath;
        repositoryManager.refreshObj();
        String pathFrom = new String(Files.readAllBytes(Paths.get(pathTo + "\\.magit\\pathToSrc")));
        switchRepository(pathFrom);
        File f = new File(pathFrom + "\\.magit\\branches");
        String[] files = f.list();
        String[] array = pathFrom.split("\\\\");
        String repoNameSrc = array[array.length-1];

        for(String branch : files)
        {
            String sonSHA1 = new String(Files.readAllBytes(Paths.get(pathFrom + "\\.magit\\branches" + "\\" + branch)));
            String fatherSHA1 = new String(Files.readAllBytes(Paths.get(pathTo + "\\.magit\\branches" + "\\" + repoNameSrc + "\\" + branch + "RB")));
            int index = fatherSHA1.lastIndexOf("\n");
            fatherSHA1 = fatherSHA1.substring(0,index);
            if(!sonSHA1.equals(fatherSHA1))
            {
                CopyDeltaFromTo(pathFrom, pathTo, fatherSHA1, sonSHA1);
                FileWriter fw = new FileWriter (pathTo + "\\.magit\\branches" + "\\" + repoNameSrc + "\\" + branch + "RB" );
                fw.write(sonSHA1 + "\n" + "RB");
                fw.close();
            }
        }


//        switchRepository(pathTo);
//        repositoryManager.refreshObj(true);
//        Utils.clearCurrentWC();
//        repositoryManager.replaceWC();
        switchRepository(activeRepo);
    }

    public boolean isRepoHasRemote()
    {
        File f = new File(Settings.repositoryFullPath + "\\" + ".magit" + "\\" + "pathToSrc");
        return f.exists();
    }

    public void pull(String repoPath) throws IOException, FirstCommitException
    {
        String activeRepo = Settings.repositoryFullPath;
        switchRepository(repoPath);
        String branchNameRTB = getCurrentHead();
        String branchNameRB = branchNameRTB + "RB";
        String SHA1RB = new String(Files.readAllBytes(Paths.get(Settings.getBranchFolderPath() + branchNameRB)));
        FileWriter fw = new FileWriter(Settings.getBranchFolderPath() + branchNameRTB);
        fw.write(SHA1RB.substring(0,SHA1RB.length()-1) + "TB");
        fw.close();
        repositoryManager.refreshObj(true);
        Utils.clearCurrentWC();
        repositoryManager.replaceWC();
        switchRepository(activeRepo);
    }

    public void Push(String pathFrom) throws IOException, FirstCommitException
    {
        String activeRepo = Settings.repositoryFullPath;
        boolean isNewBranch = false;
        String pathTo = new String(Files.readAllBytes(Paths.get(pathFrom + "\\.magit\\pathToDest")));
        String activeBranch = new String(Files.readAllBytes(Paths.get(pathTo + "\\.magit\\HEAD")));
        String[] array = pathFrom.split("\\\\");
        String repoNameSrc = array[array.length-1];

        String sonSHA1 = new String(Files.readAllBytes(Paths.get(pathTo + "\\.magit\\branches\\" + repoNameSrc + "\\" + activeBranch)));
        int index = sonSHA1.lastIndexOf("\n");
        if(index != -1)
        {
            sonSHA1 = sonSHA1.substring(0,index);
        }
        File f = new File(pathFrom + "\\.magit\\branches\\" + activeBranch);
        String fatherSHA1 = "";

        if(f.exists())
        {
            fatherSHA1 = new String(Files.readAllBytes(Paths.get(pathFrom + "\\.magit\\branches\\" + activeBranch)));
        }
        else
        {
            fatherSHA1 = getFirstCommitSHA1(repositoryManager.head.commitSHA1,pathFrom);
            isNewBranch = true;
        }

        if(!sonSHA1.equals(fatherSHA1))
        {
            CopyDeltaFromTo(pathTo, pathFrom, fatherSHA1, sonSHA1);
            if(isNewBranch)
            {
                File newFile = new File(pathFrom + "\\.magit\\branches\\" + activeBranch);
                newFile.createNewFile();
            }
            FileWriter fw = new FileWriter (pathFrom + "\\.magit\\branches\\" + activeBranch );
            fw.write(sonSHA1);
            fw.close();
        }

        switchRepository(pathFrom);
        Utils.clearCurrentWC();
        repositoryManager.replaceWC();
        switchRepository(activeRepo);
        String branchNameRB = activeBranch + "RB";
        FileWriter fw = new FileWriter(pathTo + "\\.magit\\branches\\" + repoNameSrc + "\\" + branchNameRB);
        fw.write(sonSHA1 + "\n" + "RB");
        fw.close();

        if(isNewBranch)
        {
            FileWriter fw2 = new FileWriter(pathTo + "\\.magit\\branches\\" + repoNameSrc + "\\" + activeBranch);
            fw2.write(sonSHA1 + "\n" + "RTB");
            fw2.close();
        }
    }

//    public void createCommits(Graph graph) throws IOException, FirstCommitException
//    {
//        createAllObjcets();
//        ArrayList<Commit> commitArray = new ArrayList<>(repositoryManager.commitSet.values());
//        Model model = graph.getModel();
//
//        if(firstTime)
//        {
//            graph.beginUpdate();
//            firstTime = false;
//        }
//
//        Map<String,Commit> commitSet = repositoryManager.commitSet;
//        Map<String,ICell> cells = new Hashtable<>();
//        Collections.sort(commitArray);
//
//        for(Commit commit : commitArray)
//        {
//            CommitNode temp = new CommitNode(commit.commitTime, commit.userLastModified, commit.msg,commit,commit.commitSHA1);
//            commit.setCommitNodeObj(temp);
//            cells.put(commit.commitSHA1,temp);;
//        }
//
//        for(ICell cell : cells.values())
//        {
//            model.addCell(cell);
//        }
//
//        Set<Edge> edges = new HashSet<>();
//        for(Commit commit : commitArray)
//        {
//            if(commit.previousCommit != null)
//            {
//                final Edge edge1 = new Edge(cells.get(commit.previousCommit.commitSHA1),cells.get(commit.commitSHA1));
//                model.addEdge(edge1);
//            }
//            if(commit.secondPreviousCommit != null)
//            {
//                final Edge edge2 = new Edge(cells.get(commit.secondPreviousCommit.commitSHA1),cells.get(commit.commitSHA1));
//                model.addEdge(edge2);
//            }
//        }


//        for(Lib.Commit commit : commitArray)
//        {
//            System.out.println(commit.msg + "\n" + commit.previousCommit + "\n" + commit.secondPreviousCommit + "\n" + commit.commitNodeObj + "\n");
//        }
//
//        graph.endUpdate();
//        graph.layout(new CommitTreeLayout());
//    }

    public ArrayList<String> getAllSHA1Branches() throws IOException
    {
        ArrayList<String> AllBranches = getAllBranchnames();
        ArrayList<String> SHA1Branches = new ArrayList<>();

        for(String branch : AllBranches)
        {
            String SHA1 = new String(Files.readAllBytes(Paths.get(Settings.getBranchFolderPath() + branch)));
            int index = SHA1.lastIndexOf("\n");
            if(index != -1)
            {
                SHA1 = SHA1.substring(0,index);
            }
            SHA1Branches.add(SHA1);
        }

        return SHA1Branches;
    }

    public void createAllObjcets() throws IOException, FirstCommitException
    {
        ArrayList<String> Branches = getAllBranchnames();
        String headBranch = new String(Files.readAllBytes(Paths.get(Settings.getActiveBranchFilePath())));
        repositoryManager.commitSet.clear();
        for(String branch : Branches)
        {
            repositoryManager.switchHeadBranch(branch);
            repositoryManager.refreshObj();
        }
        repositoryManager.switchHeadBranch(headBranch);
    }

    public ArrayList<String> removeBranchesWithRB(ArrayList<String> branchNames)
    {
        ArrayList<String> ret = new ArrayList<>();

        for(String str : branchNames)
        {
           if(!str.endsWith("RB"))
           {
               ret.add(str);
           }
        }

        return ret;
    }

    public boolean createBranchRR(String branchName, String branchType) throws IOException
    {
        String SHA1 = getActiveCommitSHA1();

        if(!isBranchExist(branchName))
        {
            if(branchType.equals("RB"))
            {
                String activeBranch = new String(Files.readAllBytes(Paths.get(Settings.getActiveBranchFilePath())));
                SHA1 = new String(Files.readAllBytes(Paths.get(Settings.getBranchFolderPath() + activeBranch + "RB")));
                String[] lines = SHA1.split("\n");
                SHA1 = lines[0] + "\n" + "RTB";
            }
            File f = new File(Settings.getBranchFolderPath() + branchName);
            f.createNewFile();
            FileWriter writer = new FileWriter(f);
            writer.write(SHA1);
            writer.close();
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean IsRemoteRepo()
    {
        File f = new File(Settings.getMagicFullPath() + "\\" + "pathToSrc");
        if(f.exists())
        {
            return true;
        }
        return false;
    }

   public String getCommitPointingBranchName(Commit commit) throws IOException
   {
       String ret="";
       File f = new File(Settings.getBranchFolderPath());
       String[] branches = f.list();

       for(String branch : branches)
       {
           String sha1 = new String(Files.readAllBytes(Paths.get(Settings.getBranchFolderPath() + branch)));
           String[] lines = sha1.split("\n");
           if(lines[0].equals(commit.commitSHA1))
           {
               ret = branch;
           }
       }
       return ret;
   }

   public int NumOfBranches() throws IOException
   {
       File f = new File(Settings.getBranchFolderPath());
       String[] branches = f.list();
       return branches.length;
   }
}




