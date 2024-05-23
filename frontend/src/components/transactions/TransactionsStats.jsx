import React from "react";
import { Box, Paper, Typography } from "@mui/material";
import { formatFloat } from "../../helpers/Helpers.jsx";

export const TransactionsStats = (stats) => {
  return (
    <Box
      component={Paper}
      width='100%'
      padding={2}
      mb={4}
      display='flex'
      justifyContent='space-between'
    >
      <Box>
        <Typography variant='body2'>TOTAL CASH FLOW</Typography>
        <Typography variant='h6' fontWeight='bold'>
          ${formatFloat(stats.totalCashFlow)}
        </Typography>
      </Box>
      <Box textAlign='center'>
        <Typography variant='body2'>TOTAL INVESTMENT</Typography>
        <Typography variant='h6' fontWeight='bold'>
          ${formatFloat(stats.totalInvestment)}
        </Typography>
      </Box>
      <Box textAlign='right'>
        <Typography variant='body2'>MOST TRADED STOCK</Typography>
        <Typography variant='h6' fontWeight='bold'>
          {stats.mostTradedStock || "N/A"}
        </Typography>
      </Box>
    </Box>
  );
};
