import React from "react";
import { makeStyles } from "@mui/styles";
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Typography,
} from "@mui/material";
import { StockOrderActions } from "../../../data/constants.js";
import { formatFloat } from "../../../helpers/Helpers.jsx";

const useStyles = makeStyles(() => ({
  rowContainer: {
    display: "flex",
    justifyContent: "space-between",
  },
}));

const getTotalEstimate = (action, quantity, bidPrice, askPrice, price) => {
  let total = 0;

  if (price > 0.0) {
    total = quantity * price;
  } else if (action === StockOrderActions.BUY) {
    total = quantity * askPrice;
  } else if (action === StockOrderActions.SELL) {
    total = quantity * bidPrice;
  }

  return total;
};

export const OrderDialog = React.memo((props) => {
  const classes = useStyles();

  return (
    <Dialog
      open={props.dialogOpen}
      onClose={props.handleCloseDialogAction}
      aria-labelledby='alert-dialog-title'
      aria-describedby='alert-dialog-description'
    >
      <DialogTitle id='alert-dialog-title' sx={{ textAlign: "center" }}>
        {"Preview "}
        {props.action}
        {" Order"}
      </DialogTitle>
      <DialogContent sx={{ minWidth: "260px" }}>
        <Box className={classes.rowContainer}>
          <Box>Stock symbol:</Box>
          <Box>{props.symbol}</Box>
        </Box>
        <Box className={classes.rowContainer}>
          <Box>Duration:</Box>
          <Box>{props.duration}</Box>
        </Box>
        <Box className={classes.rowContainer}>
          <Box>Order type:</Box>
          <Box>{props.type}</Box>
        </Box>
        {props.price > 0.0 && (
          <Box className={classes.rowContainer}>
            <Box>Price:</Box>
            <Box>${props.price}</Box>
          </Box>
        )}
        <Box className={classes.rowContainer}>
          <Box>Quantity:</Box>
          <Box>{props.quantity}</Box>
        </Box>
        <br />
        <Box className={classes.rowContainer} gap={3}>
          <Box>
            <Typography variant='h6'>Est. Total</Typography>
          </Box>
          <Box>
            <Typography variant='h6'>
              $
              {formatFloat(
                getTotalEstimate(
                  props.action,
                  props.quantity,
                  props.bidPrice,
                  props.askPrice,
                  props.price
                )
              )}
            </Typography>
          </Box>
        </Box>
      </DialogContent>
      <DialogActions sx={{ padding: 2 }}>
        <Button onClick={props.handleCloseDialogAction}>Cancel</Button>
        <Button
          onClick={props.handlePlaceOrder}
          autoFocus
          sx={{ fontWeight: "bold" }}
        >
          Place order
        </Button>
      </DialogActions>
    </Dialog>
  );
});
