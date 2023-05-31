import React from "react";
import { Typography } from "@mui/material";
import ArrowUpwardIcon from "@mui/icons-material/ArrowUpward";
import ArrowDownwardIcon from "@mui/icons-material/ArrowDownward";
import { formatFloat, getColorStringByValue } from "./helpers";

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

const transformHoldings = (holdings) => {
  const transformedHoldings = holdings.map((holding) => {
    return {
      ...holding,
      totalGainOrLoss: getTotalGainOrLoss(
        holding.quantity,
        holding.currentPrice,
        holding.purchasePrice
      ),
    };
  });
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
      const transformedHoldings = transformHoldings(data.content);
      return {
        holdings: transformedHoldings,
        totalPages: data.totalPages,
      };
    } else {
      return { errorMessage: data.errorMessage };
    }
  } catch (error) {
    console.error("Error occurred while fetching holdings:", error);
    return { errorMessage: error };
  }
};

// Add a function to determine the arrow direction based on gain/loss
export const getArrowDirection = (value) => {
  const arrowMarginTop = "3px";
  if (value > 0) {
    return (
      <ArrowUpwardIcon
        sx={{ color: getColorStringByValue(value), mt: arrowMarginTop }}
      />
    );
  } else if (value < 0) {
    return (
      <ArrowDownwardIcon
        sx={{ color: getColorStringByValue(value), mt: arrowMarginTop }}
      />
    );
  } else {
    return null;
  }
};
