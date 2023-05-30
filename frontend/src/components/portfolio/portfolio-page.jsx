import { Box } from "@mui/material";
import React from "react";
import PortfolioHoldings from "./portfolio-holdings";

export default function PortfolioPage() {
  return (
    <Box my={5} maxWidth='80%' mx='auto'>
      <PortfolioHoldings />
    </Box>
  );
}
