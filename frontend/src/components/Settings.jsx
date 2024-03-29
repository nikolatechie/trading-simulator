import React, { useEffect, useState } from "react";
import { Box, Paper, TextField, Button } from "@mui/material";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import { Delete } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { signOut } from "../auth/auth";
import {
  initialState,
  fetchInfo,
  saveUpdates,
  deleteAccount,
} from "../helpers/settingsHelpers";

export default function Settings() {
  const navigate = useNavigate();
  const [state, setState] = useState(initialState);

  useEffect(() => {
    const fetchData = async () => {
      const response = await fetchInfo();
      if (response.errorMessage) {
        alert(response.errorMessage);
      } else {
        setState(prevState => ({
          ...prevState,
          firstName: response.firstName,
          lastName: response.lastName,
          email: response.email,
        }));
      }
    };
    fetchData();
  }, []);

  const handleEdit = () => {
    setState({
      ...state,
      isEditing: true,
    });
  };

  const handleSave = async () => {
    const response = await saveUpdates(state);
    if (response) {
      setState({
        ...state,
        currentPassword: "",
        newPassword: "",
        newPasswordRepeat: "",
        isEditing: false,
      });
    }
  };

  const handleDeleteAccount = async () => {
    if (
      window.confirm("Are you sure you want to DELETE your account?") === true
    ) {
      const response = await deleteAccount();
      if (response) {
        signOut(navigate);
      }
    }
  };

  const handleSetFirstName = (event) => {
    setState({
      ...state,
      firstName: event.target.value,
    });
  };

  const handleSetLastName = (event) => {
    setState({
      ...state,
      lastName: event.target.value,
    });
  };

  const handleSetCurrentPassword = (event) => {
    setState({
      ...state,
      currentPassword: event.target.value,
    });
  };

  const handleSetNewPassword = (event) => {
    setState({
      ...state,
      newPassword: event.target.value,
    });
  };

  const handleSetNewPasswordRepeat = (event) => {
    setState({
      ...state,
      newPasswordRepeat: event.target.value,
    });
  };

  return (
    <Box component={Paper} padding={2} width='80%' mx='auto' my={5}>
      <Box textAlign='center' mt={2}>
        <AccountCircleIcon sx={{ width: 100, height: 100 }} />
      </Box>
      {state.isEditing ? (
        <Button
          startIcon={<SaveIcon />}
          variant='contained'
          color='primary'
          onClick={handleSave}
        >
          Save
        </Button>
      ) : (
        <Button
          startIcon={<EditIcon />}
          variant='contained'
          onClick={handleEdit}
        >
          Edit
        </Button>
      )}
      <Box my={2} mt={3}>
        <TextField
          label='First Name'
          fullWidth
          value={state.firstName}
          onChange={handleSetFirstName}
          disabled={!state.isEditing}
        />
      </Box>
      <Box my={2}>
        <TextField
          label='Last Name'
          fullWidth
          value={state.lastName}
          onChange={handleSetLastName}
          disabled={!state.isEditing}
        />
      </Box>
      <Box my={2}>
        <TextField label='Email' fullWidth value={state.email} disabled />
      </Box>
      <Box my={2}>
        <TextField
          label='Current Password'
          fullWidth
          type='password'
          value={state.currentPassword}
          onChange={handleSetCurrentPassword}
          disabled={!state.isEditing}
          required={state.isEditing}
        />
      </Box>
      <Box my={2}>
        <TextField
          label='New Password'
          fullWidth
          type='password'
          value={state.newPassword}
          onChange={handleSetNewPassword}
          disabled={!state.isEditing}
        />
      </Box>
      <Box my={2}>
        <TextField
          label='Repeat New Password'
          fullWidth
          type='password'
          value={state.newPasswordRepeat}
          onChange={handleSetNewPasswordRepeat}
          disabled={!state.isEditing}
          required={state.newPassword !== ""}
        />
      </Box>
      <Box textAlign='right' my={3}>
        <Button
          variant='outlined'
          color='error'
          startIcon={<Delete />}
          onClick={handleDeleteAccount}
        >
          Delete account
        </Button>
      </Box>
    </Box>
  );
}
