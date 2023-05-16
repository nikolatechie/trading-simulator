import React, { useState } from "react";
import { Box } from "@mui/material";
import StockSearch from "./stock-search";
import StockDetails from "./stock-details";
import { TradeForm } from "./form/trade-form";

const initialStock = {
  symbol: null,
  name: null,
  exchange: null,
  currency: null,
};

const initialQuote = {
  askPrice: null,
};

export default function TradePage() {
  const [stock, setStock] = useState(initialStock);
  const [quote, setQuote] = useState(initialQuote);

  const handleSelectStock = (stock) => {
    setStock(stock);
  };

  const handleFormatQuote = (quote) => {
    setQuote(quote);
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
      <StockDetails stock={stock} handleFormatQuote={handleFormatQuote} />
      {stock.symbol !== null && quote.askPrice !== null && (
        <TradeForm
          symbol={stock.symbol}
          bidPrice={quote.bidPrice}
          askPrice={quote.askPrice}
        />
      )}
    </Box>
  );
}
