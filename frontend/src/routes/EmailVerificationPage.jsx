import React from "react";
import { useState, useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import { Box, Typography } from "@mui/material";
import { BASE_API_URL, ENDPOINTS } from '../data/constants';

export default function EmailVerificationPage() {
  const [searchParams] = useSearchParams();
  const [verificationStatus, setVerificationStatus] = useState("Verifying...");

  useEffect(() => {
    const verifyUser = async () => {
      try {
        const token = searchParams.get("token");
        const response = await fetch(
          `${BASE_API_URL}${ENDPOINTS.VERIFY_EMAIL}?token=${token}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
            },
          }
        );
        if (response.ok) {
          setVerificationStatus("User verified successfully!");
        } else {
          const data = await response.json();
          setVerificationStatus(data.errorMessage);
        }
      } catch (err) {
        console.log(err);
        setVerificationStatus("Something went wrong. Please try again later.");
      }
    };
    verifyUser();
  }, [searchParams]);

  return (
    <Box>
      <Typography variant='h3' style={{ margin: "1em 0 0 1em" }}>
        {verificationStatus}
      </Typography>
    </Box>
  );
}
