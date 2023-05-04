import React, { useState } from "react";
import { Box } from "@mui/material";
import StockSearch from "./stock-search";
import StockDetails from "./stock-details";

const initialState = {
  symbol: null,
  name: null,
  exchange: null,
  currency: null,
};

export default function TradePage() {
  const [stock, setStock] = useState(initialState);

  const handleSelectStock = (stock) => {
    setStock(stock);
  };

  return (
    <Box
      sx={{
        my: 5,
        width: "80%",
        mx: "auto",
      }}
    >
      <StockSearch handleSelectStock={handleSelectStock} />
      <StockDetails stock={stock} />
    </Box>
  );
}
