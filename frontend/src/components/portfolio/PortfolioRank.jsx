import { Box, CircularProgress, Paper, Typography } from "@mui/material";
import React, { useEffect, useState } from "react";
import { formatFloat, getArrowDirection } from "../../helpers/Helpers.jsx";
import { BASE_API_URL, ENDPOINTS } from '../../data/constants.js';

export default function PortfolioRank() {
  const [rank, setRank] = useState(null);

  useEffect(() => {
    const fetchRank = async () => {
      try {
        const token = localStorage.getItem("jwt");
        const response = await fetch(
          `${BASE_API_URL}${ENDPOINTS.PORTFOLIO_RANK}`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );
        const data = await response.json();
        if (response.ok) {
          setRank(data);
        } else {
          alert(data.errorMessage);
        }
      } catch (error) {
        console.log(error);
      }
    };
    fetchRank();
  }, []);

  return (
    <Box component={Paper} sx={{ padding: 2, height: "100%" }}>
      <Typography variant='body2'>CURRENT RANK</Typography>
      {rank !== null ? (
        <Box>
          <Typography variant='h6' fontWeight='bold' mt='-2px'>
            {rank.prevRank && getArrowDirection(rank.prevRank - rank.rank)}
            {rank.rank} out of {rank.totalUsers}
          </Typography>
          {rank.rank !== 1 ? (
            <>
              <Typography variant='body2' mt={1}>
                TOP PLAYER'S PORTFOLIO VALUE
              </Typography>
              <Typography variant='h6' mt='-2px'>
                ${formatFloat(rank.topPlayerTotalVal)}
              </Typography>
            </>
          ) : (
            false
          )}
        </Box>
      ) : (
        <CircularProgress size={30} />
      )}
    </Box>
  );
}
