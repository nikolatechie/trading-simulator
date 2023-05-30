import { ActionTypes } from "../data/constants";
import { styled } from "@mui/styles";
import { Box } from "@mui/material";

export const initialState = {
  quote: null,
  logoUrl: null,
  canFetchData: true,
  isFetching: false,
};

export const reducer = (state, action) => {
  switch (action.type) {
    case ActionTypes.FETCH_START:
      return { ...state, quote: {}, isFetching: true };
    case ActionTypes.FETCH_SUCCESS:
      return {
        ...state,
        quote: action.quote,
        logoUrl: action.logoUrl,
        canFetchData: true,
        isFetching: false,
      };
    case ActionTypes.FETCH_FAILURE:
      return {
        ...state,
        canFetchData: false,
        isFetching: false,
      };
    default:
      throw new Error("Invalid reducer action type:", action.type);
  }
};

export const formatStockChange = (change) => {
  if (change == null) return "";
  let isNegativeChange = false;
  let isPercentChange = false;

  if (change[0] === "-") {
    isNegativeChange = true;
    change = change.replace("-", "");
  }

  if (change[change.length - 1] === "%") {
    isPercentChange = true;
    change = change.replace("%", "");
  }

  change = parseFloat(change).toFixed(2);
  let prefix = "";

  if (isNegativeChange) prefix = "-";
  else if (change !== "0.00") prefix = "+";

  return prefix + change + (isPercentChange ? "%" : "");
};

export const shortenNumber = (number) => {
  let ret = number.toString();
  if (number >= 1000000000) ret = (number / 1000000000).toFixed(1) + "B";
  else if (number >= 1000000) ret = (number / 1000000).toFixed(1) + "M";
  else if (number >= 1000) ret = (number / 1000).toFixed(1) + "K";
  return ret.replace(".0", "");
};

export const QuoteItem = styled(Box)(({ theme }) => ({
  display: "flex",
  flexDirection: "row",
  justifyContent: "space-between",
  padding: "10px",
  borderBottom: "1px solid #d3d3d3",
  width: "80%",
  maxWidth: "85%",
  "&:hover": {
    backgroundColor: theme.palette.action.hover,
  },
}));
