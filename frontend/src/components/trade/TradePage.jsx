import React, { useState } from "react";
import { Alert, Box } from "@mui/material";
import StockSearch from "./StockSearch";
import StockDetails from "./StockDetails";
import { TradeForm } from "./form/TradeForm";

const getMarketDateTime = () => {
  const currentDate = new Date();
  const options = {
    timeZone: "America/New_York",
    hour12: false, // Use 24-hour format
  };
  const dateTime = new Date(currentDate.toLocaleString("en-US", options));
  return dateTime;
};

const getHourMinuteDifference = (startDate, endDate) => {
  // Calculate the difference in milliseconds
  const differenceMs = endDate.getTime() - startDate.getTime();

  // Calculate the hours and minutes
  const hours = Math.floor(differenceMs / (1000 * 60 * 60));
  const minutes = Math.floor((differenceMs % (1000 * 60 * 60)) / (1000 * 60));

  return { hours, minutes };
};

const getMarketOpenClose = () => {
  const dateTime = getMarketDateTime();
  const dayOfWeek = dateTime.getDay();
  const hour = dateTime.getHours();
  const minutes = dateTime.getMinutes();

  if (dayOfWeek === 0 || dayOfWeek === 6) {
    // Sunday or Saturday
    return {
      component: (
        <Alert severity='warning'>
          The market is closed. It will open on{" "}
          <strong>Monday at 9:30 am.</strong>
        </Alert>
      ),
    };
  }
  if (hour < 9 || (hour === 9 && minutes < 30)) {
    const openDateTime = new Date(dateTime);
    openDateTime.setHours(9, 30);
    const diff = getHourMinuteDifference(dateTime, openDateTime);
    return {
      component: (
        <Alert severity='warning'>
          The market is closed. It will open in{" "}
          <strong>
            {diff.hours}h {diff.minutes}min.
          </strong>
        </Alert>
      ),
    };
  }
  if (hour >= 16) {
    const openingDay = dayOfWeek === 5 ? "on Monday" : "tomorrow";
    return {
      component: (
        <Alert severity='warning'>
          The market is closed. It will open{" "}
          <strong>{openingDay} at 9:30 am.</strong>
        </Alert>
      ),
    };
  }
  const closeDateTime = new Date(dateTime);
  closeDateTime.setHours(16, 0);
  const diff = getHourMinuteDifference(dateTime, closeDateTime);
  return {
    open: true,
    component: (
      <Alert severity='success'>
        The market is open. It will close in{" "}
        <strong>
          {diff.hours}h {diff.minutes}min.
        </strong>
      </Alert>
    ),
  };
};

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
  const marketOpenClose = getMarketOpenClose();

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
      {marketOpenClose.component}
      <StockSearch handleSelectStock={handleSelectStock} />
      <StockDetails stock={stock} handleFormatQuote={handleFormatQuote} />
      {stock.symbol !== null &&
        quote.askPrice !== null &&
        marketOpenClose.open && (
          <TradeForm
            symbol={stock.symbol}
            name={stock.name}
            bidPrice={quote.bidPrice}
            askPrice={quote.askPrice}
          />
        )}
    </Box>
  );
}
