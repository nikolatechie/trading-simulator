import { BASE_API_URL, ENDPOINTS } from '../data/constants';

export const initialState = {
  firstName: "",
  lastName: "",
  email: "",
  currentPassword: "",
  newPassword: "",
  newPasswordRepeat: "",
  isEditing: false,
};

export const fetchInfo = async () => {
  try {
    const token = localStorage.getItem("jwt");
    const response = await fetch(
      `${BASE_API_URL}${ENDPOINTS.USER_SETTINGS_INFO}`,
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );
    const data = await response.json();
    if (response.ok) {
      return {
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
      };
    } else {
      return { errorMessage: data.errorMessage };
    }
  } catch (error) {
    console.log(error);
    return { errorMessage: "Failed to retrieve user info." };
  }
};

export const saveUpdates = async (state) => {
  if (state.currentPassword === "") {
    alert("You must enter your current password!");
    return false;
  }
  if (
    state.newPassword !== "" &&
    state.newPasswordRepeat !== state.newPassword
  ) {
    alert("New passwords don't match!");
    return false;
  }
  try {
    const token = localStorage.getItem("jwt");
    const response = await fetch(`${BASE_API_URL}${ENDPOINTS.USER_UPDATE}`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(state),
    });
    const data = await response.json();
    if (response.ok) {
      localStorage.setItem("jwt", data.jwt);
      alert("User info has been updated.");
      return true;
    } else {
      alert(data.errorMessage);
      return false;
    }
  } catch (error) {
    console.log(error);
    alert("Failed to save updates.");
    return false;
  }
};

export const deleteAccount = async () => {
  try {
    const token = localStorage.getItem("jwt");
    const response = await fetch(`${BASE_API_URL}${ENDPOINTS.USER}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });
    if (response.ok) {
      return true;
    } else {
      const data = await response.json();
      alert(data.errorMessage);
    }
  } catch (error) {
    console.log(error);
  }
  return false;
};
