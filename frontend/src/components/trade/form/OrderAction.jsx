import React from "react";
import {
  Box,
  InputLabel,
  FormControl,
  RadioGroup,
  Radio,
  FormControlLabel,
} from "@mui/material";
import { StockOrderActions } from "../../../data/constants";

export const OrderAction = React.memo((props) => (
  <Box className={props.classes.rowContainer}>
    <InputLabel>Action:</InputLabel>
    <FormControl component='fieldset'>
      <RadioGroup
        row
        aria-label='action'
        name='action'
        value={props.action}
        onChange={props.handleActionChange}
      >
        <FormControlLabel
          value={StockOrderActions.BUY}
          control={<Radio />}
          label={StockOrderActions.BUY}
        />
        <FormControlLabel
          value={StockOrderActions.SELL}
          control={<Radio />}
          label={StockOrderActions.SELL}
        />
      </RadioGroup>
    </FormControl>
  </Box>
));
