export const DRAWER_WIDTH = 240;

export const PAGE_SIZE = 10;

export const STARTING_CASH_BALANCE = 30000.0;

export const FIRST_TRANSACTION_DATE = "1900-01-01"; // Used for fetching transactions when start date is NOT set

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

export const StockTimeRanges = {
  "1D": 1,
  "5D": 5,
  "1M": 30,
  "6M": 180,
  "1Y": 365,
  "5Y": 1825,
  MAX: 1825,
};

export const StockOrderActions = {
  BUY: "Buy",
  SELL: "Sell",
};

export const StockOrderTypes = {
  MARKET: "Market",
  LIMIT: "Limit",
  STOP: "Stop",
};

export const StockOrderDurations = {
  DAY: "Day",
  GTC: "Good 'Til Cancelled (GTC)",
  IOC: "Immediate or Cancel (IOC)",
  FOK: "Fill or Kill (FOK)",
};
