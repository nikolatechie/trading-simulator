import React from "react";
import { Box, Typography } from "@mui/material";
import {
  shortenNumber,
  QuoteItem,
} from "../../helpers/StockDetailsHelpers.jsx";

export const QuoteInfoList = ({ state }) => {
  return (
    <Box sx={{ mt: "8px" }}>
      <QuoteItem>
        <Typography variant='body1' sx={{ fontWeight: "bold" }}>
          Volume (current)
        </Typography>
        <Typography variant='body1'>
          {shortenNumber(state.quote.volume)}
        </Typography>
      </QuoteItem>
      <QuoteItem>
        <Typography variant='body1' sx={{ fontWeight: "bold" }}>
          Bid/Ask price ($)
        </Typography>
        <Typography variant='body1'>
          {state.quote.bidPrice.toFixed(2)} / {state.quote.askPrice.toFixed(2)}
        </Typography>
      </QuoteItem>
      <QuoteItem>
        <Typography variant='body1' sx={{ fontWeight: "bold" }}>
          Day's High ($)
        </Typography>
        <Typography variant='body1'>{state.quote.high.toFixed(2)}</Typography>
      </QuoteItem>
      <QuoteItem>
        <Typography variant='body1' sx={{ fontWeight: "bold" }}>
          Day's Low ($)
        </Typography>
        <Typography variant='body1'>{state.quote.low.toFixed(2)}</Typography>
      </QuoteItem>
      <QuoteItem>
        <Typography variant='body1' sx={{ fontWeight: "bold" }}>
          Open ($)
        </Typography>
        <Typography variant='body1'>{state.quote.open.toFixed(2)}</Typography>
      </QuoteItem>
      <QuoteItem>
        <Typography variant='body1' sx={{ fontWeight: "bold" }}>
          Previous Close ($)
        </Typography>
        <Typography variant='body1'>
          {state.quote.prevClose.toFixed(2)}
        </Typography>
      </QuoteItem>
    </Box>
  );
};
