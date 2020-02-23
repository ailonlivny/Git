var USER_NAME_URL = buildUrlWithContextPath("userName");
var LOGIN_URL = buildUrlWithContextPath("loginPage");
var USER_SCREEN_URL = buildUrlWithContextPath("pages/userscreen/userscreen.html");

$(function () {
    var userName=$("#userName");

    $.ajax({
        method: 'GET',
        url: USER_NAME_URL,
        success: function (r)
        {
            if(r!="null") {
                location.href = USER_SCREEN_URL;
            }
        }
    });

    $("#logInBtn").click(function () {
        $.ajax({
            data:userName,
            url: LOGIN_URL,
            success:function () {
                $("#s").text("you logged in");
                $("#s").css("color","black");
                location.href=USER_SCREEN_URL;
            },
            error: function (xhr, status, error) {
                if(xhr.status===403){
                    $("#s").text("name already exist!");
                }
                else if(xhr.status===402){
                    $("#s").text("empty user name!");
                }
                else{
                    $("#s").text("failed to get result from server")
                }
                $("#s").css("color","red")
            }
        });
    })
});
