import React, { useEffect, useState } from "react";
import {
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  AreaChart,
} from "recharts";
import { PortfolioChartTooltip } from "./PortfolioChartTooltip.jsx";
import { formatDateShort, formatDateLong } from "../../helpers/Helpers.jsx";
import { BASE_API_URL, ENDPOINTS, STARTING_CASH_BALANCE } from "../../data/constants.js";

const formatChartData = (chartData) => {
  const SUBTRACT_PERCENTAGE_OFFSET = 0.05;
  const ADD_PERCENTAGE_OFFSET = 0.03;
  let minVal = STARTING_CASH_BALANCE;
  let maxVal = STARTING_CASH_BALANCE;

  const formattedData = chartData.map((data) => {
    minVal = Math.min(
      minVal,
      Math.floor(data.totalValue - SUBTRACT_PERCENTAGE_OFFSET * data.totalValue)
    );
    maxVal = Math.max(
      maxVal,
      Math.ceil(data.totalValue + ADD_PERCENTAGE_OFFSET * data.totalValue)
    );
    return {
      totalValue: data.totalValue,
      dateShort: formatDateShort(data.date),
      dateLong: formatDateLong(data.date),
    };
  });

  return {
    data: formattedData,
    minVal: minVal,
    maxVal: maxVal,
  };
};

export default function PerformanceChart() {
  const [chartData, setChartData] = useState(null);

  useEffect(() => {
    const fetchStockData = async () => {
      try {
        const token = localStorage.getItem("jwt");
        const response = await fetch(
          `${BASE_API_URL}${ENDPOINTS.PORTFOLIO_HISTORY}`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );
        const data = await response.json();
        if (response.ok) {
          setChartData(formatChartData(data));
        } else {
          alert(data.errorMessage);
        }
      } catch (error) {
        console.log(error);
        alert(error);
      }
    };
    fetchStockData();
  }, []);

  if (chartData === null) return false;

  return (
    <ResponsiveContainer height='100%' width='100%'>
      <AreaChart data={chartData.data} margin={0}>
        <defs>
          <linearGradient id='purple' x1='0' y1='0' x2='0' y2='1'>
            <stop offset='5%' stopColor='#8884d8' stopOpacity={0.8} />
            <stop offset='95%' stopColor='#8884d8' stopOpacity={0.1} />
          </linearGradient>
        </defs>
        <XAxis dataKey='dateShort' minTickGap={20} dy={6} />
        <YAxis domain={[chartData.minVal, chartData.maxVal]} dy={-2} />
        <CartesianGrid strokeDasharray='3 3' />
        <Tooltip content={<PortfolioChartTooltip />} />
        <Area
          type='monotone'
          dataKey='totalValue'
          stroke='#8884d8'
          fillOpacity={1}
          fill='url(#purple)'
        />
      </AreaChart>
    </ResponsiveContainer>
  );
}
