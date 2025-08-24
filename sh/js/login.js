$(document).ready(function () {

    const token = localStorage.getItem("aSessionId");
    const role = localStorage.getItem("role");

    if (token && role) {
        window.location.replace("index.html");
    }

    function showToast(message) {
        const snackbar = $("#snackbar");
        snackbar.text(message).addClass("show");
        setTimeout(() => snackbar.removeClass("show"), 3000);
    }

    $("#loginForm").submit(function (e) {
        e.preventDefault(); // prevent form from submitting normally
        $("#loaderOverlay").show(); // Show loader
        $("#loginFormSubmitButton").prop("disabled", true);
        // Clear previous errors
        $(".error").text("");

        // Get field values
        const role = $("#roleLogin").val();
        const user = $("#user").val().trim();
        const pass = $("#pass").val();

        let isValid = true;

        // Validate Name
        if (user === "") {
            $("#userError").text("Email/Phone is required.");
            isValid = false;
        }
        if (pass === "") {
            $("#passError").text("Password is required.");
            isValid = false;
        }

        if (isValid) {
            const payload = {
                user: user,
                password: pass
            };

            $.ajax({
                url: `${LOGIN_PATH}/${role}`,
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(payload),
                success: function (response) {
                    showToast(response.message);

                    const expiryMinutes = 840;
                    const expiryTime = new Date().getTime() + expiryMinutes * 60 * 1000;
                    localStorage.setItem("aSessionId", response.content.token);
                    localStorage.setItem("tokenExpiry", expiryTime);
                    localStorage.setItem("role", response.content.role);

                    setTimeout(() => window.location.replace("index.html"), 1500);
                    $("#loaderOverlay").hide(); // Hide loader
                    $("#loginFormSubmitButton").prop("disabled", false);
                },
                error: function (xhr) {
                    let msg = "Login Failed, Please Try again letter";
                    if (xhr && xhr.responseJSON && xhr.responseText) {
                        if (xhr.responseJSON.message) {
                            msg = xhr.responseJSON.message;
                        } else if (xhr.responseText) {
                            msg = xhr.responseText;
                        }
                    }
                    showToast(msg);
                    $("#loginForm")[0].reset();
                    $("#loaderOverlay").hide();
                    $("#loginFormSubmitButton").prop("disabled", false);
                }
            });
        } else {
            $("#loaderOverlay").hide(); // Hide loader
            $("#loginFormSubmitButton").prop("disabled", false);
        }
    });

    $("#registerForm").submit(function (e) {
        e.preventDefault(); // prevent form from submitting normally

        $("#loaderOverlay").show(); // Show loader
        $("#registerSubmitButton").prop("disabled", true);
        // Clear previous errors
        $(".error").text("");

        // Get field values
        const role = $("#roleRegister").val();
        const name = $("#name").val().trim();
        const email = $("#email").val().trim();
        const phone = $("#phone").val().trim();
        const password = $("#password").val();

        let isValid = true;

        // Validate Name
        if (name === "") {
            $("#nameError").text("Name is required.");
            isValid = false;
        }

        // Validate Email
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (email === "") {
            $("#emailError").text("Email is required.");
            isValid = false;
        } else if (!emailRegex.test(email)) {
            $("#emailError").text("Enter Valid Email.");
            isValid = false;
        }

        // Validate Phone (must be 10 digits)
        const phoneRegex = /^[0-9]{10}$/;
        if (phone === "") {
            $("#phoneError").text("Phone number is required.");
            isValid = false;
        } else if (!phoneRegex.test(phone)) {
            $("#phoneError").text("Phone number must be 10 digits.");
            isValid = false;
        }

        // Validate Password
        if (password === "") {
            $("#passwordError").text("Password is required.");
            isValid = false;
        } else if (password.length < 5) {
            $("#passwordError").text("Password must be at least 5 characters.");
            isValid = false;
        }

        if (isValid) {
            const payload = {
                name: name,
                email: email,
                phone: phone,
                password: password
            };

            $.ajax({
                url: `${REGISTER_PATH}/${role}`,
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(payload),
                success: function (response) {
                    showToast(response.message);
                    $("#registerForm")[0].reset(); // reset form
                    $("#loaderOverlay").hide(); // Hide loader
                    $("#registerSubmitButton").prop("disabled", false);
                },
                error: function (xhr) {
                    let msg = "Registration Failed, Please Try again letter";
                    if (xhr && xhr.responseJSON && xhr.responseText) {
                        if (xhr.responseJSON.message) {
                            msg = xhr.responseJSON.message;
                        } else if (xhr.responseText) {
                            msg = xhr.responseText;
                        }
                    }
                    showToast(msg);
                    $("#registerForm")[0].reset();
                    $("#loaderOverlay").hide(); // Hide loader
                    $("#registerSubmitButton").prop("disabled", false);
                }
            });
        } else {
            $("#loaderOverlay").hide(); // Hide loader
            $("#registerSubmitButton").prop("disabled", false);
        }
    });
});