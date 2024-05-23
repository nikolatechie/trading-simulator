import * as React from "react";
import Avatar from "@mui/material/Avatar";
import Button from "@mui/material/Button";
import CssBaseline from "@mui/material/CssBaseline";
import TextField from "@mui/material/TextField";
import Link from "@mui/material/Link";
import Paper from "@mui/material/Paper";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import Typography from "@mui/material/Typography";
import { useSearchParams } from "react-router-dom";
import { BASE_API_URL, ENDPOINTS, PASSWORD_MIN_LENGTH } from '../data/constants';
import { useState } from 'react';

export default function ResetPasswordPage() {
  const [resetPassword, setResetPassword] = useState(false); // Becomes true after user resets a password
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");

  const handleSendLink = async (event) => {
    const data = new FormData(event.currentTarget);
    try {
      const response = await fetch(`${BASE_API_URL}${ENDPOINTS.RESET_PASSWORD}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email: data.get("email")
        }),
      });
      if (response.ok) {
        alert("An email with the reset link has been sent.");
      } else {
        const data = await response.json();
        throw new Error(data.errorMessage);
      }
    } catch (error) {
      console.log(error);
      alert(error);
    }
  };

  const handleResetPassword = async (event) => {
    const data = new FormData(event.currentTarget);
    const password = data.get("password");
    const passwordRepeat = data.get("repeat-password");

    if (password !== passwordRepeat) {
      alert("Passwords don't match.");
      return;
    }

    if (password.length < PASSWORD_MIN_LENGTH) {
      alert(`Password must consist of at least ${PASSWORD_MIN_LENGTH} characters.`)
      return;
    }

    try {
      const response = await fetch(`${BASE_API_URL}${ENDPOINTS.UPDATE_PASSWORD}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          newPassword: password,
          newPasswordRepeat: passwordRepeat,
          token: token
        }),
      });
      if (response.ok) {
        setResetPassword(true);
      } else {
        const data = await response.json();
        throw new Error(data.errorMessage);
      }
    } catch (error) {
      console.log(error);
      alert(error);
    }
  }

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (token) {
      await handleResetPassword(event);
    } else {
      await handleSendLink(event);
    }
  }

  let formContent = <>
    < TextField
      margin='normal'
      required
      fullWidth
      id='email'
      label='Email Address'
      name='email'
      autoComplete='email'
      autoFocus
    />
    <Button
      type='submit'
      fullWidth
      variant='contained'
      sx={{ mt: 3, mb: 2 }}
    >
      Send a reset link
    </Button>
  </>;

  if (token) {
    if (resetPassword) {
      formContent = <Typography variant='h6' align='center' my={5}>Your password has been reset successfully!</Typography>
    } else {
      formContent = <>
        < TextField
          margin='normal'
          required
          fullWidth
          id='password'
          label='New Password'
          name='password'
          autoComplete='password'
          type='password'
          autoFocus
        />
        < TextField
          margin='normal'
          required
          fullWidth
          id='repeat-password'
          label='Repeat New Password'
          name='repeat-password'
          type='password'
        />
        <Button
          type='submit'
          fullWidth
          variant='contained'
          sx={{ mt: 3, mb: 2 }}
        >
          Reset your password
        </Button>
      </>;
    }
  }

  return (
    <Grid container component='main' sx={{ height: "100vh" }}>
      <CssBaseline />
      <Grid
        item
        xs={false}
        sm={4}
        md={7}
        sx={{
          backgroundImage:
            "url(https://images.unsplash.com/photo-1640340434855-6084b1f4901c?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format)",
          backgroundRepeat: "no-repeat",
          backgroundColor: (t) =>
            t.palette.mode === "light"
              ? t.palette.grey[50]
              : t.palette.grey[900],
          backgroundSize: "cover",
          backgroundPosition: "center",
        }}
      />
      <Grid item xs={12} sm={8} md={5} component={Paper} elevation={6} square>
        <Box
          sx={{
            my: 15,
            mx: 4,
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
          }}
        >
          <Avatar sx={{ m: 1, bgcolor: "secondary.main" }}>
            <LockOutlinedIcon />
          </Avatar>
          <Typography component='h1' variant='h5'>
            Reset your password
          </Typography>
          <Box
            component='form'
            noValidate
            onSubmit={handleSubmit}
            sx={{ mt: 1, width: "100%" }}
          >
            {formContent}
            <Grid container justifyContent='flex-end'>
              <Grid item>
                <Link href='/login' variant='body2'>
                  Back to login
                </Link>
              </Grid>
            </Grid>
          </Box>
        </Box>
      </Grid>
    </Grid>
  );
}
