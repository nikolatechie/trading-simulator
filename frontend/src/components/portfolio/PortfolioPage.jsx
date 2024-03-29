import React, { useEffect, useState } from "react";
import { Box, Grid } from "@mui/material";
import PortfolioHoldings from "./PortfolioHoldings";
import PerformanceChart from "./PerformanceChart";
import PortfolioOverview from "./PortfolioOverview";
import PortfolioRank from "./PortfolioRank";
import { BASE_API_URL, ENDPOINTS } from '../../data/constants';

export default function PortfolioPage() {
  const [portfolioStats, setPortfolioStats] = useState({
    totalValue: 0.0,
    totalGainOrLoss: 0.0,
  });

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const token = localStorage.getItem("jwt");
        const response = await fetch(
          `${BASE_API_URL}${ENDPOINTS.PORTFOLIO_STATS}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );
        const data = await response.json();
        if (response.ok) {
          setPortfolioStats({
            totalValue: data.totalValue,
            totalGainOrLoss: data.totalGainOrLoss,
          });
        } else {
          alert(data.errorMessage);
        }
      } catch (error) {
        console.log(error);
      }
    };
    fetchStats();
  }, []);

  return (
    <Box my={5} maxWidth='80%' mx='auto'>
      <Grid container spacing={2}>
        <Grid item xs={12} lg={5} container spacing={2}>
          <Grid item xs={6} sm={12} md={6} lg={12}>
            <PortfolioOverview stats={portfolioStats}></PortfolioOverview>
          </Grid>
          <Grid item xs={6} sm={12} md={6} lg={12}>
            <PortfolioRank></PortfolioRank>
          </Grid>
        </Grid>
        <Grid item xs={12} lg={7}>
          <PerformanceChart></PerformanceChart>
        </Grid>
      </Grid>
      <PortfolioHoldings stats={portfolioStats} />
    </Box>
  );
}
