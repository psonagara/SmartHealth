const BASE_API_URL = "http://localhost:9902";
const AUTH_PATH = "auth";
const REGISTER = "register";
const LOGIN = "login";
const ADMIN = "admin";
const NAME = "name";
const PROFILE = "profile";
const PICTURE = "picture";
const DATA = "data";
const DOCTOR = "doctor";
const PATIENT = "patient";
const ADMIN_LOGIN_PATH = BASE_API_URL + "/" + AUTH_PATH + "/" + LOGIN + "/" + ADMIN;
const ADMIN_REGISTER_PATH = BASE_API_URL + "/" + AUTH_PATH + "/" + REGISTER + "/" + ADMIN;
const GET_PICTURE_NAME_PATH = BASE_API_URL + "/" + PROFILE + "/" + PICTURE + "/" + NAME;
const LOAD_PICTURE_PATH = BASE_API_URL + "/" + DATA + "/" + PICTURE;
const AVAILABILITY_PATH = "availability";
const SLOTS = "slots";
const DELETE = "delete";
const VIEW = "view";
const DETAILS = "details";
const CHANGE = "change";
const STATUS = "status";
const APPOINTMENT = "appointment";
const DELETE_AVAILABILITY_SLOTS = BASE_API_URL + "/" + ADMIN + "/" + DELETE + "/" + SLOTS;
const VIEW_AVAILABILITY_SLOT_DETAILS = BASE_API_URL + "/" + ADMIN + "/" + VIEW + "/" + SLOTS + "/" + DETAILS;
const CHANGE_APPOINTMENT_STATUS = BASE_API_URL + "/" + ADMIN + "/" + CHANGE + "/" + STATUS;
const CHANGE_AVAILABILITY_APPOINTMENT_STATUS = BASE_API_URL + "/" + ADMIN + "/" + CHANGE + "/" + APPOINTMENT + "/" + STATUS;
const DASHBOARD = "dashboard";
const STATS = "stats";
const UPDATE = "update";
const UPLOAD = "upload";
const PASSWORD = "password";
const SEARCH = "search";
const TOGGLE = "toggle";
const LIST = "list";
const LEAVE = "leave";
const HOLIDAY = "holiday";
const ADD = "add";
const VIEW_DASHBOARD_STATS = BASE_API_URL + "/" + ADMIN + "/" + DASHBOARD + "/" + STATS;
const VIEW_PROFILE = BASE_API_URL + "/" + PROFILE + "/" + VIEW;
const UPDATE_ADMIN_NAME = BASE_API_URL + "/" + PROFILE + "/" + UPDATE + "/" + ADMIN + "/" + NAME;
const UPLOAD_PROFILE_PICTURE = BASE_API_URL + "/" + PROFILE + "/" + UPLOAD + "/" + PICTURE;
const UPDATE_PASSWORD = BASE_API_URL + "/" + PROFILE + "/" + UPDATE + "/" + PASSWORD;
const DOCTOR_DATA_API_PATH = BASE_API_URL + "/" + DATA + "/" + DOCTOR;
const SEARCH_DOCTOR = BASE_API_URL + "/" + ADMIN + "/" + SEARCH + "/" + DOCTOR;
const SEARCH_PATIENT = BASE_API_URL + "/" + ADMIN + "/" + SEARCH + "/" + PATIENT;
const TOGGLE_USER_ACTIVATION_STATUS = BASE_API_URL + "/" + ADMIN + "/" + TOGGLE + "/" + STATUS;
const VIEW_DOCTOR_DETAILS = BASE_API_URL + "/" + ADMIN + "/" + VIEW + "/" + DOCTOR;
const VIEW_PATIENT_DETAILS = BASE_API_URL + "/" + ADMIN + "/" + VIEW + "/" + PATIENT;
const GET_DOCTOR_LIST = BASE_API_URL + "/" + ADMIN + "/" + DOCTOR + "/" + LIST
const GET_PATIENT_LIST = BASE_API_URL + "/" + ADMIN + "/" + PATIENT + "/" + LIST
const SEARCH_SLOTS = BASE_API_URL + "/" + ADMIN + "/" + SEARCH + "/" + SLOTS;
const SEARCH_APPOINTMENT = BASE_API_URL + "/" + ADMIN + "/" + SEARCH + "/" + APPOINTMENT;
const VIEW_DOCTOR_LEAVE = BASE_API_URL + "/" + ADMIN + "/" + VIEW + "/" + LEAVE + "/" + DOCTOR;
const CHANGE_LEAVE_STATUS = BASE_API_URL + "/" + ADMIN + "/" + CHANGE + "/" + LEAVE + "/" + STATUS + "/" + DOCTOR;
const VIEW_HOLIDAY = BASE_API_URL + "/" + DATA + "/" + VIEW + "/" + HOLIDAY;
const ADD_HOLIDAY = BASE_API_URL + "/" + ADMIN + "/" + ADD + "/" + HOLIDAY;
const DELETE_HOLIDAY = BASE_API_URL + "/" + ADMIN + "/" + DELETE + "/" + HOLIDAY;
const APPOINTMENT_COUNT = BASE_API_URL + "/" + ADMIN + "/" + DASHBOARD + "/" + "appointmentCount";
const UPCOMING_LEAVES = BASE_API_URL + "/" + ADMIN + "/" + DASHBOARD + "/" + "upcoming-leaves";
const TODAYS_APPOINTMENT = BASE_API_URL + "/" + ADMIN + "/" + DASHBOARD + "/" + "todays-appointments";
const APPOINTMENT_TREND = BASE_API_URL + "/" + ADMIN + "/" + DASHBOARD + "/" + "appointmentTrend";





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

function getBearerToken() {
    return "Bearer " + localStorage.getItem("aSessionId");
}

function getRole() {
    return localStorage.getItem("role");
}