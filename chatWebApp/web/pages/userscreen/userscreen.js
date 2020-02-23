var userName;
var LOG_OUT_URL = buildUrlWithContextPath("LogOut");
var LOGIN_URL = buildUrlWithContextPath("pages/login/loginPage.html");
var USER_SCREEN_URL = buildUrlWithContextPath("pages/userscreen/userscreen.html");
var USER_NAME_URL = buildUrlWithContextPath("userName");
var USER_SYSTEM_URL = buildUrlWithContextPath("usersInSystem");
var UPLOAD_REPO_URL = buildUrlWithContextPath("uploadRepo");
var REPO_PAGE_URL = buildUrlWithContextPath("pages/repositoryPage/repositoryPage.html");
var USER_IN_SYSTEM_URL = buildUrlWithContextPath("pages/userInSystemInfo/userInSystemInfo.html");
var GET_NOTIFICATIONS_URL = buildUrlWithContextPath("getNotifications");
var ALL_MY_REPO_URL = buildUrlWithContextPath("allMyRepos");

$(function() {
    setInterval(getNotifications, 3000);
    getNotifications();
    loadAllRepo();
    $("#logOutBotton").click(function ()
    {
        $.ajax({
            method: 'POST',
            url: LOG_OUT_URL,
            success: function ()
            {
                location.href = LOGIN_URL;
            }
        });
    })

    $.ajax({
        method:'GET',
        url: USER_NAME_URL,
        success: function(r) {
            $('#welcome').text("welcome " + r);
            userName = r;
        }
    })

    $.ajax({
        method: 'GET',
        url: USER_SYSTEM_URL,
        success: function (r)
        {
            $("#usersList").empty();
            for (var i = 0; i < r.length; i++)
            {
                var userName = r[i];
                createUserElement(userName);
            }
        }
    });



    $("#uploadForm").submit(function (event) {
        event.preventDefault();
        var file = this[0].files[0];
        var formData = new FormData();
        formData.append('theFile', file);
        $.ajax({
            method: 'POST',
            data: formData,
            url: UPLOAD_REPO_URL,
            processData: false,
            contentType: false,
            timeout: 4000,
            success: function (r) {
                var ActiveBranchName = getActiveBranchName(r);
                var NumOfBranches = getNumOfBranches(r);
                var Commit = getLastCommit(r);
                Commit.msg = Commit.msg.replace(/\0/g, '');
                var repository = createRepoElement(r, ActiveBranchName, NumOfBranches, Commit.commitTime,Commit.msg);
                repository.onclick = function (ev)
                {
                    location.href = REPO_PAGE_URL + "?repoName=" +  r
                }
                $("#repoList").append(repository);
            },
            error: function (xhr, status, error) {
            }
        });
        return false;
    });

});

function createUserElement(name)
{
    var liElem = document.createElement("li");
    liElem.innerText = name;
    liElem.classList.add("userName");
    liElem.onclick = function (ev)
    {
        location.href = USER_IN_SYSTEM_URL + "?user=" + name
    }
    $("#usersList").append(liElem);
}

function getNotifications()
{
    $.ajax({
        method: 'GET',
        dataType: "json",
        url: GET_NOTIFICATIONS_URL,
        success: function (r)
        {
            for (var i = 0; i < r.length; i++)
            {
                var notification = "From:" + r[i].addressed + ",  Message:" + r[i].message + ", Subject:" + r[i].subject + ", Date:" + r[i].date;
                var liElem = createElement('li',notification);
                $("#notificationsList").append(liElem);
            }
        }
    });
}

function loadAllRepo()
{
    $.ajax({
        url: ALL_MY_REPO_URL,
        method: 'GET',
        data: {userName: userName},
        success: function (r)
        {
            $("#repoList").empty();
            for (var i = 0; i < r.length; i++)
            {
                var ActiveBranchName = getActiveBranchName(r[i], userName);
                var NumOfBranches = getNumOfBranches(r[i], userName);
                var Commit = getLastCommit(r[i], userName);
                Commit.msg = Commit.msg.replace(/\0/g, '');
                var repository = createRepoElement(r[i], ActiveBranchName, NumOfBranches, Commit.commitTime,Commit.msg);
                repository.setAttribute("repoName", r[i]);
                repository.onclick = function (ev)
                {
                    location.href = REPO_PAGE_URL + "?repoName=" +  $(this).attr("repoName")
                }

                $("#repoList").append(repository);
            }
        }
    })
}




