$(document).ready(function () {

    $('#doctor_degrees').select2({
        placeholder: "Select Degrees",
    });
    $('#doctor_department').select2({
        placeholder: "Select Departments",
    });
    $('#doctor_specializations').select2({
        placeholder: "Select Specializations",
    });

    function showToastAndRedirectToLogin(message, delay = 2000) {
        const snackbar = $("#snackbar");
        snackbar.text(message).addClass("show");

        // Remove the toast after 2 seconds
        setTimeout(() => {
            snackbar.removeClass("show");
            // Then redirect to login
            window.location.replace("login.html");
        }, delay);
    }

    function showToast(message) {
        const snackbar = $("#snackbar");
        snackbar.text(message).addClass("show");
        setTimeout(() => {
            snackbar.removeClass("show");
        }, 3000);
    }

    function validateUser() {
        const token = localStorage.getItem("aSessionId");
        const role = localStorage.getItem("role");
        const expiry = localStorage.getItem("tokenExpiry");
        const now = new Date().getTime();

        if (!token || !role) {
            showToastAndRedirectToLogin("Unauthorized! Please login first.");
        } else if (!expiry || now > expiry) {
            localStorage.removeItem("aSessionId");
            localStorage.removeItem("role");
            localStorage.removeItem("tokenExpiry");
            showToastAndRedirectToLogin("Session expired. Please login again.");
        } else {
            return true;
        }
        return false;
    }

    if (validateUser()) {
        const role = localStorage.getItem("role");
        const token = localStorage.getItem("aSessionId");

        if (role === 'patient') {
            $("#profile-card-patient").show();
            $("#profile-card-doctor").hide();
            $('#loaderOverlay').show();

            $.ajax({
                url: `${VIEW_PROFILE_PATH}/${role}`,
                method: 'GET',
                headers: {
                    'Authorization': "Bearer " + token
                },
                success: function (response) {
                    $('#loaderOverlay').hide();
                    $('#name').val(response.content.name);
                    $('#email').val(response.content.email);
                    $('#phone').val(response.content.phone);
                    $('#dob').val(response.content.dob);
                    $('#gender').val(response.content.gender);
                    $('#height').val(response.content.height);
                    $('#weight').val(response.content.weight);
                    if (response.content.profilePicPath) {
                        $('#profilePreview').attr('src', `${LOAD_PICTURE_PATH}/${response.content.profilePicPath}?role=${role}`);
                    }
                },
                error: function (xhr) {
                    let msg = "Failed to load profile.";
                    if (xhr && xhr.responseJSON && xhr.responseText) {
                        if (xhr.responseJSON.message) {
                            msg = xhr.responseJSON.message;
                        } else if (xhr.responseText) {
                            msg = xhr.responseText;
                        }
                    }
                    showToast(msg);
                    $('#loaderOverlay').hide();
                    window.location.href = document.referrer || 'index.html';
                }
            });
        } else if (role === "doctor") {
            $("#profile-card-doctor").show();
            $("#profile-card-patient").hide();
            $('#loaderOverlay').show();

            $.ajax({
                url: `${VIEW_PROFILE_PATH}/${role}`,
                method: 'GET',
                headers: {
                    'Authorization': "Bearer " + token
                },
                success: function (response) {
                    $('#loaderOverlay').hide();
                    $('#doctor_name').val(response.content.name);
                    $('#doctor_email').val(response.content.email);
                    $('#doctor_phone').val(response.content.phone);
                    $('#doctor_dob').val(response.content.dob);
                    $('#doctor_gender').val(response.content.gender);
                    $('#doctor_experience').val(response.content.yearOfExperience);
                    $('#doctor_address').val(response.content.address);
                    $('#doctor_registrationNumber').val(response.content.registrationNumber);
                    $.get(DOCTOR_DEGREE_PATH, function (data) {
                        data.forEach(deg => $('#doctor_degrees').append(`<option value='${deg.id}' data-name='${deg.name}'>${deg.name}</option>`));
                        $('#doctor_degrees').val(response.content.degrees.map(d => d.id)).trigger('change');
                    });
                    $.get(DOCTOR_DEPARTMENT_PATH, function (data) {
                        data.forEach(dep => $('#doctor_department').append(`<option value='${dep.id}' data-name='${dep.name}'>${dep.name}</option>`));
                        $('#doctor_department').val(response.content.departments.map(d => d.id)).trigger('change');
                    });
                    $.get(DOCTOR_SPECIALIZATION_PATH, function (data) {
                        data.forEach(spec => $('#doctor_specializations').append(`<option value='${spec.id}' data-name='${spec.name}'>${spec.name}</option>`));
                        $('#doctor_specializations').val(response.content.specializations.map(s => s.id)).trigger('change');
                    });
                    if (response.content.profilePicPath) {
                        $('#doctor_profilePreview').attr('src', `${LOAD_PICTURE_PATH}/${response.content.profilePicPath}?role=${role}`);
                    }
                },
                error: function (xhr) {
                    $('#loaderOverlay').hide();
                    const msg = xhr.responseJSON?.message || "Failed to load profile.";
                    alert(msg);
                    window.location.href = document.referrer || 'index.html';
                }
            });
        }
        $('#loaderOverlay').hide();
    }

    $("#profilePreview").click(function () {
        const src = $(this).attr("src");
        $("#modalImage").attr("src", src);
        $("#imageModal").fadeIn();
    });
    $("#doctor_profilePreview").click(function () {
        const src = $(this).attr("src");
        $("#modalImage").attr("src", src);
        $("#imageModal").fadeIn();
    });
    $("#modalCloseBtn").click(function () {
        closeModal();
    });
    // Close modal on background or close button
    function closeModal() {
        $("#imageModal").fadeOut();
    }
    $(window).on("click", function (e) {
        if ($(e.target).is("#imageModal")) {
            closeModal();
        }
    });


    $("#updateProfile").click(function () {
        if (validateUser()) {
            updateProfile();
        }
    });
    $("#doctor_update_button").click(function () {
        if (validateUser()) {
            updateProfile();
        }
    });
    $("#changePassword").click(function () {
        window.location.href = "password.html";
    });
    $("#doctor_change_password_button").click(function () {
        window.location.replace("password.html");
    });
    $("#profilePic").on("change", function () {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                $("#profilePreview").attr("src", e.target.result);
            };
            reader.readAsDataURL(file);
        }

        if (validateUser()) {
            uploadProfilePic(file);
        }
    });
    $("#doctor_profilePic").on("change", function () {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                $("#doctor_profilePreview").attr("src", e.target.result);
            };
            reader.readAsDataURL(file);
        }

        // Auto-upload after choosing
        if (validateUser()) {
            uploadProfilePic(file);
        }
    });
    function updateProfile() {
        const token = localStorage.getItem("aSessionId");
        const role = localStorage.getItem("role");
        $('#loaderOverlay').show();

        $('.error').text('');

        if (role === "patient") {

            const name = $('#name').val().trim();
            const email = $('#email').val().trim();
            const phone = $('#phone').val().trim();
            const dob = $('#dob').val();
            const gender = $('#gender').val();
            const height = $('#height').val();
            const weight = $('#weight').val();

            let isValid = true;
            if (!name) {
                $('#nameError').text('Name is required');
                isValid = false;
            }
            if (!dob) {
                $('#dobError').text('Date of Birth is required');
                isValid = false;
            }
            if (!gender) {
                $('#genderError').text('Please select gender');
                isValid = false;
            }
            if (!height) {
                $('#heightError').text('Height is required');
                isValid = false;
            }
            if (!weight) {
                $('#weightError').text('weight is required');
                isValid = false;
            }

            if (!isValid) {
                $('#loaderOverlay').hide();
                return;
            }
            const payload = {
                name, email, phone, dob, gender, height, weight
            };
            $.ajax({
                url: `${UPDATE_PROFILE_PATH}/${role}`,
                type: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify(payload),
                headers: {
                    'Authorization': "Bearer " + token
                },
                success: function (response) {
                    $('#loaderOverlay').hide();
                    showToast(response.message);
                },
                error: function (xhr) {
                    let msg = "Failed to update profile";
                    if (xhr && xhr.responseJSON && xhr.responseText) {
                        if (xhr.responseJSON.message) {
                            msg = xhr.responseJSON.message;
                        } else if (xhr.responseText) {
                            msg = xhr.responseText;
                        }
                    }
                    $('#loaderOverlay').hide();
                    showToast(msg);
                }
            });
        } else if (role === "doctor") {
            $('#loaderOverlay').show();
            const name = $('#doctor_name').val().trim();
            const email = $('#doctor_email').val().trim();
            const phone = $('#doctor_phone').val().trim();
            const dob = $('#doctor_dob').val();
            const gender = $('#doctor_gender').val();
            const yearOfExperience = $('#doctor_experience').val();
            const degrees = getSelectedObjects("doctor_degrees");
            const departments = getSelectedObjects("doctor_department");
            const specializations = getSelectedObjects("doctor_specializations");
            const address = $('#doctor_address').val();
            const registrationNumber = $('#doctor_registrationNumber').val();


            let isValid = true;
            if (!name) {
                $('#doctor_nameError').text('Name is required');
                isValid = false;
            }
            if (!dob) {
                $('#doctor_dobError').text('Date of Birth is required');
                isValid = false;
            }
            if (!gender) {
                $('#doctor_genderError').text('Please select gender');
                isValid = false;
            }
            if (!yearOfExperience) {
                $('#doctor_yoeError').text('Please Fill Year of Experience');
                isValid = false;
            }
            if (!degrees || degrees.length == 0) {
                $('#doctor_degreesError').text('Please select Degrees');
                isValid = false;
            }
            if (!departments || departments.length == 0) {
                $('#doctor_departmentError').text('Please select Department');
                isValid = false;
            }
            if (!address) {
                $('#doctor_addressError').text('Please Fill your Address');
                isValid = false;
            }

            if (!isValid) {
                $('#loaderOverlay').hide();
                return;
            }
            const payload = {
                name, email, phone, dob, gender, yearOfExperience, degrees, departments, specializations, address, registrationNumber
            };
            $.ajax({
                url: `${UPDATE_PROFILE_PATH}/${role}`,
                type: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify(payload),
                headers: {
                    'Authorization': "Bearer " + token
                },
                success: function (response) {
                    $('#loaderOverlay').hide();
                    showToast(response.message);
                },
                error: function (xhr) {
                    let msg = "Failed to update profile";
                    if (xhr.responseJSON.message) {
                        msg = xhr.responseJSON.message;
                    } else if (xhr.responseText) {
                        msg = xhr.responseText;
                    }
                    $('#loaderOverlay').hide();
                    showToast(msg);
                }
            });
        }
        $('#loaderOverlay').hide();
    }

    function getSelectedObjects(selectorId) {
        const selected = [];
        $(`#${selectorId} option:selected`).each(function () {
            selected.push({
                id: $(this).val(),
                name: $(this).data("name")
            });
        });
        return selected;
    }
    function uploadProfilePic(file) {
        $('#loaderOverlay').show();
        const token = localStorage.getItem("aSessionId");
        const role = localStorage.getItem("role");
        const formData = new FormData();
        formData.append("file", file);

        $.ajax({
            url: `${UPLOAD_PROFILE_PIC_PATH}/${role}`,
            type: "PUT",
            headers: {
                Authorization: "Bearer " + token,
            },
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                const snackbar = $("#snackbar");
                snackbar.text(response.message).addClass("show");
                setTimeout(() => {
                    snackbar.removeClass("show");
                    $('#loaderOverlay').hide();
                    location.reload();
                }, 2000);
            },
            error: function (xhr) {
                let msg = "Failed to update profile picture";
                        if (xhr && xhr.responseJSON && xhr.responseText) {
                            if (xhr.responseJSON.message) {
                                msg = xhr.responseJSON.message;
                            } else if (xhr.responseText) {
                                msg = xhr.responseText;
                            }
                        }
                $('#loaderOverlay').hide();
                showToast(msg);
            },
        });
    }
});