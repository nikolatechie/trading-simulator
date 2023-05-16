import React from "react";
import { FormControl, InputLabel, Select, MenuItem } from "@mui/material";
import { StockOrderTypes } from "../../../data/constants";

export const OrderType = React.memo((props) => (
  <FormControl>
    <InputLabel id='order-type-label'>Order Type</InputLabel>
    <Select
      className={props.classes.formField}
      labelId='order-type-label'
      id='order-type'
      label='Order Type'
      value={props.orderType}
      onChange={props.handleOrderTypeChange}
      required
    >
      {Object.keys(StockOrderTypes).map((type, i) => (
        <MenuItem key={i} value={StockOrderTypes[type]}>
          {StockOrderTypes[type]}
        </MenuItem>
      ))}
    </Select>
  </FormControl>
));
