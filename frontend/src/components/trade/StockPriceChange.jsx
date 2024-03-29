import React from "react";
import { formatStockChange } from "../../helpers/StockDetailsHelpers.jsx";
import { getColorStringByValue } from "../../helpers/Helpers.jsx";
import { Box, Typography } from "@mui/material";
import { makeStyles } from "@mui/styles";

const useStyles = makeStyles(() => ({
  flexEnd: {
    display: "flex",
    alignItems: "flex-end",
  },
}));

export const StockPriceAndChange = (props) => {
  const classes = useStyles();

  return (
    <Box className={classes.flexEnd} sx={{ mt: "15px" }}>
      <Typography variant='h4'>{props.quote.price.toFixed(2)}</Typography>
      <Typography variant='body1' sx={{ ml: "3px", mb: "2px" }}>
        {props.currency}
      </Typography>
      <Box
        className={classes.flexEnd}
        sx={{
          ml: "18px",
          color: getColorStringByValue(props.quote.change),
        }}
      >
        <Typography variant='h6'>
          {formatStockChange(props.quote.change)}
        </Typography>
        <Typography variant='h6' sx={{ ml: "5px" }}>
          ({formatStockChange(props.quote.changePercent)})
        </Typography>
      </Box>
    </Box>
  );
};
