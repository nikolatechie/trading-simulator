import React, { useEffect, useState } from "react";
import { Box, CircularProgress, Paper, Typography } from "@mui/material";
import { formatFloat, getColorStringByValue } from "../../helpers/Helpers";
import { BASE_API_URL, ENDPOINTS } from '../../data/constants';

export default function BestWorstStocks() {
  const [stocks, setStocks] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchBestAndWorstStocks = async () => {
      try {
        setLoading(true);
        const token = localStorage.getItem("jwt");
        const response = await fetch(
          `${BASE_API_URL}${ENDPOINTS.PORTFOLIO_BEST_WORST_STOCKS}`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setLoading(false);
        const data = await response.json();
        if (response.ok) {
          setStocks(data);
        } else {
          alert(data.errorMessage);
        }
      } catch (error) {
        setLoading(false);
        console.log(error);
      }
    };
    fetchBestAndWorstStocks();
  }, []);

  if (loading) {
    return <CircularProgress size={30} />;
  }

  if (stocks === null || stocks.best === null) {
    return (
      <Box component={Paper} sx={{ padding: 2, width: "100%", height: "100%" }}>
        <Typography fontStyle='italic'>
          You don't have any stocks in possession.
        </Typography>
      </Box>
    );
  }

  const worstTotalValue =
    stocks.worst.quantity * stocks.worst.currentPrice -
    stocks.worst.purchasePrice;
  const bestTotalValue =
    stocks.best.quantity * stocks.best.currentPrice - stocks.best.purchasePrice;

  return (
    <Box component={Paper} sx={{ padding: 2, width: "100%", height: "100%" }}>
      <Typography variant='body2'>YOUR BEST PERFORMING STOCK</Typography>
      <Box display='flex' gap={2}>
        <Typography variant='h6' fontWeight='bold'>
          {stocks.best.symbol}
        </Typography>
        <Typography
          variant='h6'
          fontWeight='bold'
          sx={{ color: getColorStringByValue(bestTotalValue) }}
        >
          {bestTotalValue < 0.0 ? "-" : "+"}$
          {formatFloat(Math.abs(bestTotalValue))}
        </Typography>
      </Box>
      {stocks.worst.id !== stocks.best.id && (
        <Box>
          <Typography variant='body2' mt={1}>
            YOUR WORST PERFORMING STOCK
          </Typography>
          <Box display='flex' gap={2}>
            <Typography variant='h6' fontWeight='bold'>
              {stocks.worst.symbol}
            </Typography>
            <Typography
              variant='h6'
              fontWeight='bold'
              sx={{ color: getColorStringByValue(worstTotalValue) }}
            >
              {worstTotalValue < 0.0 ? "-" : "+"}$
              {formatFloat(Math.abs(worstTotalValue))}
            </Typography>
          </Box>
        </Box>
      )}
    </Box>
  );
}
