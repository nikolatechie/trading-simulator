import { StockTimeRanges } from "../data/constants.js";
import { formatDateShort, formatDateLong } from "./Helpers.jsx";

// Called after fetching data from the API
// Formats data and returns it to set state
export const formatFetchedHistoryData = (data, timeRange) => {
  const timeSeries = data["Time Series (Daily)"];
  StockTimeRanges.MAX = Object.keys(timeSeries).length;
  const days = StockTimeRanges[timeRange];
  const dates = Object.keys(timeSeries);
  const formattedData = dates.map((date) => ({
    dateLong: formatDateLong(date),
    dateShort: formatDateShort(date),
    price: parseFloat(timeSeries[date]["5. adjusted close"]),
  }));
  const chartData = extractDataForChart(formattedData, days, timeRange);
  return {
    history: formattedData,
    chartData,
  };
};

// Extracts prices and dates from the specified last number of days
export const extractDataForChart = (data, days, timeRange) => {
  // Rearrange the data in ascending order based on the date
  data = data.slice(0, days).reverse();
  if (timeRange === "MAX") return data;
  const minDate = new Date();
  minDate.setDate(minDate.getDate() - days);
  // Dates are in ascending order, so binary search is used
  let left = 0;
  let right = data.length - 1;
  let startDateIdx = right;

  while (left <= right) {
    const mid = Math.floor((left + right) / 2);
    const midDate = new Date(data[mid].dateLong);

    if (midDate >= minDate) {
      startDateIdx = Math.min(startDateIdx, mid);
      right = mid - 1;
    } else {
      left = mid + 1;
    }
  }

  return data.slice(startDateIdx);
};
