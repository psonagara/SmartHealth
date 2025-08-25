$(function () {

    const token = localStorage.getItem("aSessionId");
    const role = localStorage.getItem("role");

    if (!token || !role || role !== 'admin') {
        window.location.replace("login.html");
    }

    $("#sidebar").load('sidebar.html', function() {
        let currentPage = window.location.pathname.split("/").pop();
        if (!currentPage) {
            currentPage = "index.html";
        }
        $(".nav-item").each(function () {

            if ($(this).data("page") === currentPage) {
                $(this).addClass("active");
            }
        });
    });
    $("#main-header").load('main-header.html', function () {
        $.ajax({
            url: GET_PICTURE_NAME_PATH + "/" + role,
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            },
            success: function (response) {
                const profilePicUrl = LOAD_PICTURE_PATH + "/" + response.content + "?role=" + role;
                $("#loginOrProfileListItem").html(`
						<a class="dropdown-toggle profile-pic" data-toggle="dropdown" href="#" aria-expanded="false"> 
							<img src="${profilePicUrl}" alt="user-img" width="36" class="img-circle" style=" height: 2rem; width: 2rem;"></a>
						<ul class="dropdown-menu dropdown-user">
							<a class="dropdown-item" href="profile.html"><i class="la la-user"></i> My Profile</a>
							<a class="dropdown-item" href="password.html"><i class="la la-lock"></i> Change Password</a>
							<div class="dropdown-divider"></div>
							<a class="dropdown-item" href="#"><i class="ti-settings"></i> Account Setting</a>
							<div class="dropdown-divider"></div>
							<a class="dropdown-item" id="logout-link" href="#"><i class="la la-power-off"></i> Logout</a>
						</ul>
					`);
                $("#logout-link").click(function () {
                    localStorage.removeItem("aSessionId");
                    localStorage.removeItem("role");
                    window.location.replace("login.html");
                });
            },
            error: function (xhr) {
                console.log(xhr);
            }
        });
    });
});