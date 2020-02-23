var repoName;
var myUserName;
var SET_REPO_URL = buildUrlWithContextPath("setRepo");
var WC = buildUrlWithContextPath("pages/WC/WC.html");
var USER_SCREEN_URL = buildUrlWithContextPath("pages/userscreen/userscreen.html");
var USER_NAME_URL = buildUrlWithContextPath("userName");
var CHECK_OUT_URL = buildUrlWithContextPath("checkOut");
var GET_BRANCHES_URL = buildUrlWithContextPath("getAllBranches");
var GET_RTB_BRANCHES_URL = buildUrlWithContextPath("getAllRTBBranches");
var CREATE_BRANCH_URL = buildUrlWithContextPath("createBranch");
var DELET_BRACH_URL = buildUrlWithContextPath("deleteBranch");
var CREATE_PR_URL = buildUrlWithContextPath("createPR");
var GET_REMOTE_BRANCH_URL = buildUrlWithContextPath("getAllRemoteBranches");
var SHOW_ALL_BRANCH_URL = buildUrlWithContextPath("showAllBranches");
var GET_NOTFICATIONS_URL = buildUrlWithContextPath("getNotifications");
var GET_PR_URL = buildUrlWithContextPath("getPR");
var GET_COMMITS_URL = buildUrlWithContextPath("getCommits");
var PULL_URL = buildUrlWithContextPath("pull");
var PUSH_URL = buildUrlWithContextPath("push");
var GET_FILE_LIST_FROM_COMMIT_URL = buildUrlWithContextPath("getFileListFromCommit");
var GET_PR_FILE_LIST_URL = buildUrlWithContextPath("getPRFileList");
var DELET_PR_URL = buildUrlWithContextPath("deletePR");
var ACCEPT_PR_URL = buildUrlWithContextPath("acceptPR");
var DENY_PR_URL = buildUrlWithContextPath("denyPR");

$(function () {
    var url_string = window.location.href;
    var url = new URL(url_string);
    repoName = url.searchParams.get("repoName");

    $.ajax({
        method: 'POST',
        data: {repoName: repoName},
        url: SET_REPO_URL,
        dataType: 'json',
        success: function () {

        }
    });

    $.ajax({
        url: USER_NAME_URL,
        method: 'GET',
        success: function (userName) {
            myUserName = userName;
        },
        error: function (xhr, status, error) {
            alert(xhr.responseText);

        }
    });


    createBranchBtn();
    showBranches();
    createBackBtn();
    $("#branches").click(function (){showBranches()});
    // $("#commits").click(showCommits());
    // $("#pr").click(showPR());
    $("#messages").click(function (){showMessages()});

    $("#commits").click(function (){showCommits()});

    $("#repoName").text(repoName);

    $("#pull").click(function (){pull()});

    $("#push").click(function (){push()});

    $("#pr").click(function (){ShowPR()});

    $("#deleteBranch").click(function (){deleteBranch()});



    $("#WC").click(function ()
    {
        location.href = WC + '?repoName=' + repoName
    });



    $("#checkOutChoice").submit(function (event) {
        modal.style.display = "none";
        event.preventDefault();
        var x = document.getElementById("branchesOptions").selectedIndex;
        var y = document.getElementById("branchesOptions").options;
        var branchName = y[x].text;

        $.ajax({
            method: 'POST',
            data: {branchName: branchName},
            url: CHECK_OUT_URL,
            dataType: 'json',
            success: function (r) {
                console.log(r);
                if (r !== null) {
                    alert("There are open changes! please make commit");
                }
                location.href = location.href;
            },
            error: function (xhr, status, error) {
                location.href = location.href;
            }
        });

    });

    $("#PRChoice").submit(function (event) {
        PRmodal.style.display = "none";
        event.preventDefault();
        var x = document.getElementById("PROptions").selectedIndex;
        var y = document.getElementById("PROptions").options;
        var branchName = y[x].text;
        var Msg = $("#PRMsg").val();
        var xRemote = document.getElementById("PRRemoteOptions").selectedIndex;
        var yRemote = document.getElementById("PRRemoteOptions").options;
        var baseBranch =  yRemote[xRemote].text;

        $.ajax({
            method: 'POST',
            data: {branchName: branchName,Msg: Msg,baseBranch:baseBranch},
            url: CREATE_PR_URL,
            dataType: 'json',
            success: function (r)
            {
                GetAllRemoteBranches();
            },
            error: function (xhr, status, error) {
                alert(xhr.responseText);
            }
        });

    });


    $("#DeleteChoice").submit(function (event) {
        deleteModal.style.display = "none";
        event.preventDefault();
        var x2 = document.getElementById("DeleteOptions").selectedIndex;
        var y2 = document.getElementById("DeleteOptions").options;
        var branchName = y2[x2].text;

        $.ajax({
            method: 'POST',
            data: {branchName: branchName},
            url: DELET_BRACH_URL,
            dataType: 'json',
            success: function (r)
            {
                if (r !== null) {
                    alert("You cant delete the branch head!");
                }
                location.href = location.href;
            },
            error: function () {
                location.href = location.href;
            }
        });

    });
    GetAllBranchesDeleteList();
    GetAllBranches();
    GetAllRTBBranches();
    GetAllRemoteBranches();

    // Get the modal
    var modal = document.getElementById("ChackOutModal");

// Get the button that opens the modal
    var btn = document.getElementById("checkOut");

// Get the <span> element that closes the modal
    var span = document.getElementsByClassName("close")[0];

// When the user clicks the button, open the modal
    btn.onclick = function()
    {
        if($("#branchesOptions").children().length === 0)
        {
            window.alert("The head branch is the only branch!")
        }
        else
        {
            modal.style.display = "block";
        }

    }

// When the user clicks on <span> (x), close the modal
    span.onclick = function() {
        modal.style.display = "none";
    }

// When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }


    // Get the modal
    var PRmodal = document.getElementById("PRModal");

// Get the button that opens the modal
    var PRbtn = document.getElementById("PRBtn");

// Get the <span> element that closes the modal
    var PRspan = document.getElementsByClassName("close")[0];

// When the user clicks the button, open the modal
    PRbtn.onclick = function()
    {
        if($("#PROptions").children().length === 0)
        {
            window.alert("There is not remote repository connected!")
        }
        else
        {
            PRmodal.style.display = "block";
        }
    }

// When the user clicks on <span> (x), close the modal
    PRspan.onclick = function() {
        PRmodal.style.display = "none";
    }

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
        if (event.target == PRmodal) {
            PRmodal.style.display = "none";
        }
    }


    // Get the modal
    var deleteModal = document.getElementById("DeleteModal");

// Get the button that opens the modal
    var deleteBtn = document.getElementById("deleteBranch");

// Get the <span> element that closes the modal
    var deleteSpan = document.getElementsByClassName("close")[0];

// When the user clicks the button, open the modal
    deleteBtn.onclick = function()
    {
        if($("#DeleteOptions").children().length === 0)
        {
            window.alert("The head branch is the only branch!")
        }
        else
        {
            deleteModal.style.display = "block";
        }

    }

// When the user clicks on <span> (x), close the modal
    deleteSpan.onclick = function() {
        deleteModal.style.display = "none";
    }

// When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
        if (event.target == deleteModal) {
            deleteModal.style.display = "none";
        }
    }

})

function createBackBtn()
{
    $("#back").click(function (ev) {
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
    });
}

function createBranchBtn()
{
    $("#createBranch").click(function (ev) {
        var branchName = prompt("Please enter a branch:", "");
        if (branchName != null)
        {
            $.ajax({
                url: CREATE_BRANCH_URL,
                method: 'GET',
                data: {branchName: branchName},
                success: function (isCreateBranch)
                {
                    if(isCreateBranch)
                    {
                        window.alert("branch was created")
                        GetAllBranches();
                    }
                    else
                    {
                        window.alert("branch already exist")
                    }

                },
                error: function (xhr, status, error) {
                    alert(xhr.responseText);

                }
            });
        }});
}
function GetAllBranches()
{
    $.ajax({
        method: 'POST',
        url: GET_BRANCHES_URL,
        dataType: 'json',
        success: function (r)
        {
            $("#branchesOptions").empty();
            for (var i = 0; i < r.length; i++)
            {
                var optionElem = createElement("option",r[i])
                $("#branchesOptions").append(optionElem);

            }
        },
        error: function (xhr, status, error)
        {
            alert(xhr.responseText);
        }
    });
}

function GetAllBranchesDeleteList()
{
    $.ajax({
        method: 'POST',
        url: GET_BRANCHES_URL,
        dataType: 'json',
        success: function (r)
        {
            $("#DeleteOptions").empty();
            for (var i = 0; i < r.length; i++)
            {
                var optionElem = createElement("option",r[i])
                $("#DeleteOptions").append(optionElem);
            }
        },
        error: function (xhr, status, error)
        {
            alert(xhr.responseText);
        }
    });
}

function GetAllRTBBranches()
{
    $.ajax({
        method: 'GET',
        url: GET_RTB_BRANCHES_URL,
        dataType: 'json',
        success: function (r)
        {
            $("#PROptions").empty();
            for (var i = 0; i < r.length; i++)
            {
                var optionElem = createElement("option",r[i])
                $("#PROptions").append(optionElem);
            }
        },
        error: function (xhr, status, error)
        {
            alert(xhr.responseText);
        }
    });
}

function GetAllRemoteBranches()
{
    $.ajax({
        method: 'GET',
        url: GET_REMOTE_BRANCH_URL,
        dataType: 'json',
        success: function (r)
        {
            $("#PRRemoteOptions").empty();
            for (var i = 0; i < r.length; i++)
            {
                var optionElem = createElement("option",r[i])
                $("#PRRemoteOptions").append(optionElem);
            }
        },
        error: function (xhr, status, error)
        {
            alert(xhr.responseText);
        }
    });
}


function showBranches()
{
    $.ajax({
        method: 'Get',
        url: SHOW_ALL_BRANCH_URL,
        dataType: 'json',
        success: function (r)
        {
            $("#TitlesList").empty();
            for (var i = 0; i < r.length; i++)
            {
                var liElem = createElement("li",r[i])
                $("#TitlesList").append(liElem);
            }
        },
        error: function (xhr, status, error)
        {
            alert(xhr.responseText);
        }
    });
}

function ShowPR()
{
    $.ajax({
        method: 'GET',
        dataType: "json",
        url: GET_PR_URL,
        success: function (r)
        {
            $("#TitlesList").empty();
            for (var i = 0; i < r.length; i++)
            {
                var commitInfo = "User:" + r[i].user.name + ", Target Branch:" + r[i].targetBranch + ", Base Branch:" + r[i].baseBranch + ", Date:" + r[i].date + " Status:" +r[i].status ;
                var liElem = createElement('li',commitInfo);
                liElem.setAttribute("id",r[i].msg);
                liElem.setAttribute("status",r[i].status);
                liElem.setAttribute("userName",r[i].user.name);
                AppendButtonsToLiElem(liElem,r[i].status,r[i].user.name,r[i].msg);


                liElem.onclick = function (ev)
                {

                    if($(this).children(".ulElem").length === 0)
                    {
                        var ulElem = document.createElement("ul");
                        $(ulElem).addClass("ulElem");
                        createPRFileList(this.id,ulElem);
                        $(this).append(ulElem);
                    }
                    else
                    {
                        $(this).children().remove(".ulElem");
                    }

                }

                $("#TitlesList").append(liElem);
            }
        }
    });
}

function showMessages()
{
    $.ajax({
        method: 'GET',
        dataType: "json",
        url: GET_NOTFICATIONS_URL,
        success: function (r)
        {
            $("#TitlesList").empty();
            for (var i = 0; i < r.length; i++)
            {
                var notification = "From:" + r[i].addressed + ",  Message:" + r[i].message + ", Subject:" + r[i].subject + ", Date:" + r[i].date;
                var liElem = createElement('li',notification);
                $("#TitlesList").append(liElem);
            }
        }
    });
}

function showCommits()
{
    $.ajax({
        method: 'GET',
        dataType: "json",
        url: GET_COMMITS_URL,
        success: function (r)
        {
            $("#TitlesList").empty();
            for (var i = 0; i < r.length; i++)
            {
                var msg = r[i].msg.replace(/\0/g, '');
                var commitInfo = "SHA1:" + r[i].commitSHA1 + ",  Message:" + msg + ", User:" + r[i].userLastModified + ", Date:" + r[i].commitTime + " Branch list:" +r[i].branchList ;
                var liElem = createElement('li',commitInfo);
                liElem.setAttribute("id",r[i].commitSHA1);

                // $(this).find("li").append(ulElem)
                // $(liElem).append(ulElem);
                liElem.onclick = function (ev)
                {
                    // $(this).children().hide();
                    if($(this).children().length == 0)
                    {
                        var ulElem = document.createElement("ul");
                        createCommitFileList(this.id,ulElem);
                        $(this).append(ulElem);
                    }
                    else
                    {
                        $(this).children().remove();
                    }


                }

                $("#TitlesList").append(liElem);
            }
        }
    });
}

function pull()
{
    $.ajax({
        method: 'POST',
        dataType: "json",
        url: PULL_URL,
        success: function (isRemoteExist)
        {
            if(!isRemoteExist)
            {
                window.alert("There is not remote repository connected!")
            }
        }
    });
}

function push()
{
    $.ajax({
        method: 'POST',
        dataType: "json",
        url: PUSH_URL,
        success: function (isRemoteExist)
        {
            if(!isRemoteExist)
            {
                window.alert("There is not remote repository connected!")
            }
        }
    });
}

function createCommitFileList(SHA1,ulElem)
{
    $.ajax({
        method: 'GET',
        data: {SHA1: SHA1},
        dataType: "json",
        url: GET_FILE_LIST_FROM_COMMIT_URL,
        success: function (r)
        {
            for (var i = 0; i < r.length; i++)
            {
               var liElem = createElement("li",r[i]);
                $(ulElem).append(liElem);
            }
        }
    });
}

function createPRFileList(msg,ulElem)
{
    $.ajax({
        method: 'GET',
        data: {msg: msg},
        dataType: "json",
        url: GET_PR_FILE_LIST_URL,
        success: function (r)
        {
            for (var i = 0; i < r.length; i++)
            {
                var liElem = createElement("li",r[i].fullPath + " " + r[i].status);
                $(ulElem).append(liElem);
            }
        }
    });

}

function AppendButtonsToLiElem(liElem,status,userName,key)
{
    var deleteBtn = createElement("button","Delete");
    deleteBtn.setAttribute("id",key);
    deleteBtn.onclick = function (ev)
    {
        $.ajax({
            method: 'POST',
            data: {key:this.id},
            dataType: "json",
            url: DELET_PR_URL,
            success: function ()
            {
                location.href = location.href;
            },
            error: function (xhr, status, error)
            {
                location.href = location.href;
            }
        });
    }

    $(liElem).append(deleteBtn)
    if(userName !== myUserName && status == "OPEN")
    {
        var accecptBtn = createElement("button","Accept");
        accecptBtn.setAttribute("id",key);
        accecptBtn.onclick = function (ev)
        {
            $.ajax({
                method: 'POST',
                data: {key:this.id},
                dataType: "json",
                url: ACCEPT_PR_URL,
                success: function ()
                {
                    location.href = location.href;
                },
                error: function (xhr, status, error)
                {
                    location.href = location.href;
                }
            });
        }
        var denyBtn = createElement("button","Deny");
        denyBtn.setAttribute("id",key);
        denyBtn.onclick = function (ev)
        {
            var denyMsg = prompt("Please enter deniel reason", "");
            $.ajax({
                method: 'POST',
                data: {key:this.id,denyMsg:denyMsg},
                dataType: "json",
                url: DENY_PR_URL,
                success: function ()
                {
                    location.href = location.href;
                },
                error: function (xhr, status, error)
                {
                    location.href = location.href;
                }
            });
        }
        $(liElem).append(accecptBtn,denyBtn)
    }
}

