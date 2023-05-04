import React, { useEffect, useRef, useReducer } from "react";
import { Alert, Typography, CircularProgress, Grid } from "@mui/material";
import { ActionTypes } from "../../data/constants";
import { initialState, reducer } from "./helpers/stock-details-helpers";
import StockChart from "./stock-chart";
import { StockPriceAndChange } from "./stock-price-change";
import { QuoteInfoList } from "./quote-info-list";

export default function StockDetails({ stock }) {
  const [state, dispatch] = useReducer(reducer, initialState);
  const alphaVantageApiKey = useRef(null);

  useEffect(() => {
    const fetchApiKey = async () => {
      try {
        const token = localStorage.getItem("jwt");
        const response = await fetch(
          "http://localhost:8080/api/key/alpha-vantage",
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );
        const data = await response.json();
        if (response.ok) {
          alphaVantageApiKey.current = data.apiKey;
        }
      } catch (err) {
        console.log(err);
      }
    };
    fetchApiKey();
  }, []);

  useEffect(() => {
    const fetchStockQuote = async () => {
      dispatch({ type: ActionTypes.FETCH_START });
      try {
        const response = await fetch(
          `https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=${stock.symbol}&apikey=${alphaVantageApiKey}`
        );
        if (response.ok) {
          const data = await response.json();
          const quote = data["Global Quote"];
          if (Object.keys(quote).length === 0) {
            // No info about the stock
            dispatch({ type: ActionTypes.FETCH_FAILURE });
            return;
          }
          const price = parseFloat(quote["05. price"]);
          dispatch({
            type: ActionTypes.FETCH_SUCCESS,
            quote: {
              open: parseFloat(quote["02. open"]),
              high: parseFloat(quote["03. high"]),
              low: parseFloat(quote["04. low"]),
              price: price,
              bidPrice: price - 0.01,
              askPrice: price + 0.01,
              volume: parseFloat(quote["06. volume"]),
              prevClose: parseFloat(quote["08. previous close"]),
              change: quote["09. change"],
              changePercent: quote["10. change percent"],
            },
          });
        } else {
          dispatch({ type: ActionTypes.FETCH_FAILURE });
        }
      } catch (err) {
        dispatch({ type: ActionTypes.FETCH_FAILURE });
      }
    };
    if (stock.symbol !== null) fetchStockQuote();
  }, [stock.symbol]);

  if (stock.symbol === null || state.quote === null) return false;

  if (state.isFetching) {
    return <CircularProgress sx={{ mt: "4rem" }} />;
  }

  if (!state.canFetchData) {
    return (
      <Alert severity='error' sx={{ mt: "2rem" }}>
        Couldn't fetch the {stock.symbol} stock quote. Please try again later.
      </Alert>
    );
  }

  return (
    <Grid container sx={{ mt: "4rem" }}>
      <Grid item xs={6}>
        <Typography variant='h4'>{stock.symbol}</Typography>
        <Typography variant='body1' sx={{ color: "grey" }} gutterBottom>
          {stock.name} | {stock.exchange}
        </Typography>
        <StockPriceAndChange currency={stock.currency} quote={state.quote} />
        <QuoteInfoList state={state} />
      </Grid>
      <Grid item xs={6}>
        <StockChart />
      </Grid>
    </Grid>
  );
}
