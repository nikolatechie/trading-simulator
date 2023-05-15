import React from "react";
import { Typography, Button, Box } from "@mui/material";
import { useNavigate } from "react-router-dom";

export default function ErrorPage() {
  const navigate = useNavigate();

  const handleBackToHome = () => {
    // Redirect the user back to the home page
    navigate("/");
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
