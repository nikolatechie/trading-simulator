import React from "react";
import { Typography, Button, Box } from "@mui/material";

export default function ErrorPage() {
  const handleBackToHome = () => {
    // Redirect the user back to the home page
    window.location.href = "/";
  };

  return (
    <Box
      display='flex'
      justifyContent='center'
      alignItems='center'
      minHeight='100vh'
    >
      <div style={{ textAlign: "center", lineHeight: "8" }}>
        <Typography variant='h1'>Page Not Found</Typography>
        <Typography variant='h4'>
          Oops! The page you're looking for doesn't exist.
        </Typography>
        <Button variant='contained' color='primary' onClick={handleBackToHome}>
          Back to Home
        </Button>
      </div>
    </Box>
  );
}
