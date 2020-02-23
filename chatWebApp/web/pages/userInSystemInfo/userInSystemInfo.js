var user;
var myUserName;
var USER_NAME_URL = buildUrlWithContextPath("userName");
var ALL_MY_REPO_URL = buildUrlWithContextPath("allMyRepos");
var USER_SCREEN_URL = buildUrlWithContextPath("pages/userscreen/userscreen.html");
var FORK_URL = buildUrlWithContextPath("fork");

$(function () {
    var url_string = window.location.href;
    var url = new URL(url_string);
    user = url.searchParams.get("user");

    $('#userRepositories').text(user + "/Repositories");

    $.ajax({
        url: ALL_MY_REPO_URL,
        method: 'GET',
        data: {userName: user},
        dataType: 'json',
        success: function (r) {
            for (var i = 0; i < r.length; i++)
            {
                var ActiveBranchName = getActiveBranchName(r[i], user);
                var NumOfBranches = getNumOfBranches(r[i], user);
                var Commit = getLastCommit(r[i], user);
                Commit.msg = Commit.msg.replace(/\0/g, '');
                var repository = createRepoElement(r[i], ActiveBranchName, NumOfBranches, Commit.commitTime,Commit.msg);
                repository.setAttribute("repoName", r[i]);
                createForkBtn(repository);
                $("#repoList").append(repository);
            }
            var back = createBackBtn();
            $("#repos").append(back);
        }
    })
})

function createBackBtn()
{
    var btn = document.createElement('button');
    btn.innerHTML = "Back";
    btn.onclick = function (ev) {
        $.ajax({
            url: USER_NAME_URL,
            method: 'GET',
            success: function (userName) {
                myUserName = userName;
                location.href = USER_SCREEN_URL + '?user=' + myUserName;
            },
            error: function (xhr, status, error) {
                alert(xhr.responseText);

            }
        });
    };
    return btn;
}

function createForkBtn(repo) {
    var button = document.createElement("button");
    button.classList.add("button");
    button.innerText = "Fork";
    repo.append(button);


    button.onclick = function () {
        $.ajax({
            url: FORK_URL,
            method: 'post',
            data: {userNameToFork: user, repositoryName: repo.getAttribute("repoName")},
            success: function () {
                location.href = USER_SCREEN_URL;
            }
        })
    }
}