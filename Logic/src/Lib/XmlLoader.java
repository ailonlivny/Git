package Lib;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import exceptions.*;
import fromXml.*;
import fromXml.Item;

public class XmlLoader {

    private MagitRepository magitRepository;
    private MagitBlobs magitBlobs;
    private MagitBranches magitBranches;
    private MagitFolders magitFolders;
    private MagitCommits magitCommits;
    private String repositoryPath;
    private Map<String, MagitBlob> blobMap = new HashMap<>();
    private Map<String, MagitSingleFolder> folderMap = new HashMap<>();
    private Map<String, MagitSingleCommit> commitMap = new HashMap<>();
    private Map<String, List<MagitSingleCommit>> commitPointersMap = new HashMap<>();
    private MagitSingleCommit firstCommit;
    private MagitRepository.MagitRemoteReference remoteReference;

    RepositoryManager repositoryManager = MainEngine.getRepositoryManager();

    public String getrepositoryPath() {
        return repositoryPath;
    }

    public XmlLoader(String XmlPath) throws XmlException {
        File file = new File(XmlPath);
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(MagitRepository.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            magitRepository = (MagitRepository) jaxbUnmarshaller.unmarshal(file);
            magitBranches = magitRepository.getMagitBranches();
            magitCommits = magitRepository.getMagitCommits();
            magitFolders = magitRepository.getMagitFolders();
            magitBlobs = magitRepository.getMagitBlobs();
            repositoryPath = magitRepository.getLocation();
            remoteReference = magitRepository.getMagitRemoteReference();

        } catch (JAXBException e)
        {
            throw new XmlException("Given file has no XML extension OR XML file not exist");
        }
    }

    public void checkValidXml() throws XmlException
    {
        checkMagitBlolb();
        checkMagitCommits();
        checkMagitFodler();
        checkFolderPointers();
        checkCommitsPointers();
        checkBranchPointers();
        checkHeadPointer();
        CheckMagitRemoteReference();
        checkBranchRemotes();
    }

    private void checkBranchRemotes() throws XmlException {
        Map<String,MagitSingleBranch> branches = getHashMapBranch();

        for(MagitSingleBranch branch :magitBranches.getMagitSingleBranch())
        {
            if(branch.isTracking())
            {
                if(!branches.get(branch.getTrackingAfter()).isIsRemote())
                {
                    throw new XmlException("The branch" + branch.getName() + " is tracking while the branch" + branch.getTrackingAfter() + " is not remote");
                }
            }
        }
    }

    private void CheckMagitRemoteReference() throws XmlException
    {
        if(remoteReference != null)
        {
            if(remoteReference.getLocation() != null)
            {
                File f = new File(remoteReference.getLocation());
                if(!f.exists())
                {
                    throw new XmlException("There is not remote repository in the location");
                }
            }
        }
    }

    public void loadRepo() throws UncommittedChangesError, InvalidBranchNameError, IOException, FirstCommitException, NoRepositoryExeption, NoChangesMadeException {

        repositoryManager.createNewRepository(repositoryPath);
        Utils.clearCurrentWC();
        setFirstCommit();

        if(remoteReference.getLocation() != null)
        {
            repositoryManager.getActiveRepository().setRemoteRepositoryName(
                    remoteReference.getName());
            repositoryManager.getActiveRepository().setRemoteRepositoryPath(
                    remoteReference.getLocation());
            FileWriter fw = new FileWriter (repositoryPath + "\\" + ".magit" + "\\" + "pathToSrc");
            //System.out.println(remoteReference.getLocation());
            fw.write( magitRepository.getMagitRemoteReference().getLocation());
            fw.close();
            FileWriter fw1 = new FileWriter ( remoteReference.getLocation() + "\\" + ".magit" + "\\" + "pathToDest");
            fw1.write(repositoryPath);
            fw1.close();

            File f = new File(Settings.getBranchFolderPath() + remoteReference.getName());
            f.mkdir();
        }

        if(!(firstCommit == null))
        {
            buildCommitPointersMap();
            openCommitRec(firstCommit, null);
            //Controller.getEngine().checkoutBranch(magitBranches.getHead());
        }




    }

    private void setFirstCommit()
    {
        for (MagitSingleCommit magitSingleCommit : magitCommits.getMagitSingleCommit())
        {
            if (magitSingleCommit.getPrecedingCommits() == null ||
                    magitSingleCommit.getPrecedingCommits().getPrecedingCommit().isEmpty())
            {
                firstCommit = magitSingleCommit;
                break;
            }
        }
    }

    private void buildCommitPointersMap()
    {
        for(MagitSingleCommit commit: magitCommits.getMagitSingleCommit())
        {
            commitPointersMap.put(commit.getId(), new ArrayList<>());
        }
        for(MagitSingleCommit commit: magitCommits.getMagitSingleCommit())
        {
            if(commit.getPrecedingCommits() == null) continue;
            if(commit.getPrecedingCommits().getPrecedingCommit().isEmpty() ) continue;
            for(PrecedingCommits.PrecedingCommit precedingCommit : commit.getPrecedingCommits().getPrecedingCommit())
            {
                List<MagitSingleCommit> currentChilds = commitPointersMap.get(precedingCommit.getId());
                currentChilds.add(commit);
            }
        }
    }

    public void openCommitRec(MagitSingleCommit commit, String prevCommitSha1) throws IOException, NoRepositoryExeption, FirstCommitException, NoChangesMadeException {
        openCommit(commit.getId(), prevCommitSha1);
        List<MagitSingleCommit> commitChilds = commitPointersMap.get(commit.getId());
        if(!commitChilds.isEmpty())
        {
            for(MagitSingleCommit child: commitChilds)
            {
              //  openCommitRec(child, Controller.getEngine().getActiveCommitSHA1());
            }
        }
    }

    public void openCommit(String commitID, String prevCommit) throws IOException, NoRepositoryExeption, FirstCommitException, NoChangesMadeException {
        MagitSingleCommit magitCommit = commitMap.get(commitID);
        MagitSingleFolder magitRootFolder = folderMap.get(magitCommit.getRootFolder().getId());
        createFilesTree(magitRootFolder, Settings.repositoryFullPath);
       // Controller.getEngine().commit(magitCommit);
        List<MagitSingleBranch> pointingBranches = getPointedMagitBranch(magitCommit.getId());
        String head = magitBranches.getHead();

        if (!pointingBranches.isEmpty())
        {
            for(MagitSingleBranch pointingBranch: pointingBranches)
            {
               // Controller.getEngine().createBranch(pointingBranch,head);
            }
        }

        Utils.clearCurrentWC();
        return;
    }

    private List<MagitSingleBranch> getPointedMagitBranch(String id)
    {
        List<MagitSingleBranch> pointingBranches = new LinkedList<>();

        for(MagitSingleBranch magitBranch : magitBranches.getMagitSingleBranch())
        {
            if (magitBranch.getPointedCommit().getId().equals(id))
                pointingBranches.add(magitBranch);

        }
        return pointingBranches;
    }

    private Map<String,MagitSingleBranch> getHashMapBranch()
    {
        Map<String,MagitSingleBranch> pointingBranches = new HashMap<>();

        for(MagitSingleBranch magitBranch : magitBranches.getMagitSingleBranch())
        {
                pointingBranches.put(magitBranch.getName(),magitBranch);
        }
        return pointingBranches;
    }


    private void createFilesTree(MagitSingleFolder magitRootFolder, String path)
    {
        List<Item> items = magitRootFolder.getItems().getItem();
        File directory = new File(path);
        directory.mkdir();

        for( Item item : items)
        {
            String itemId = item.getId();
            switch (item.getType())
            {
                case "blob":
                    MagitBlob magitBlob = blobMap.get(itemId);
                    Utils.createNewFile(path + "/" + magitBlob.getName(), magitBlob.getContent());
                    break;
                case "folder":
                    MagitSingleFolder magitFolder = folderMap.get(itemId);
                    String folderPath = path +  "/" + magitFolder.getName();
                    createFilesTree(magitFolder, folderPath);
                    break;
            }
        }

        return;
    }

    private void checkMagitBlolb() throws XmlException
    {
        Set<String> ids = new HashSet<>();
        for(MagitBlob blob: magitBlobs.getMagitBlob()){
            if (!ids.add(blob.getId())){
                throw new XmlException("There is duplicate ID in blobs. id : " + blob.getId());
            }
            blobMap.put(blob.getId(), blob);
        }
    }

    private void checkMagitFodler() throws XmlException
    {
        Set<String> ids = new HashSet<>();
        for(MagitSingleFolder folder: magitFolders.getMagitSingleFolder())
        {
            if (!ids.add(folder.getId()))
            {
                throw new XmlException("There is duplicate ID in folders. id : " + folder.getId());
            }
            folderMap.put(folder.getId(), folder);
        }
    }

    private void checkMagitCommits() throws XmlException
    {
        Set<String> ids = new HashSet<>();
        for(MagitSingleCommit commit: magitCommits.getMagitSingleCommit())
        {
            if (!ids.add(commit.getId())){
                throw new XmlException("There is duplicate ID in Commits. id : " + commit.getId());
            }
            commitMap.put(commit.getId(), commit);
        }
    }

    private void checkFolderPointers() throws XmlException
    {
        for (MagitSingleFolder folder : folderMap.values())
        {
            List<Item> items = folder.getItems().getItem();
            for (Item item : items) {
                String type = item.getType();
                String id = item.getId();
                if(type.equals("blob"))
                {
                    if (blobMap.get(id) == null)
                    {
                        throw new XmlException("Lib.Folder id " + folder.getId() +
                                " points to non existing blob item (id : " + id + ")");
                    }
                }
                else if(type.equals("folder"))
                {
                    if(id.equals(folder.getId()))
                    {
                        throw new XmlException("Lib.Folder id " + id + " points to itself");
                    }
                    if(folderMap.get(id) == null)
                    {
                        throw new XmlException("Lib.Folder id " + folder.getId() +
                                " points to non existing folder item (id : " + id + ")");
                    }
                }
            }
        }
    }

    private void checkCommitsPointers() throws XmlException
    {
        for(MagitSingleCommit commit : commitMap.values())
        {
            String folderId = commit.getRootFolder().getId();
            MagitSingleFolder folder = folderMap.get(folderId);
            if(folder == null)
            {
                throw new XmlException("commit id " + commit.getId() +
                        " points to not existing folder (id : " + folderId + ")");
            }
            else
            {
                if(!folder.isIsRoot())
                {
                    throw new XmlException("commit id " + commit.getId() +
                            " points to not root folder (id : " + folderId + ")");
                }
            }

        }
    }

    private void checkBranchPointers() throws XmlException
    {
        for(MagitSingleBranch branch: magitBranches.getMagitSingleBranch())
        {
            if (branch.getPointedCommit().getId().equals("")) continue;

            if(commitMap.get(branch.getPointedCommit().getId()) == null)
            {
                throw new XmlException("branch " + branch.getName() +
                        " points to non existing commit id : " + branch.getPointedCommit().getId());
            }
        }
    }

    private void checkHeadPointer() throws XmlException
    {
        boolean isFound = false;
        String head = magitBranches.getHead();
        for(MagitSingleBranch branch : magitBranches.getMagitSingleBranch())
        {
            if(branch.getName().equals(head))
            {
                isFound = true;
            }
        }
        if (!isFound)
        {
            throw new XmlException("Lib.Head: " + head + " is not an existing branch");
        }
    }

    public String checkRepoLocation() throws XmlException
    {
        String repositoryLocation = magitRepository.getLocation();
        File repoLocation = new File(repositoryLocation);
        File magitRepoLocation = new File(repositoryLocation + "\\.magit");
        if (repoLocation.exists())
        {
            if (!magitRepoLocation.exists())
            {
                throw new XmlException("Cannot create new repository in " +
                        repositoryLocation + " ,already have existing files in this path");
            }
            else
            {
                return repositoryLocation;
            }
        }
        return null;
    }
}