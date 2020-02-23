var repoName;
var filePath;
var GET_WC_URL = buildUrlWithContextPath("getWC");
var LOGIN_URL = buildUrlWithContextPath("pages/login/loginPage.html");
var USER_SCREEN_URL = buildUrlWithContextPath("pages/userscreen/userscreen.html");
var SAVE_FILE_URL = buildUrlWithContextPath("saveFile");
var ADD_FILE_URL = buildUrlWithContextPath("addFile");
var ADD_FOLDER_URL = buildUrlWithContextPath("addFolder");
var REPO_PAGE_URL = buildUrlWithContextPath("pages/repositoryPage/repositoryPage.html");
var COMMIT_URL = buildUrlWithContextPath("makeCommit");
var DELET_FILE_URL = buildUrlWithContextPath("deleteFile");
var GET_FILE_CONT_URL = buildUrlWithContextPath("getFileContent");


$(function () {
    var url_string = window.location.href;
    var url = new URL(url_string);
    repoName = url.searchParams.get("repoName");

    $("#EditSection").hide();
    $("#AddFileSection").hide();
    $("#repoName").innerText = "WC " + repoName + ":";
    var back = createBackBtn();
    var commit = createCommitBtn();
    $("#HeadButtons").append(back,commit);
    $.ajax({
        method: 'GET',
        data: {repoName: repoName},
        url: GET_WC_URL,
        dataType: 'json',
        success: function (r) {
            for(var i=0;i<r.length;i++)
            {
                if(r[i].typeItem == "Folder")
                {
                    createRowFolderTable(r[i]);
                }
                else
                {
                    createRowFileTable(r[i]);
                }

            }
        },
        error: function (xhr, status, error) {
            alert(xhr.responseText);
        }
    });

    $("#FileContent").submit(function (event) {
        var content = $("#ContentTextArea").val();
        $.ajax({
            method: 'POST',
            data: {content:content,filePath: filePath},
            url: SAVE_FILE_URL,
            success: function (r)
            {
                location.href = location.href;
            },
            error: function (xhr, status, error)
            {

            }
        });})

    $("#AddFile").submit(function (event) {
        var content = $("#AddFileTextArea").val();
        var fileName = $('#FileName').val();
        $.ajax({
            method: 'POST',
            data: {content:content,filePath:filePath, fileName:fileName},
            url: ADD_FILE_URL,
            success: function (r)
            {
                location.href = location.href;
            },
            error: function (xhr, status, error)
            {
                location.href = location.href;
            }
        });})

    $("#AddFolder").submit(function (event) {
        var folderName = $('#FolderName').val();
        $.ajax({
            method: 'POST',
            data: {filePath:filePath, folderName:folderName},
            url: ADD_FOLDER_URL,
            success: function (r)
            {
                location.href = location.href;
            },
            error: function (xhr, status, error)
            {
                location.href = location.href;
            }
        });})


})

function createRowFolderTable(item)
{
    var trElem = document.createElement('tr');
    var tdPath = createElement('td',item.fullPath);
    var tdType = createElement('td',item.typeItem);
    var tdDelete = createElement('td',"Delete");
    tdDelete.classList.add("Button");
    tdDelete.setAttribute("id",item.fullPath);
    tdDelete.onclick = function (ev)
    {
        deleteFile(ev.target.id);
    }
    var tdAdd = createElement('td',"Add");
    tdAdd.classList.add("Button");
    tdAdd.setAttribute("id",item.fullPath);
    tdAdd.onclick = function (ev)
    {
        $("#AddFileSection").show();
        $("#EditSection").hide();
        filePath = ev.target.id;
    }
    var tdContent = createElement('td',"");
    trElem.append(tdPath,tdType,tdDelete,tdAdd,tdContent);
    $("#WCTable").append(trElem);
}

function createRowFileTable(item)
{
    var trElem = document.createElement('tr');
    var tdPath = createElement('td',item.fullPath);
    var tdType = createElement('td',item.typeItem);
    var tdDelete = createElement('td',"Delete");
    tdDelete.classList.add("Button");
    tdDelete.setAttribute("id",item.fullPath);
    tdDelete.onclick = function (ev)
    {
        deleteFile(ev.target.id);
    }
    var tdAdd = createElement('td',"");
    var tdContent = createElement('td',"Content");
    tdContent.classList.add("Button");
    tdContent.setAttribute("id",item.fullPath);
    tdContent.onclick = function (ev)
    {
        $("#EditSection").show();
        $("#AddFileSection").hide();
        showContent(ev.target.id);
        filePath = ev.target.id;
    }
    debugger;
    trElem.append(tdPath,tdType,tdDelete,tdAdd,tdContent);
    $("#WCTable").append(trElem);
}

function showContent(itemPath)
{
    var path = itemPath;

    $.ajax({
        method: 'GET',
        data: {path: path},
        url: GET_FILE_CONT_URL,
        dataType: 'json',
        success: function (r) {
            $("#ContentTextArea").empty();
            $("#ContentTextArea").append(r)
        },
        error: function (xhr, status, error) {
            alert(xhr.responseText);
        }
    });

}

function deleteFile(filePath)
{
    $.ajax({
        method: 'POST',
        data: {filePath: filePath},
        url: DELET_FILE_URL,
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

function createBackBtn()
{
    var btn = document.createElement('button');
    btn.innerHTML = "Back";
    btn.onclick = function (ev)
    {
        location.href = REPO_PAGE_URL + "?repoName=" + repoName;
    }
    return btn;
}

function createCommitBtn()
{
    var btn = document.createElement('button');
    btn.innerHTML = "Commit";
    btn.onclick = function (ev)
    {
        var commitMsg = prompt("Please enter the commit message", "");
        $.ajax({
            method: 'POST',
            data: {commitMsg: commitMsg},
            url: COMMIT_URL,
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
    return btn;
}