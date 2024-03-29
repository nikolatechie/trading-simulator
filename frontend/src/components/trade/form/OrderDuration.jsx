import React from "react";
import { FormControl, InputLabel, Select, MenuItem } from "@mui/material";
import { StockOrderDurations } from "../../../data/constants";

export const OrderDuration = React.memo((props) => (
  <FormControl>
    <InputLabel id='duration-label'>Duration</InputLabel>
    <Select
      className={props.classes.formField}
      labelId='duration-label'
      id='duration'
      label='Duration'
      value={props.duration}
      onChange={props.handleDurationChange}
      required
    >
      {Object.keys(StockOrderDurations).map((key, i) => (
        <MenuItem key={i} value={StockOrderDurations[key]}>
          {StockOrderDurations[key]}
        </MenuItem>
      ))}
    </Select>
  </FormControl>
));
