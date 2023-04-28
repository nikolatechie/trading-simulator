import React from "react";
import { Box } from "@mui/material";
import StockSearch from "./stock-search";

export const TradePage = () => {
  return (
    <Box
      sx={{
        my: 5,
        width: "80%",
        mx: "auto",
      }}
    >
      <StockSearch />
    </Box>
  );
};
