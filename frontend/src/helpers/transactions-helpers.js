import { BASE_API_URL, ENDPOINTS } from '../data/constants';

export const HEADER_CELLS = [
  "#",
  "Action",
  "Symbol",
  "Name",
  "Price",
  "Quantity",
  "Total Cash Value",
  "Type",
  "Duration",
  "Date and Time",
];

export const initialStats = {
  totalCashFlow: 0.0,
  totalInvestment: 0.0,
  mostTradedStock: "",
};

export const PAGE_SIZE = 5;

export const fetchTransactionsHelper = async (
  page,
  startDate,
  endDate,
  searchTerm
) => {
  try {
    const token = localStorage.getItem("jwt");
    const response = await fetch(
      `${BASE_API_URL}${ENDPOINTS.TRANSACTION}?size=${PAGE_SIZE}&page=${page}&startDate=${startDate}&endDate=${endDate}&searchTerm=${searchTerm}`,
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
      return data;
    } else {
      return { errorMessage: data.errorMessage };
    }
  } catch (error) {
    return {
      errorMessage: "Error occurred while fetching transactions: " + error,
    };
  }
};

export const fetchStatsHelper = async () => {
  try {
    const token = localStorage.getItem("jwt");
    const response = await fetch(
      `${BASE_API_URL}${ENDPOINTS.TRANSACTION_STATS}`,
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
      return data;
    } else {
      return { errorMessage: data.errorMessage };
    }
  } catch (error) {
    return {
      errorMessage: "Error occurred while fetching stats: " + error,
    };
  }
};
