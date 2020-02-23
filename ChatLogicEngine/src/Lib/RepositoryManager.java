package Lib;

import com.google.gson.annotations.Expose;
import exceptions.FirstCommitException;
import exceptions.XmlException;
import fromXml.MagitRepository;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RepositoryManager
{
    Repository activeRepository = null;
    @Expose
    Head head;
    transient Map<String,Commit> commitSet = new HashMap<String,Commit>();

    public Settings settings() {
        return settings;
    }

    transient Settings settings = new Settings();
    String pathToDest = "";
    String pathToSrc = "";


    public MainEngine getEngine() {
        return engine;
    }

    transient  MainEngine engine = new MainEngine();

    public RepositoryManager()
    {
        engine.setRepositoryManager(this);
    }

    public XmlLoader getXmlLoader()
    {
        return xmlLoader;
    }

    XmlLoader xmlLoader;
    private MagitRepository m_MagitRepository;

    public String getPathToDest() {
        return pathToDest;
    }

    public void setPathToDest(String pathToDest) {
        this.pathToDest = pathToDest;
    }

    public String getPathToSrc() {
        return pathToSrc;
    }

    public void setPathToSrc(String pathToSrc) {
        this.pathToSrc = pathToSrc;
    }



    public Map<String, Commit> getCommitSet()
    {
        return commitSet;
    }

    public ArrayList<String>getCommitSetNamesByArrayList()
    {
        ArrayList<String> ret = new ArrayList<String>();

        for(String SHA1 : commitSet.keySet())
        {
            ret.add(SHA1);
        }

        return ret;
    }

    public Head getHead()
    {
        return head;
    }
    public void settHead(Head head)
    {
        this.head = head;
    }

    public Repository getActiveRepository()
    {
        return activeRepository;
    }

    void createNewRepository(String repositoryFullPath) throws IOException
    {
        settings.setNewRepository(repositoryFullPath);
        File f = new File(Settings.getMagicFullPath());
        f.mkdir();
        FileWriter fw = new FileWriter (Settings.getMagicFullPath() + "\\" + "HEAD");
        fw.write("master");
        fw.close();
        File objectsPath = new File(Settings.objectsFolderPath);

        if(!objectsPath.exists())
        {
            objectsPath.mkdirs();
        }
        File branchesPath = new File(Settings.branchFolderPath);

        if(!branchesPath.exists())
        {
            branchesPath.mkdirs();
            FileWriter fw2 = new FileWriter (Settings.getBranchFolderPath() + "\\" + "master");
            fw2.write("");
            fw2.close();
        }

        Repository repo = new Repository(repositoryFullPath);
        activeRepository = repo;
    }

    Blob createBlobObject(String SHA1File,String fullPath,String userLastModified,String lastModified)
    {
        BlobInfo content = Utils.unzipString(Settings.getObjectsFolder() + SHA1File + ".zip");
        return new Blob(content.name, content.content,fullPath + "\\" + content.name,SHA1File,userLastModified,lastModified);
    }

    Folder createFolderObj(String SHA1File ,String path,String userLastModified,String lastModified)
    {

        Map<String,Blob> Files = new HashMap();
        Map<String,Folder> Folders = new HashMap();
        BlobInfo content = Utils.unzipString(Settings.getObjectsFolder() + SHA1File + ".zip");
        String myPath = path + "\\" + content.name;
        String[] lines = content.content.split("\n");
        String modifyDate = "";
       for(int i =0; i<lines.length-1; i++)
       {
           String line = lines[i];
            String[] info = line.split(",");
            String type = info[2];
            String SHA1 = info[1];
            String user = info[3];
            modifyDate = info[4];

            if(type.equals("file"))
            {
                Blob blob = createBlobObject(SHA1,myPath,user,modifyDate);
                Files.put(SHA1,blob);
            }
            else
            {
                Folder folder = createFolderObj(SHA1,myPath,userLastModified,modifyDate);
                Folders.put(SHA1,folder);
            }
        }
        return new Folder(content.name,Files,Folders,myPath,SHA1File,userLastModified,modifyDate);
    }

    Commit createCommitObj(String SHA1File , String RepositoryPath)
    {
        if(SHA1File.equals("0"))
        {
            return null;
        }

        BlobInfo content = Utils.unzipString(RepositoryPath + "\\.magit\\objects\\" + SHA1File + ".zip");
        String[] lines = content.content.split("\n");

        List<String> list = new ArrayList<String>(Arrays.asList(lines));
        list.remove("RTB");
        list.remove("RB");
        lines = list.toArray(new String[0]);

        String RootSHA1 = lines[0];
        String prevCoomitStr = lines[1];
        String secondPrevCommitStr = lines[2];
        String commitTime = lines[3];
        String userLastModified = lines[4];
        String [] msgLines = Arrays.copyOfRange(lines,5,lines.length);
        String msg = String.join("\n" , msgLines);
        String str = RepositoryPath;
        int index = str.lastIndexOf("\\");
        str = str.substring(0,index);
        Folder rootFolder = createFolderObj(RootSHA1,str,lines[3],lines[4]);
        Commit prevCommit = createCommitObj(prevCoomitStr);
        Commit secondPrevCommit = createCommitObj(secondPrevCommitStr);
        Commit temp =  new Commit(msg,rootFolder,prevCommit,secondPrevCommit,commitTime,userLastModified,SHA1File);
        commitSet.put(temp.commitSHA1,temp);
        return temp;
    }

    Commit createCommitObj(String SHA1File)
    {
        return createCommitObj(SHA1File,Settings.repositoryFullPath);
    }

    Head createHeadObj() throws IOException
    {
        commitSet.clear();
        String currentBranch = new String(Files.readAllBytes(Paths.get(Settings.getActiveBranchFilePath())));
        BufferedReader br = new BufferedReader(new FileReader(Settings.getBranchFolderPath() + currentBranch));
        if (br.readLine() == null)
        {
            return new Head(currentBranch);
        }
        String currentCommit = new String(Files.readAllBytes(Paths.get(Settings.getBranchFolderPath()+ "\\" + currentBranch)));

        int index=currentCommit.lastIndexOf("\n");
        if(index != -1)
        {
            currentCommit = currentCommit.substring(0,index);
        }

        Commit commit = createCommitObj(currentCommit);
        return new Head(currentBranch,commit,currentCommit);
    }

    public void refreshObj(boolean forked) throws IOException, FirstCommitException
    {
        File HeadFile = new File(Settings.getActiveBranchFilePath());
        if(!HeadFile.exists())
        {
            throw new FirstCommitException();
        }
        else
        {
            if(head == null || forked || hasChanges() || isHeadValid())
            {
                head = createHeadObj();
            }
        }
    }

    boolean isHeadValid() throws IOException
    {
        String activeBranch = new String(Files.readAllBytes(Paths.get(Settings.getActiveBranchFilePath())));
        String SHA1 = new String(Files.readAllBytes(Paths.get(Settings.getBranchFolderPath() + activeBranch)));
        String[] lines = SHA1.split("\n");
        BlobInfo b = Utils.unzipString(Settings.getObjectsFolder() + lines[0]);
        String[] contect = b.content.split("/n");
        String rootSHA1 = contect[0];
        return !head.activeCommit.rootFolder.currentSHA1.equals(rootSHA1);
    }

    public void refreshObj() throws IOException, FirstCommitException
    {
        refreshObj(false);
    }

    public boolean hasChanges() throws IOException
    {
        if(head.commitSHA1.equals(MainEngine.getActiveCommitSHA1()))
        {
            return false;
        }
        return true;
    }

    public String WorkingCopyStatus() throws IOException, FirstCommitException
    {
        refreshObj();
        Map<String,Item> WC = MainEngine.createWC(Settings.repositoryFullPath);
        Map<String,Item> LastCommit = head.activeCommit.rootFolder.createMapFromTree();
        System.out.println(Settings.repositoryFullPath);
        System.out.println(head.activeCommit.rootFolder.fullPath);
        String ret = "";

        for(Item item : WC.values())
        {
            if(LastCommit.containsKey(item.fullPath))
            {
                if(!item.currentSHA1.equals(LastCommit.get(item.fullPath).getCurrentSHA1()))
                {
                    ret = ret + "Changed" + item.fullPath + '\n';
                }
            }
            else
            {
                ret = ret + "Created" + item.fullPath + '\n';
            }
        }

        for(Item item : LastCommit.values())
        {
            if(!WC.containsKey(item.fullPath))
            {
                ret = ret + "Deleted" + item.fullPath + '\n';
            }
        }

        return ret;
    }

    public List<Item> changesListForPr(String fromPath) throws IOException, FirstCommitException
    {
        refreshObj();
        Map<String,Item> WC = MainEngine.createWC(fromPath);
        Map<String,Item> LastCommit = head.activeCommit.rootFolder.createMapFromTree();

        WC = changeKeyVal(WC);
        LastCommit = changeKeyVal(LastCommit);
        List<Item> changes = new ArrayList<>();

        for(Item item : WC.values())
        {
            if(LastCommit.containsKey(item.relativePath))
            {
                if(!item.currentSHA1.equals(LastCommit.get(item.relativePath).getCurrentSHA1()))
                {
                    item.setStatus("Changed");
                    changes.add(item);
//                    ret = ret + "Changed" + item.fullPath + '\n';
                }
            }
            else
            {
                item.setStatus("Deleted");
                changes.add(item);
//                ret = ret + "Created" + item.fullPath + '\n';
            }
        }

        for(Item item : LastCommit.values())
        {
            if(!WC.containsKey(item.relativePath))
            {
                item.setStatus("Created");
                changes.add(item);
//                ret = ret + "Deleted" + item.fullPath + '\n';
            }
        }

        return changes;
    }

    private Map<String, Item> changeKeyVal(Map<String, Item> wc)
    {
        Map<String, Item> ret = new HashMap<>();
        for(Item item : wc.values())
        {
            ret.put(item.relativePath,item);
        }

        return ret;
    }

    public String deltaBetweenCommits(Commit active,Commit prev) throws IOException
    {
        Map<String,Item> activeCommit = active.rootFolder.createMapFromTree();
        Map<String,Item> prevCommit = prev.rootFolder.createMapFromTree();
        String ret = "";

        for(Item item : activeCommit.values())
        {
            if(prevCommit.containsKey(item.fullPath))
            {
                if(!item.currentSHA1.equals(prevCommit.get(item.fullPath).getCurrentSHA1()))
                {
                    ret = ret + "Changed" + item.fullPath + '\n';
                }
            }
            else
            {
                ret = ret + "Created" + item.fullPath + '\n';
            }
        }

        for(Item item : prevCommit.values())
        {
            if(!activeCommit.containsKey(item.fullPath))
            {
                ret = ret + "Deleted" + item.fullPath + '\n';
            }
        }

        return ret;
    }

    public void showCurrentBranchHistory()
    {
        Commit cur = head.activeCommit;

        while(cur!=null)
        {
            cur.printInfo();
            cur = cur.previousCommit;
        }
    }

    public void switchHeadBranch(String branch) throws IOException
    {
        if(isBranchExist(branch))
        {
            FileWriter fw = new FileWriter (Settings.getActiveBranchFilePath());
            fw.write(branch);
            fw.close();
            String currentCommit = new String(Files.readAllBytes(Paths.get(Settings.getBranchFolderPath()+ "\\" + branch)));
            int index=currentCommit.lastIndexOf("\n");
            if(index != -1)
            {
                currentCommit = currentCommit.substring(0,index);
            }
            Commit commit = createCommitObj(currentCommit);
            head.setInfo(branch,commit,currentCommit);
        }
        else
        {
            throw new IOException("Branch not exist");
        }
    }

    private boolean isBranchExist(String branch)
    {
        File f = new File(Settings.getBranchFolderPath() + branch);
        return f.exists();
    }

    public void replaceWC() throws IOException, FirstCommitException
    {
        Folder folder = head.activeCommit.rootFolder;
        System.out.println(head.activeCommit.commitSHA1);
        System.out.println(head.activeCommit.rootSha1);
        replaceWCRec(folder,Settings.repositoryFullPath);
    }

    public void replaceWCRec(Folder folder,String path)
    {
        for(Blob blob : folder.Files.values())
        {
            Utils.unzip(Settings.getObjectsFolder() + blob.currentSHA1 + ".zip",path);
        }

        for(Folder fold : folder.Folders.values())
        {
            File f = new File(path + "\\" + fold.name);
            f.mkdir();
            BlobInfo Info = Utils.unzipString(Settings.getObjectsFolder() + fold.getSHA1()+ ".zip");
            String[] lines = Info.content.split("\n");

            for(int i =0; i<lines.length-1; i++)
            {
                String line = lines[i];
                String[] info = line.split(",");
                String name = info[0];
                String type = info[2];
                String SHA1 = info[1];
                String user = info[3];
                String modifyDate = info[4];

                if(type.equals("file"))
                {
                    Utils.unzip(Settings.getObjectsFolder() + SHA1 + ".zip",path + "\\" + f.getName());
                }
                else
                {
                    replaceWCRec(fold,path + "\\" + f.getName());
                }
            }
        }
    }

    public void isXmlValid(String xmlPath) throws XmlException
    {
        xmlLoader = new XmlLoader(xmlPath);
        xmlLoader.checkValidXml();
//        return xmlLoader.checkRepoLocation();
    }

    public void isXmlValid(InputStream inputStream,String name) throws JAXBException, InvocationTargetException, FileNotFoundException, XmlException, IllegalAccessException, NoSuchMethodException {
        m_MagitRepository = XmlLoader.deserializeFrom(inputStream);
        xmlLoader = new XmlLoader(m_MagitRepository,name,this);
        xmlLoader.checkValidXml();
    }

    public List<Commit> getCommitsFromHeadBranch()
    {
        List<Commit> ret = new ArrayList<>();
        Commit activeCommit = head.activeCommit;
        getCommitsFromHeadBranchRec(activeCommit,ret);
        return ret;
    }

    private void getCommitsFromHeadBranchRec(Commit activeCommit, List<Commit> ret)
    {
        if(activeCommit == null)
        {
            return;
        }
        else
        {
            ret.add(activeCommit);
            getCommitsFromHeadBranchRec(activeCommit.previousCommit,ret);
            getCommitsFromHeadBranchRec(activeCommit.secondPreviousCommit,ret);
        }

    }

    public void updateBranchList() throws IOException
    {
        try {
            refreshObj();
        } catch (FirstCommitException e) {
            e.printStackTrace();
        }
        for(Commit commit :commitSet.values())
        {
            commit.branchList = "";
        }

        File f = new File(Settings.getBranchFolderPath());
        String[] files = f.list();

        for(String branch : files)
        {
            String SHA1 = new String(Files.readAllBytes(Paths.get(Settings.getBranchFolderPath() + branch)));
            String[] lines = SHA1.split("\n");
            System.out.println(lines[0]);
            Commit commit = commitSet.get(lines[0]);
            if(commit!=null)
            {
                commit.branchList = commit.branchList+ " " + branch;
            }
        }
    }
}
