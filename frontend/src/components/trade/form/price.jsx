import React from "react";
import { TextField } from "@mui/material";
import { StockOrderTypes } from "../../../data/constants";

export const Price = React.memo((props) => (
  <TextField
    label='Price'
    type='number'
    value={props.price}
    onChange={props.handlePriceChange}
    inputProps={{ min: 0, step: 0.01 }}
    sx={{
      display: props.orderType === StockOrderTypes.MARKET ? "none" : "inline",
    }}
    required
  />
));
