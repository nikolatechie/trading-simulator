import React from "react";
import ArrowUpwardIcon from "@mui/icons-material/ArrowUpward";
import ArrowDownwardIcon from "@mui/icons-material/ArrowDownward";
import { MONTHS_SHORT } from "../data/constants";

export const formatDateTime = (dateTime) => {
  const year = dateTime.substring(0, 4);
  const month = Number(dateTime.substring(5, 7));
  const day = dateTime.substring(8, 10);
  const hour = dateTime.substring(11, 13);
  const min = dateTime.substring(14, 16);
  return `${day}-${MONTHS_SHORT[month]}-${year} at ${hour}:${min}`;
};

export const formatDateLong = (date) => {
  const month = Number(date.substring(5, 7));
  const day = Number(date.substring(8));
  const year = Number(date.substring(0, 4));
  return day + "-" + MONTHS_SHORT[month] + "-" + year;
};

export const formatDateShort = (date) => {
  const month = Number(date.substring(5, 7));
  const year = Number(date.substring(0, 4));
  return MONTHS_SHORT[month] + "-" + year;
};

// Inserts commas
export const formatFloat = (cash) => {
  cash = parseFloat(cash).toFixed(2);
  let pointIdx = cash.indexOf(".") - 3;

  while (pointIdx > 0) {
    cash = cash.substring(0, pointIdx) + "," + cash.substring(pointIdx);
    pointIdx -= 3;
  }

  return cash;
};

export const getColorStringByValue = (value) => {
  value = parseFloat(value);
  if (value === 0.0) return "#e6b800";
  return value < 0.0 ? "red" : "green";
};

// Determines the arrow direction based on value
export const getArrowDirection = (value) => {
  const arrowMarginBottom = "-5px";
  if (value > 0) {
    return (
      <ArrowUpwardIcon
        sx={{ color: getColorStringByValue(value), mb: arrowMarginBottom }}
      />
    );
  } else if (value < 0) {
    return (
      <ArrowDownwardIcon
        sx={{ color: getColorStringByValue(value), mb: arrowMarginBottom }}
      />
    );
  } else {
    return false;
  }
};
