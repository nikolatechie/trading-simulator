import React, { useCallback, useEffect, useState } from "react";
import { makeStyles } from "@mui/styles";
import { Box } from "@mui/material";
import {
  fetchAvailableCash,
  getMaxQuantity,
  initialState,
  getDurationKey,
  placeTradeOrder,
} from "../../../helpers/tradeFormHelpers";
import { CashBalance } from "./CashBalance";
import { OrderAction } from "./OrderAction";
import { OrderQuantity } from "./OrderQuantity";
import { OrderType } from "./OrderType";
import { Price } from "./Price";
import { OrderDuration } from "./OrderDuration";
import { FormActions } from "./FormActions";
import { OrderDialog } from "./OrderDialog";

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

export const TradeForm = ({ symbol, name, bidPrice, askPrice }) => {
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
    // eslint-disable-next-line
    [action, symbol, bidPrice, askPrice]
  );

  const handleQuantityChange = useCallback(
    (e) => {
      setQuantity(e.target.value);
    },
    // eslint-disable-next-line
    [quantity, symbol, bidPrice, askPrice]
  );

  const handleShowMax = useCallback(async () => {
    const quantity = await getMaxQuantity(action, symbol, askPrice);
    setQuantity(quantity);
    // eslint-disable-next-line
  }, [action, symbol, bidPrice, askPrice, quantity]);

  const handleOrderTypeChange = useCallback(
    (e) => {
      setType(e.target.value);
    },
    // eslint-disable-next-line
    [type, symbol, bidPrice, askPrice]
  );

  const handlePriceChange = useCallback(
    (e) => {
      setPrice(e.target.value);
    },
    // eslint-disable-next-line
    [price, symbol, bidPrice, askPrice]
  );

  const handleDurationChange = useCallback(
    (e) => {
      setDuration(e.target.value);
    },
    // eslint-disable-next-line
    [duration, symbol, bidPrice, askPrice]
  );

  const handleClearOrder = useCallback(() => {
    setAction(initialState.action);
    setQuantity(initialState.quantity);
    setType(initialState.orderType);
    setPrice(initialState.price);
    setDuration(initialState.duration);
    // eslint-disable-next-line
  }, [action, quantity, type, price, duration]);

  const handleOpenDialogAction = useCallback(() => setDialogOpen(true),
    // eslint-disable-next-line
    [dialogOpen]
  );

  const handleCloseDialogAction = useCallback(() => setDialogOpen(false),
    // eslint-disable-next-line
    [dialogOpen]
  );

  const handlePlaceOrder = useCallback(async () => {
    setDialogOpen(false);
    const response = await placeTradeOrder({
      symbol: symbol,
      name: name,
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
    // eslint-disable-next-line
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
