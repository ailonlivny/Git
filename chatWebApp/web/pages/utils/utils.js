var GET_REPO_URL = buildUrlWithContextPath("getRepository");
var NUM_OF_BRANCH_URL = buildUrlWithContextPath("numOfBranches");
var LAST_COMMIT_URL = buildUrlWithContextPath("getLastCommit");

function createRepoElement(name, activeBranchName, numOfBranches, lastCommitDate, lastCommitMessage)
{
    var repository = document.createElement('li');
    repository.classList.add("rep");
    var content = document.createElement('div');
    content.classList.add("content");
    var repoName = createElement('h3', name);
    repoName.classList.add("header-name");
    var activeBranch = createElement('h4', "Active branch: " + activeBranchName);
    var branchesNumber = createElement('h4', "Num of branches: " + numOfBranches);
    var commitDate = createElement('h4', "Last commit created at: " + lastCommitDate);
    var commitMessage = createElement('h4', "Last commit message: " + lastCommitMessage);
    content.append(repoName, activeBranch, branchesNumber, commitDate, commitMessage);
    repository.append(content);
    return repository;
}

function createElement(elementTag, text) {
    var element = document.createElement(elementTag);
    element.innerText = text;
    return element;
}

function getActiveBranchName(repository, user)
{
    var repoName = "sos";
    $.ajax({
        dataType: 'json',
        data: {userName:user, repName: repository},
        url: GET_REPO_URL,
        method: 'post',
        async: false,
        success: function (r) {
            repoName = r.head.branch;
        }
    });
    return repoName;
}

function getNumOfBranches(repository, user) {
    var numOfBranches = 0;
    $.ajax({
        dataType: 'json',
        data: {userName:user,repName: repository},
        url: NUM_OF_BRANCH_URL,
        method: 'post',
        async: false,
        success: function (r) {
            numOfBranches = r;
        }
    });
    return numOfBranches;
}

function getLastCommit(repository, user) {
    var lastCommit = null;
    $.ajax({
        dataType: 'json',
        data: {userName:user, repName: repository},
        url: LAST_COMMIT_URL,
        method: 'post',
        async: false,
        success: function (r) {
            lastCommit = r;
        }
    });
    return lastCommit;
}

