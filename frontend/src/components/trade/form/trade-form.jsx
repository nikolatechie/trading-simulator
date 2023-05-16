import React, { useCallback, useEffect, useState } from "react";
import { makeStyles } from "@mui/styles";
import { Box } from "@mui/material";
import {
  fetchAvailableCash,
  getMaxQuantity,
  initialState,
  getDurationKey,
  placeTradeOrder,
} from "../helpers/trade-form-helpers";
import { CashBalance } from "./cash-balance";
import { OrderAction } from "./order-action";
import { OrderQuantity } from "./order-quantity";
import { OrderType } from "./order-type";
import { Price } from "./price";
import { OrderDuration } from "./order-duration";
import { FormActions } from "./form-actions";
import { OrderDialog } from "./order-dialog";

const useStyles = makeStyles(() => ({
  rowContainer: {
    display: "flex",
    flexDirection: "row",
    alignItems: "center",
    gap: "1rem",
  },
  formField: {
    minWidth: 200,
  },
}));

export const TradeForm = ({ symbol, bidPrice, askPrice }) => {
  const classes = useStyles();
  const [action, setAction] = useState(initialState.action);
  const [quantity, setQuantity] = useState(initialState.quantity);
  const [type, setType] = useState(initialState.orderType);
  const [price, setPrice] = useState(initialState.price);
  const [duration, setDuration] = useState(initialState.duration);
  const [cash, setCash] = useState(initialState.cash);
  const [dialogOpen, setDialogOpen] = useState(false);

  useEffect(() => {
    const fetchCash = async () => {
      const cash = await fetchAvailableCash();
      setCash(cash);
    };
    fetchCash();
  }, []);

  const handlePreviewOrder = (e) => {
    e.preventDefault();
    setDialogOpen(true);
  };

  const handleActionChange = useCallback(
    (e) => {
      setAction(e.target.value);
    },
    [action]
  );

  const handleQuantityChange = useCallback(
    (e) => {
      setQuantity(e.target.value);
    },
    [quantity]
  );

  const handleShowMax = useCallback(async () => {
    const quantity = await getMaxQuantity(action, symbol, askPrice);
    setQuantity(quantity);
  }, [action, quantity]);

  const handleOrderTypeChange = useCallback(
    (e) => {
      setType(e.target.value);
    },
    [type]
  );

  const handlePriceChange = useCallback(
    (e) => {
      setPrice(e.target.value);
    },
    [price]
  );

  const handleDurationChange = useCallback(
    (e) => {
      setDuration(e.target.value);
    },
    [duration]
  );

  const handleClearOrder = useCallback(() => {
    setAction(initialState.action);
    setQuantity(initialState.quantity);
    setType(initialState.orderType);
    setPrice(initialState.price);
    setDuration(initialState.duration);
  }, [action, quantity, type, price, duration]);

  const handleOpenDialogAction = useCallback(() => setDialogOpen(true), [
    dialogOpen,
  ]);

  const handleCloseDialogAction = useCallback(() => setDialogOpen(false), [
    dialogOpen,
  ]);

  const handlePlaceOrder = useCallback(async () => {
    setDialogOpen(false);
    const response = await placeTradeOrder({
      symbol: symbol,
      action: action,
      quantity: quantity,
      type: type,
      price: price,
      duration: getDurationKey(duration),
    });
    if (response.errorMessage) {
      alert(response.errorMessage);
    } else {
      setCash(response.cash);
      alert("Your trade order has been placed!");
    }
  }, [dialogOpen, cash, action, quantity, type, price, duration]);

  return (
    <Box display='flex' sx={{ mt: "2.5rem" }}>
      <form onSubmit={handlePreviewOrder} style={{ width: "100%" }}>
        <Box display='flex' flexDirection='column' gap={2}>
          <Box display='flex' justifyContent='space-between'>
            <OrderAction
              action={action}
              handleActionChange={handleActionChange}
              classes={classes}
            />
            <CashBalance cash={cash} />
          </Box>
          <OrderQuantity
            quantity={quantity}
            handleQuantityChange={handleQuantityChange}
            handleShowMax={handleShowMax}
            classes={classes}
          />
          <Box className={classes.rowContainer}>
            <OrderType
              orderType={type}
              handleOrderTypeChange={handleOrderTypeChange}
              classes={classes}
            />
            <Price
              price={price}
              orderType={type}
              handlePriceChange={handlePriceChange}
            />
            <OrderDuration
              duration={duration}
              handleDurationChange={handleDurationChange}
              classes={classes}
            />
          </Box>
          <FormActions handleClearOrder={handleClearOrder} classes={classes} />
        </Box>
      </form>
      <OrderDialog
        dialogOpen={dialogOpen}
        handleOpenDialogAction={handleOpenDialogAction}
        handleCloseDialogAction={handleCloseDialogAction}
        handlePlaceOrder={handlePlaceOrder}
        symbol={symbol}
        bidPrice={bidPrice}
        askPrice={askPrice}
        cash={cash}
        action={action}
        quantity={quantity}
        type={type}
        price={price}
        duration={getDurationKey(duration)}
      />
    </Box>
  );
};
