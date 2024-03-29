import React, { useEffect, useState } from "react";
import { Box, Paper, Typography, Grid } from "@mui/material";
import { formatFloat, getColorStringByValue } from "../../helpers/Helpers.jsx";
import { BASE_API_URL, ENDPOINTS } from '../../data/constants.js';

export default function PortfolioOverview(props) {
  const [change, setChange] = useState({
    valueChange: 0.0,
    percentageChange: null,
  });
  const [annualReturn, setAnnualReturn] = useState(0.0);
  const [cash, setCash] = useState(0.0);

  useEffect(() => {
    const fetchCash = async () => {
      try {
        const token = localStorage.getItem("jwt");
        const response = await fetch(
          `${BASE_API_URL}${ENDPOINTS.PORTFOLIO_OVERVIEW}`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );
        const data = await response.json();
        if (response.ok) {
          setCash(data.cash);
          setChange(data.todayChange);
          setAnnualReturn(data.annualReturn);
        }
      } catch (error) {
        console.log(error);
      }
    };
    fetchCash();
  }, []);

  const accountValue = (
    <>
      <Typography variant='body2'>ACCOUNT VALUE</Typography>
      <Typography variant='h5' fontWeight='bold'>
        ${formatFloat(cash + props.stats.totalValue)}
      </Typography>
    </>
  );

  const todayChange = (
    <Grid item sx={{ mt: 1 }}>
      <Typography variant='body2'>TODAY'S CHANGE</Typography>
      <Typography
        variant='h5'
        fontWeight='bold'
        sx={{ color: getColorStringByValue(change.valueChange) }}
      >
        {change.valueChange < 0.0 && "-"}$
        {formatFloat(Math.abs(change.valueChange))}
      </Typography>
      <Typography
        variant='body1'
        fontWeight='bold'
        sx={{ color: getColorStringByValue(change.percentageChange) }}
      >
        ({formatFloat(change.percentageChange)}%)
      </Typography>
    </Grid>
  );

  const cashComponent = (
    <Grid item xs={12} lg={6} sx={{ mt: 1 }}>
      <Typography variant='body2'>CASH</Typography>
      <Typography variant='h5' fontWeight='bold'>
        ${formatFloat(cash)}
      </Typography>
    </Grid>
  );

  if (props.info === "basic") {
    return (
      <Box component={Paper} sx={{ padding: 2, width: "100%" }}>
        {accountValue}
        <Grid container justifyContent='space-between'>
          {todayChange}
          {cashComponent}
        </Grid>
      </Box>
    );
  }

  return (
    <Box component={Paper} elevation={3} sx={{ padding: 2, width: "100%" }}>
      {accountValue}
      <Grid container>
        <Grid container item xs={12} lg={6}>
          {todayChange}
        </Grid>
        <Grid container item xs={12} lg={6}>
          <Grid item sx={{ mt: 1 }}>
            <Typography variant='body2'>ANNUAL RETURN</Typography>
            <Typography
              variant='h5'
              fontWeight='bold'
              sx={{ color: getColorStringByValue(annualReturn) }}
            >
              {formatFloat(annualReturn)}%
            </Typography>
          </Grid>
        </Grid>
      </Grid>
      <Grid container>
        <Grid item xs={12} lg={6} sx={{ mt: 1 }}>
          <Typography variant='body2'>BUYING POWER</Typography>
          <Typography variant='h5' fontWeight='bold'>
            ${formatFloat(cash + props.stats.totalValue / 2)}
          </Typography>
        </Grid>
        {cashComponent}
      </Grid>
    </Box>
  );
}
