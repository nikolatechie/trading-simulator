import React, { useEffect, useRef, useReducer } from "react";
import { Alert, Typography, CircularProgress, Grid, Box } from "@mui/material";
import { ActionTypes, BASE_API_URL, ENDPOINTS } from "../../data/constants.js";
import { initialState, reducer } from "../../helpers/StockDetailsHelpers.jsx";
import StockChart from "./StockChart.jsx";
import { StockPriceAndChange } from "./StockPriceChange.jsx";
import { QuoteInfoList } from "./QuoteInfoList";
import { makeStyles } from "@mui/styles";

const useStyles = makeStyles(() => ({
  flex: {
    display: "flex",
    alignItems: "center",
    gap: 10,
  },
  logo: {
    borderRadius: "50%",
    height: "30px",
  },
}));

export default function StockDetails({ stock, handleFormatQuote }) {
  const [state, dispatch] = useReducer(reducer, initialState);
  const alphaVantageApiKey = useRef(null);
  const rapidApiKey = useRef(null);
  const classes = useStyles();

  useEffect(() => {
    const fetchApiKey = async () => {
      try {
        const token = localStorage.getItem("jwt");
        const alphaVantageResponse = await fetch(
          `${BASE_API_URL}${ENDPOINTS.ALPHA_VANTAGE_KEY}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );
        if (alphaVantageResponse.ok) {
          const data = await alphaVantageResponse.json();
          alphaVantageApiKey.current = data.apiKey;
        }
        const rapidApiResponse = await fetch(
          `${BASE_API_URL}${ENDPOINTS.RAPID_API_KEY}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );
        if (rapidApiResponse.ok) {
          const data = await rapidApiResponse.json();
          rapidApiKey.current = data.apiKey;
        }
      } catch (err) {
        console.log(err);
      }
    };
    fetchApiKey();
  }, []);

  useEffect(() => {
    const fetchStockQuoteAndLogo = async () => {
      dispatch({ type: ActionTypes.FETCH_START });
      try {
        const quoteResponse = await fetch(
          `https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=${stock.symbol}&apikey=${alphaVantageApiKey.current}`,
          {
            method: "GET",
          }
        );
        if (quoteResponse.ok) {
          const data = await quoteResponse.json();
          const quote = data["Global Quote"];
          if (Object.keys(quote).length === 0) {
            // No info about the stock
            dispatch({ type: ActionTypes.FETCH_FAILURE });
            return;
          }
          const logoResponse = await fetch(
            `https://twelve-data1.p.rapidapi.com/logo?symbol=${stock.symbol}`,
            {
              method: "GET",
              headers: {
                "X-RapidAPI-Key": rapidApiKey.current,
                "X-RapidAPI-Host": "twelve-data1.p.rapidapi.com",
              },
            }
          );
          let logoUrl = null;
          if (logoResponse.ok) {
            const data = await logoResponse.json();
            logoUrl = data.url;
          }
          const price = parseFloat(quote["05. price"]);
          const formattedQuote = {
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
          };
          dispatch({
            type: ActionTypes.FETCH_SUCCESS,
            quote: formattedQuote,
            logoUrl: logoUrl,
          });
          handleFormatQuote(formattedQuote);
        } else {
          dispatch({ type: ActionTypes.FETCH_FAILURE });
        }
      } catch (err) {
        dispatch({ type: ActionTypes.FETCH_FAILURE });
      }
    };
    if (stock.symbol !== null) {
      fetchStockQuoteAndLogo();
    }
    // eslint-disable-next-line
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
    <Grid container sx={{ mt: "4rem", display: "flex", alignItems: "stretch" }}>
      <Grid item xs={5} sx={{ mb: 1 }}>
        <Box className={classes.flex}>
          <img
            className={classes.logo}
            alt={stock.name + " logo"}
            src={state.logoUrl}
          />
          <Typography variant='h4'>{stock.symbol}</Typography>
        </Box>
        <Typography variant='body1' sx={{ color: "grey" }} gutterBottom>
          {stock.name} | {stock.exchange}
        </Typography>
        <StockPriceAndChange currency={stock.currency} quote={state.quote} />
        <QuoteInfoList state={state} />
      </Grid>
      <Grid item xs={7} sx={{ display: "flex", flexDirection: "column" }}>
        <StockChart symbol={stock.symbol} rapidApiKey={rapidApiKey.current} />
      </Grid>
    </Grid>
  );
}
