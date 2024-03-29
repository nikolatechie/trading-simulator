import {
  StockOrderActions,
  StockOrderTypes,
  StockOrderDurations,
  BASE_API_URL,
  ENDPOINTS,
} from "../data/constants";

export const initialState = {
  action: StockOrderActions.BUY,
  quantity: "",
  orderType: StockOrderTypes.MARKET,
  price: 0,
  duration: StockOrderDurations.IOC,
  cash: 0,
};

export const getDurationKey = (val) => {
  for (const key in StockOrderDurations) {
    if (val === StockOrderDurations[key]) return key;
  }
  return null;
};

export const fetchAvailableCash = async () => {
  try {
    const token = localStorage.getItem("jwt");
    const response = await fetch(`${BASE_API_URL}${ENDPOINTS.PORTFOLIO_CASH}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });
    if (response.ok) {
      const data = await response.json();
      return data.cash;
    }
  } catch (error) {
    console.log(error);
  }
  return 0.0;
};

export const fetchQuantity = async (symbol) => {
  try {
    const token = localStorage.getItem("jwt");
    const response = await fetch(
      `${BASE_API_URL}${ENDPOINTS.PORTFOLIO_QUANTITY}?symbol=${symbol}`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );
    if (response.ok) {
      const data = await response.json();
      return data.quantity;
    }
  } catch (error) {
    console.log(error);
  }
  return 0;
};

export const getMaxQuantity = async (action, symbol, askPrice) => {
  let quantity = 0;

  if (action === StockOrderActions.BUY) {
    // Fetch cash balance
    const cash = await fetchAvailableCash();
    quantity = Math.floor(cash / askPrice);
  } else {
    // Fetch quantity the user owns
    quantity = await fetchQuantity(symbol);
  }
  return quantity;
};

export const placeTradeOrder = async (tradeOrder) => {
  try {
    const token = localStorage.getItem("jwt");
    const response = await fetch(`${BASE_API_URL}${ENDPOINTS.TRADE}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(tradeOrder),
    });
    const data = await response.json();
    if (response.ok) return { cash: data.cash };
    else {
      if (data.errorMessage) return { errorMessage: data.errorMessage };
      else
        return {
          errorMessage: "An error occurred while placing your trade order.",
        };
    }
  } catch (error) {
    console.log(error);
    return { errorMessage: error };
  }
};
