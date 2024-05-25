const SERVER_PORT = 8080;
export const BASE_API_URL = `http://localhost:${SERVER_PORT}/api`;
export const ENDPOINTS = {
  STOCKS: "/stocks",
  PORTFOLIO: "/portfolio",
  PORTFOLIO_CASH: "/portfolio/cash",
  PORTFOLIO_STATS: "/portfolio/stats",
  PORTFOLIO_HISTORY: "/portfolio/history",
  PORTFOLIO_OVERVIEW: "/portfolio/overview",
  PORTFOLIO_RANK: "/portfolio/rank",
  PORTFOLIO_HOLDINGS: "/portfolio/holdings",
  PORTFOLIO_QUANTITY: "/portfolio/quantity",
  PORTFOLIO_BEST_WORST_STOCKS: "/portfolio/bestWorstStocks",
  TRADE: "/trade",
  NEWS: "/news",
  ADD_COMMENT: "/news/comment",
  TRANSACTION: "/transaction",
  TRANSACTION_STATS: "/transaction/stats",
  STOCK_RECOMMENDATION: "/stocks/recommendation",
  USER: "/user",
  USER_FULL_NAME: "/user/fullName",
  USER_SETTINGS_INFO: "/user/settingsInfo",
  USER_UPDATE: "/user/update",
  VERIFY_EMAIL: "/verify-email",
  CHECK_JWT_EXPIRY: "/check-jwt-expiry",
  LOGIN: "/login",
  RESET_PASSWORD: "/user/reset-password",
  UPDATE_PASSWORD: "/user/update-password",
  REGISTER: "/register",

  ALPHA_VANTAGE_KEY: "/key/alpha-vantage",
  RAPID_API_KEY: "/key/rapid-api"
};

export const PASSWORD_MIN_LENGTH = 8;

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
