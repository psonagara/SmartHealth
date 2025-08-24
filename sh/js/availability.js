let currentPage = 0;
const size = 5;
$(document).ready(function() {
    validateUser('doctor');
});
$('.section-header[data-target="#generateSlotsBody"]').click(function () {

    if (validateUser('doctor')) {

        $('#loaderOverlay').show();
        const token = localStorage.getItem("aSessionId");

        $.ajax({
            url: AVAILABILITY_PREFERENCE_PATH,
            method: 'GET',
            headers: { Authorization: "Bearer " + token },
            success: function (res) {
                res = res.content;
                if (!res || !res.mode) return;

                const modeMap = {
                    AUTO: '1',
                    CUSTOM_ONE_TIME: '2',
                    CUSTOM_CONTINUOUS: '3',
                    MANUAL: '4'
                };
                const modeVal = modeMap[res.mode];
                $('#skipHoliday').prop('checked', res.skipHoliday || false);

                if (res.mode === 'CUSTOM_ONE_TIME') {
                    $('input[name="startDate"]').val(res.startDate);
                    $('input[name="endDate"]').val(res.endDate || '');

                    $('#slotInputs').empty();
                    (res.slotInputs || []).forEach(slot => {
                        const row = `
                    <div class="slot-row">
                      <label>Start</label><input type="time" name="startTime[]" value="${slot.startTime.slice(0, 5)}" class="form-control" />
                      <label>End</label><input type="time" name="endTime[]" value="${slot.endTime.slice(0, 5)}" class="form-control" />
                      <label>Gap (min)</label><input type="number" name="gap[]" value="${slot.gapInMinutes}" class="form-control" />
                    </div>`;
                        $('#slotInputs').append(row);
                    });

                } else if (res.mode === 'CUSTOM_CONTINUOUS') {
                    $('input[name="startDate"]').val(res.startDate);
                    $('input[name="daysAhead"]').val(res.daysAhead || '');

                    $('#slotInputs').empty();
                    (res.slotInputs || []).forEach(slot => {
                        const row = `
                      <div class="slot-row">
                        <label>Start</label><input type="time" name="startTime[]" value="${slot.startTime.slice(0, 5)}" class="form-control" />
                        <label>End</label><input type="time" name="endTime[]" value="${slot.endTime.slice(0, 5)}" class="form-control" />
                        <label>Gap (min)</label><input type="number" name="gap[]" value="${slot.gapInMinutes}" class="form-control" />
                      </div>`;
                        $('#slotInputs').append(row);
                    });
                }

                $(`input[name="mode"][value="${modeVal}"]`).prop('checked', true).trigger('change').change(showModeAccrd(modeVal));
                $('#loaderOverlay').hide();
            },
            error: function () {
                showToast("No saved preference found or failed to load.");
                $('#loaderOverlay').hide();
            }
        });
    }
});

$('.section-header').click(function () {
    const target = $(this).data('target');
    $('.section-body').not(target).slideUp();
    $(target).slideToggle();
});

$('#addSlot').click(function () {
    const newRow = `<div class="slot-row">
        <label>Start:</label>
        <input type="time" class="form-control" name="startTime[]">
        <label>End:</label>
        <input type="time" class="form-control" name="endTime[]">
        <label>Gap:</label>
        <input type="number" class="form-control" name="gap[]" placeholder="Gap (mins)">
        <button type="button" class="btn btn-outline-danger remove-slot">×</button>
      </div>`;
    $('#slotInputs').append(newRow);
});

$('#addManualSlot').click(function () {
    const newRow = `<div class="manual-slot-row">
        <label>Date:</label>
        <input type="date" class="form-control" name="manualDate[]">
        <label>Start:</label>
        <input type="time" class="form-control" name="manualStartTime[]">
        <label>End:</label>
        <input type="time" class="form-control" name="manualEndTime[]">
        <button type="button" class="btn btn-outline-danger remove-slot">×</button>
      </div>`;
    $('#manualSlotInputs').append(newRow);
});

$(document).on('click', '.remove-slot', function () {
    $(this).closest('.slot-row, .manual-slot-row').remove();
});

$('input[name="mode"]').change(function () {
    const mode = $(this).val();
    showModeAccrd(mode);
});

function showModeAccrd(mode) {
    // Hide everything by default
    $('.custom-input, .manual-input').addClass('d-none');
    $('#generateBtnShared').addClass('d-none');
    $('#generateBtn').addClass('d-none');
    $('#savePreferenceBtn').addClass('d-none');

    if (mode === '1') {
        // Default Auto
        $('#generateBtnShared').removeClass('d-none').text('Save Preference');
    }

    if (mode === '2') {
        // Custom One-Time
        $('.custom-input').removeClass('d-none');
        $('#endDateField').removeClass('d-none');
        $('#daysAheadField').addClass('d-none');
        $('#generateBtnShared').removeClass('d-none').text('Save Preference and Generate');
    }

    if (mode === '3') {
        // Custom Continuous
        $('.custom-input').removeClass('d-none');
        $('#daysAheadField').removeClass('d-none');
        $('#endDateField').addClass('d-none');
        $('#generateBtnShared').removeClass('d-none').text('Save Preference and Generate');
    }

    if (mode === '4') {
        // Manual
        $('.manual-input').removeClass('d-none');
        $('#generateBtn').removeClass('d-none');
        $('#savePreferenceBtn').removeClass('d-none');
    }
}
function showError(message) {
    const snackbar = $("#snackbar");
    snackbar.text(message).addClass("show");
    setTimeout(() => snackbar.removeClass("show"), 3000);
}

function isValidTimeRange(start, end, gap) {
    const [sh, sm] = start.split(':').map(Number);
    const [eh, em] = end.split(':').map(Number);
    const startDate = new Date(0, 0, 0, sh, sm);
    const endDate = new Date(0, 0, 0, eh, em);
    const diff = (endDate - startDate) / 60000;
    return diff >= gap;
}

function postSlotData(payload) {
    $('#loaderOverlay').show();
    const token = localStorage.getItem("aSessionId");

    $.ajax({
        url: AVAILABILITY_GENERATOR_PATH,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(payload),
        headers: {
            Authorization: "Bearer " + token
        },
        success: (response) => {
            showToast(response.message);
            $('#loaderOverlay').hide();
        },
        error: (xhr) => {
            let msg = "Failed to generate slots";
            if (xhr && xhr.responseJSON && xhr.responseText) {
                if (xhr.responseJSON.message) {
                    msg = xhr.responseJSON.message;
                } else if (xhr.responseText) {
                    msg = xhr.responseText;
                }
            }
            showToast(msg);
            $('#loaderOverlay').hide();
        }
    });
}

function isDuplicateSlot(slotArray, newSlotKey) {
    return slotArray.some(slot => slot.key === newSlotKey);
}

$('#generateSlotsForm').on('submit', function (e) {
    e.preventDefault();

    if (validateUser('doctor')) {

        const mode = $('input[name="mode"]:checked').val();
        const skipIfHoliday = $('#skipHoliday').is(':checked');
        const todayStr = new Date().toISOString().split('T')[0];
        let payload = {};

        if (mode === '1') {
            payload.mode = "AUTO";
            postSlotData(payload);
            return;
        }

        if (mode === '2' || mode === '3') {
            const start = $('input[name="startDate"]').val();
            const end = $('input[name="endDate"]').val();
            const daysAhead = $('input[name="daysAhead"]').val();
            const slotInputs = [];
            const keys = new Set();

            if (!start || (mode === '2' && !end)) return showError("Required date field(s) missing.");
            if (new Date(start) < new Date(todayStr)) return showError("Start date should be today or in future.");
            if (mode === '2' && (new Date(end) < new Date(start))) return showError("End date must be equal or after start date.");
            if (mode === '2' && ((new Date(end) - new Date(start)) / (1000 * 3600 * 24) > 15)) return showError("Max 15-day range allowed.");
            if (mode === '3' && (parseInt(daysAhead) < 0 || parseInt(daysAhead) > 15)) return showError("Days Ahead must be 0-15");

            let allValid = true;
            $('#slotInputs .slot-row').each(function (index) {
                const s = $(this).find('input[name="startTime[]"]').val();
                const e = $(this).find('input[name="endTime[]"]').val();
                const g = $(this).find('input[name="gap[]"]').val();
                const key = `${s}-${e}-${g}`;
                if (!s || !e || !g) {
                    showError(`Slot ${index + 1} missing input.`);
                    allValid = false;
                    return false;
                }
                if (!isValidTimeRange(s, e, parseInt(g))) {
                    showError(`Invalid time range in slot ${index + 1}.`);
                    allValid = false;
                    return false;
                }
                if (keys.has(key)) {
                    showError(`Duplicate slot in slot ${index + 1}`);
                    allValid = false;
                    return false;
                }
                keys.add(key);
                slotInputs.push({ startTime: s, endTime: e, gapInMinutes: parseInt(g) });
            });

            if (!allValid || slotInputs.length === 0) return;

            payload = { mode: mode === '2' ? "CUSTOM_ONE_TIME" : "CUSTOM_CONTINUOUS", startDate: start, slotInputs };
            if (mode === '2') payload.endDate = end;
            if (mode === '3') payload.daysAhead = parseInt(daysAhead);
            payload.skipIfHoliday = skipIfHoliday;
            postSlotData(payload);
            return;
        }

        if (mode === '4') {
            const manualSlots = [];
            const keys = new Set();
            let allValid = true;

            $('#manualSlotInputs .manual-slot-row').each(function (index) {
                const d = $(this).find('input[name="manualDate[]"]').val();
                const f = $(this).find('input[name="manualStartTime[]"]').val();
                const t = $(this).find('input[name="manualEndTime[]"]').val();
                const key = `${d}-${f}-${t}`;

                if (!d || !f || !t) {
                    showError(`Manual Slot ${index + 1} missing input.`);
                    allValid = false;
                    return false;
                }
                if (new Date(d) < new Date(todayStr)) {
                    showError(`Manual Slot ${index + 1} must be today or future.`);
                    allValid = false;
                    return false;
                }
                if (!isValidTimeRange(f, t, 1)) {
                    showError(`End must be after start in slot ${index + 1}.`);
                    allValid = false;
                    return false;
                }
                if (keys.has(key)) {
                    showError(`Duplicate manual slot at ${index + 1}`);
                    allValid = false;
                    return false;
                }
                keys.add(key);
                manualSlots.push({ date: d, from: f, to: t });
            });

            const clicked = $(document.activeElement).attr('id');
            payload = { mode: "MANUAL" };
            if (clicked === 'generateBtn') {
                if (!allValid || manualSlots.length === 0) return;
                payload.manualSlots = manualSlots;
            }
            postSlotData(payload);
        }
    }
});

$('#savePreferenceBtn').click(function () {
    postSlotData({ mode: "MANUAL" });
});

$('#viewSlotsForm').on('submit', function (e) {
    e.preventDefault();
    currentPage = 0;
    fetchAndRenderSlots(currentPage);
});

function renderSlotTable(response) {
    const { data, currentPage, totalPages } = response.content;

    $('#slotTableBody').empty();
    data.forEach(slot => {
        let html = "";
        const status = slot.status;
        if (status === 'AVAILABLE') {
            html = "<button class='btn btn-danger btn-sm ml-2 btn-delete-slot' data-slot-id='" + slot.id + "'>Delete</button>";
        }
        const viewUrl = `slot-details.html?slotId=${slot.id}`;
        $('#slotTableBody').append(`
      <tr>
        <td>${slot.id}</td>
        <td>${slot.date}</td>
        <td>${slot.startTime} --- ${slot.endTime}</td>
        <td>${slot.status}</td>
        <td>${slot.mode}</td>
        <td>
          <a class='btn btn-info btn-sm' href='${viewUrl}'>View</a>
          ${html}
        </td>
      </tr>
    `);
    });

    renderPagination(currentPage, totalPages);
}

function renderPagination(currentPage, totalPages) {
    const $pagination = $('#pagination');
    $pagination.empty();

    if (totalPages <= 1) return;

    const maxVisible = 5;
    const half = Math.floor(maxVisible / 2);
    let start = Math.max(0, currentPage - half);
    let end = Math.min(totalPages, start + maxVisible);

    if (end - start < maxVisible && start > 0) {
        start = Math.max(0, end - maxVisible);
    }

    $pagination.append(`<li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
    <a class="page-link" href="#" data-page="0">«</a></li>`);

    $pagination.append(`<li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
    <a class="page-link" href="#" data-page="${currentPage - 1}"><</a></li>`);

    for (let i = start; i < end; i++) {
        $pagination.append(`<li class="page-item ${i === currentPage ? 'active' : ''}">
      <a class="page-link" href="#" data-page="${i}">${i + 1}</a></li>`);
    }

    $pagination.append(`<li class="page-item ${currentPage >= totalPages - 1 ? 'disabled' : ''}">
    <a class="page-link" href="#" data-page="${currentPage + 1}">></a></li>`);

    $pagination.append(`<li class="page-item ${currentPage >= totalPages - 1 ? 'disabled' : ''}">
    <a class="page-link" href="#" data-page="${totalPages - 1}">»</a></li>`);

    // Add "Go to Page" input and button
    $pagination.append(`
    <li class="page-item ml-3">
      <span>Go to Page</span>
    </li>
    <li class="page-item">
      <input type="number" id="goToPageInput" class="form-control form-control-sm ml-2" style="width: 70px;" min="1" max="${totalPages}" />
    </li>
    <li class="page-item">
      <button class="btn btn-sm btn-primary ml-2" id="goToPageBtn">Go</button>
    </li>
  `);
}

$('#pagination').on('click', 'a.page-link', function (e) {
    e.preventDefault();
    const page = parseInt($(this).data('page'));
    if (!isNaN(page) && page !== currentPage) {
        currentPage = page;
        fetchAndRenderSlots(currentPage);
    }
});

$('#pagination').on('click', '#goToPageBtn', function () {
    const pageInput = parseInt($('#goToPageInput').val());
    if (!isNaN(pageInput) && pageInput >= 1 && pageInput <= 1000) { // Safe limit
        const targetPage = pageInput - 1;
        currentPage = targetPage;
        fetchAndRenderSlots(currentPage);
    } else {
        showError("Please enter a valid page number.");
    }
});

function fetchAndRenderSlots(page = 0) {

    if (validateUser('doctor')) {
        const token = localStorage.getItem("aSessionId");

        const from = $('input[name="from"]').val();
        const to = $('input[name="to"]').val();

        const params = new URLSearchParams();
        if (from) params.append("from", from);
        if (to) {
            if (!from) return showError("Please select From date");
            if (new Date(from) > new Date(to)) return showError("To date must be after From date");
            params.append("to", to);
        }
        $('#loaderOverlay').show();
        const sort = $('#sortBy').val();

        params.append("page", page);
        if (sort) params.append("sort", sort);

        $.ajax({
            url: `${VIEW_AVAILABILITY_SLOTS}?${params.toString()}`,
            method: 'GET',
            headers: { Authorization: "Bearer " + token },
            success: (response, status) => {
                if (status === "nocontent") {
                    showToast("No Slots found");
                } else {
                    renderSlotTable(response);
                }
                $('#loaderOverlay').hide();
            },
            error: function (xhr) {
                let msg = "Failed to fetch slots";
                if (xhr && xhr.responseJSON && xhr.responseText) {
                    if (xhr.responseJSON.message) {
                        msg = xhr.responseJSON.message;
                    } else if (xhr.responseText) {
                        msg = xhr.responseText;
                    }
                }
                showToast(msg);
                $('#loaderOverlay').hide();
            }
        });
    }
}

// Delegate click event to dynamically added Delete buttons
$('#slotTableBody').on('click', '.btn-delete-slot', function () {

    if (validateUser('doctor')) {
        $('#loaderOverlay').show();
        const $row = $(this).closest('tr');
        const slotId = $(this).data('slot-id');
        const token = localStorage.getItem("aSessionId");

        $.ajax({
            url: `${DELETE_AVAILABILITY_SLOTS}/${slotId}`,
            method: 'DELETE',
            headers: { Authorization: "Bearer " + token },
            success: function (response) {
                $row.remove();
                showToast(response.message);
                $('#loaderOverlay').hide();
            },
            error: function (xhr) {

                let msg = "Failed to delete slot. Please try again.";
                if (xhr && xhr.responseJSON && xhr.responseText) {
                    if (xhr.responseJSON.message) {
                        msg = xhr.responseJSON.message;
                    } else if (xhr.responseText) {
                        msg = xhr.responseText;
                    }
                }
                showToast(msg);
                $('#loaderOverlay').hide();
            }
        });
    }
});

$('#sortBy').on('change', function () {
    currentPage = 0;
    fetchAndRenderSlots(currentPage);
});


$('#deleteSlotsForm').on('submit', function (e) {
    e.preventDefault();

    if (validateUser('doctor')) {
        const token = localStorage.getItem("aSessionId");

        const startDate = $('input[name="startDateD"]').val();
        const endDate = $('input[name="endDateD"]').val();
        const startTime = $('input[name="fromTime"]').val();
        const endTime = $('input[name="toTime"]').val();

        // Validations
        if (!startDate || !endDate) {
            showError("Start Date and End Date are required.");
            return;
        }

        const sd = new Date(startDate);
        const ed = new Date(endDate);
        if (sd > ed) {
            showError("End Date must be same or after Start Date.");
            return;
        }

        // Optional time validation
        if (startTime && endTime) {
            const [sh, sm] = startTime.split(':').map(Number);
            const [eh, em] = endTime.split(':').map(Number);
            const sTime = new Date(0, 0, 0, sh, sm);
            const eTime = new Date(0, 0, 0, eh, em);
            if (sTime >= eTime) {
                showError("End Time must be after Start Time.");
                return;
            }
        }

        const payload = {
            startDate,
            endDate,
            startTime: startTime || null,
            endTime: endTime || null
        };

        if (!confirm("Are you sure you want to delete this slots?")) return;
        $('#loaderOverlay').show();
        $.ajax({
            url: DELETE_AVAILABILITY_SLOTS,
            method: 'POST',
            contentType: 'application/json',
            headers: { Authorization: "Bearer " + token },
            data: JSON.stringify(payload),
            success: (response) => {
                showToast(response.message);
                // Optionally refresh current view
                fetchAndRenderSlots(currentPage);
                $('#loaderOverlay').hide();
            },
            error: function (xhr) {
                let msg = "Failed to delete slots";
                if (xhr && xhr.responseJSON && xhr.responseText) {
                    if (xhr.responseJSON.message) {
                        msg = xhr.responseJSON.message;
                    } else if (xhr.responseText) {
                        msg = xhr.responseText;
                    }
                }
                showToast(msg);
                $('#loaderOverlay').hide();
            }
        });
    }
});


function validateUser(requiredRole) {
    const token = localStorage.getItem("aSessionId");
    const role = localStorage.getItem("role");
    const expiry = localStorage.getItem("tokenExpiry");
    const now = new Date().getTime();

    if (!token || !role) {
        showToastAndRedirectTo("Unauthorized! Please login first.", 2000, "login.html");
    } else if (!expiry || now > expiry) {
        localStorage.removeItem("aSessionId");
        localStorage.removeItem("role");
        localStorage.removeItem("tokenExpiry");
        showToastAndRedirectTo("Session expired. Please login again.", 2000, "login.html");
    } else if (requiredRole !== role) {
        showToastAndRedirectTo("You don't have access to this page.", 2000, "index.html");
    } else {
        return true;
    }
    return false;
}

function showToast(message) {
    const snackbar = $("#snackbar");
    snackbar.text(message).addClass("show");
    setTimeout(() => {
        snackbar.removeClass("show");
    }, 3000);
}

function showToastAndRedirectTo(message, delay = 2000, redirect) {
    const snackbar = $("#snackbar");
    snackbar.text(message).addClass("show");

    setTimeout(() => {
        snackbar.removeClass("show");
        window.location.replace(redirect);
    }, delay);
}
