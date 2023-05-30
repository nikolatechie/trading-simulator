import React from "react";
import { Typography } from "@mui/material";
import { formatFloat, getColorStringByValue } from "./helpers";

const fetchStockPrice = async (twelveDataApiKey, symbol) => {
  try {
    const response = await fetch(
      `https://twelve-data1.p.rapidapi.com/price?symbol=${symbol}&format=json&outputsize=30`,
      {
        method: "GET",
        headers: {
          "X-RapidAPI-Key": twelveDataApiKey,
          "X-RapidAPI-Host": "twelve-data1.p.rapidapi.com",
        },
      }
    );
    if (response.ok) {
      const data = await response.json();
      return data.price;
    } else {
      throw new Error(response.statusText);
    }
  } catch (error) {
    console.error(error);
    throw error;
  }
};

const getTotalGainOrLoss = (quantity, price, totalPurchasePrice) => {
  const totalGainOrLoss = quantity * price - totalPurchasePrice;
  const percentage = (totalGainOrLoss / totalPurchasePrice) * 100;
  const isNegative = totalGainOrLoss < 0.0 ? "-" : "";
  const formattedTotalGainOrLoss = formatFloat(Math.abs(totalGainOrLoss));
  return (
    <Typography
      fontWeight='bold'
      sx={{ color: getColorStringByValue(totalGainOrLoss) }}
    >
      {isNegative}${formattedTotalGainOrLoss} ({formatFloat(percentage)}%)
    </Typography>
  );
};

const transformHoldings = async (fetchedHoldings, twelveDataApiKey) => {
  const transformedHoldings = await Promise.all(
    fetchedHoldings.map(async (holding) => {
      try {
        const price = await fetchStockPrice(twelveDataApiKey, holding.symbol);
        const modifiedHolding = {
          ...holding,
          currentPrice: price,
          totalGainOrLoss: getTotalGainOrLoss(
            holding.quantity,
            price,
            holding.purchasePrice
          ),
        };
        return modifiedHolding;
      } catch (error) {
        throw error;
      }
    })
  );
  return transformedHoldings;
};

export const fetchHoldings = async (page) => {
  try {
    const token = localStorage.getItem("jwt");
    const response = await fetch(
      `http://localhost:8080/api/portfolio/holdings?page=${page}`,
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
      try {
        const transformedHoldings = await transformHoldings(
          data.holdings.content,
          data.twelveDataApiKey
        );
        return {
          holdings: transformedHoldings,
          totalPages: data.totalPages,
        };
      } catch (error) {
        return { errorMessage: error };
      }
    } else {
      return { errorMessage: data.errorMessage };
    }
  } catch (error) {
    console.error("Error occurred while fetching holdings:", error);
    return { errorMessage: error };
  }
};
