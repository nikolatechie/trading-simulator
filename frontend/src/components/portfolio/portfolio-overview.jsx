import React, { useEffect, useState } from "react";
import { Box, Paper, Typography, Grid } from "@mui/material";
import { formatFloat, getColorStringByValue } from "../../helpers/helpers";

const formatChange = (change) => {
  const valueChange = change.valueChange;
  let percentageChange = change.percentageChange;
  if (valueChange !== 0.0 && percentageChange === 0.0) {
    percentageChange = null;
  }
  return {
    valueChange: "$" + valueChange,
    percentageChange:
      percentageChange !== 0.0 ? ` (${formatFloat(percentageChange)}%)` : null,
  };
};

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
          "http://localhost:8080/api/portfolio/overview",
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
          setChange(formatChange(data.todayChange));
          setAnnualReturn(data.annualReturn);
        }
      } catch (error) {
        console.log(error);
      }
    };
    fetchCash();
  }, []);

  return (
    <Box component={Paper} sx={{ padding: 2 }}>
      <Typography variant='body2'>ACCOUNT VALUE</Typography>
      <Typography variant='h5' fontWeight='bold'>
        ${formatFloat(cash + props.stats.totalValue)}
      </Typography>
      <Grid container>
        <Grid container item xs={12} lg={6}>
          <Grid item sx={{ mt: 1 }}>
            <Typography variant='body2'>TODAY'S CHANGE</Typography>
            <Typography
              variant='h5'
              fontWeight='bold'
              sx={{ color: getColorStringByValue(change.valueChange) }}
            >
              {change.valueChange}
            </Typography>
            <Typography
              variant='body1'
              fontWeight='bold'
              sx={{ color: getColorStringByValue(change.percentageChange) }}
            >
              {change.percentageChange}
            </Typography>
          </Grid>
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
        <Grid item xs={12} lg={6} sx={{ mt: 1 }}>
          <Typography variant='body2'>CASH</Typography>
          <Typography variant='h5' fontWeight='bold'>
            ${formatFloat(cash)}
          </Typography>
        </Grid>
      </Grid>
    </Box>
  );
}
