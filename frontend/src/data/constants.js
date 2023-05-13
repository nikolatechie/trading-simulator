export const DRAWER_WIDTH = 240;

export const PAGE_SIZE = 10;

export const MONTHS_SHORT = [
  "null",
  "Jan",
  "Feb",
  "Mar",
  "Apr",
  "May",
  "Jun",
  "Jul",
  "Aug",
  "Sep",
  "Oct",
  "Nov",
  "Dec",
];

export const ActionTypes = {
  FETCH_START: "FETCH_START",
  FETCH_SUCCESS: "FETCH_SUCCESS",
  FETCH_FAILURE: "FETCH_FAILURE",
  INCREMENT_PAGE: "INCREMENT_PAGE",
  SET_PAGE: "SET_PAGE",
};

export const stockTimeRanges = {
  "1D": 1,
  "5D": 5,
  "1M": 30,
  "6M": 180,
  "1Y": 365,
  "5Y": 1825,
  MAX: 1825,
};
