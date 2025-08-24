$(function () {

    $("#header").load("header.html", function () {

        $("#app_name").text(APP_NAME);
        const token = localStorage.getItem("aSessionId");
        const role = localStorage.getItem("role");
        if (token) {
            $.ajax({
                url: GET_PICTURE_NAME_PATH + "/" + role,
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                },
                success: function (response) {
                    const profilePicUrl = LOAD_PICTURE_PATH + "/" + response.content + "?role=" + role;
                    const appHtml = role === 'doctor' ? '<a href="view-app-doctor.html" class="dropdown-item">Appointments</a>' :
                                                        '<a href="view-app-patient.html" class="dropdown-item">Appointments</a>';
                    const leaveHtml = role === 'doctor' ? '<a href="leave.html" class="dropdown-item">My Leaves</a>' : "";
                    $("#nav-auth-section").html(`
                    <div class="nav-item dropdown">
                        <img src="${profilePicUrl}" id="profilePreviewSmall" class="nav-link dropdown-toggle profile-pic" data-bs-toggle="dropdown" alt="Profile Picture" style="
                            height: 6rem;
                            width: 3.2rem;
                        "></img>
                        <div class="dropdown-menu m-0">
                            <a href="profile.html" class="dropdown-item">Profile</a>
                            ${appHtml}
                            ${leaveHtml}
                            <a id="logout-link" class="dropdown-item">Logout</a>
                        </div>
                    </div>
                    <style>
                        img.profile-pic {
                            height: 100px;
                            width: 40px;
                            border-radius: 60%;
                            object-fit: cover;
                            cursor: pointer;
                            margin-left: 50px;
                        }
                    </style>
                    `);
                    $("#logout-link").click(function () {
                        localStorage.removeItem("aSessionId");
                        localStorage.removeItem("role");
                        localStorage.removeItem("tokenExpiry");
                        window.location.replace("login.html");
                    });
                },
                error: function (xhr) {
                    console.log(xhr);
                }
            });
        } else {
            // Show login/signup if not logged in
            $("#nav-auth-section").html(`
                <a href="login.html" class="nav-item nav-link" data-page="login.html">Login/SignUp</a>
            `);
        }
        
        $("#nav_slots").show();
        if (role === 'doctor') {

            $("#nav_slots").attr('href', 'availability.html');
            $("#nav_slots").attr('data-page', 'availability.html');

            $("#appointments_nav_slots").attr('href', 'view-app-doctor.html');
            $("#appointments_nav_slots").attr('data-page', 'view-app-doctor.html');

        } else if (role === 'patient') {

            $("#nav_slots").attr('href', 'find-doctor.html');
            $("#nav_slots").attr('data-page', 'find-doctor.html');

            $("#appointments_nav_slots").attr('href', 'view-app-patient.html');
            $("#appointments_nav_slots").attr('data-page', 'view-app-patient.html');

        } else {

            $("#nav_slots").hide();
            $("#appointments_nav_slots").hide();

        }
        
        let currentPage = window.location.pathname.split("/").pop();
        if (!currentPage) {
            currentPage = "index.html";
        }
        $(".nav-link").each(function () {

            if ($(this).data("page") === currentPage) {
                $(this).addClass("active");
            }
        });
        $(".dropdown-menu .dropdown-item").each(function () {
            const page = $(this).attr("href");
            if (page && page === currentPage) {
                $(this).addClass("active"); // Active on dropdown item

                // Add active to parent dropdown toggle link
                $(this).closest(".nav-item.dropdown").find(".dropdown-toggle").addClass("active");
            }
        });
    });

    $("#footer").load("footer.html");
});