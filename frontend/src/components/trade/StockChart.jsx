import React, { useEffect, useState } from "react";
import { ButtonGroup, Button, Box, CircularProgress } from "@mui/material";
import { StockTimeRanges } from "../../data/constants";
import { StockChartTooltip } from "./StockChartTooltip";
import {
  formatFetchedHistoryData,
  extractDataForChart,
} from "../../helpers/stockChartHelpers";
import {
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  AreaChart,
} from "recharts";

export default function StockChart(props) {
  const [timeRange, setTimeRange] = useState("1Y");
  const [priceHistory, setPriceHistory] = useState(null); // Full stock price history
  const [chartData, setChartData] = useState(null); // Stock price history displayed on the chart

  useEffect(() => {
    const fetchStockData = async () => {
      try {
        const response = await fetch(
          `https://alpha-vantage.p.rapidapi.com/query?function=TIME_SERIES_DAILY_ADJUSTED&datatype=json&symbol=${props.symbol}&outputsize=full`,
          {
            method: "GET",
            headers: {
              "X-RapidAPI-Key": props.rapidApiKey,
              "X-RapidAPI-Host": "alpha-vantage.p.rapidapi.com",
            },
          }
        );
        if (response.ok) {
          const data = await response.json();
          const formattedData = formatFetchedHistoryData(data, timeRange);
          setPriceHistory(formattedData.history);
          setChartData(formattedData.chartData);
        } else {
          alert("Can't fetch the chart data now.");
        }
      } catch (error) {
        console.error(error);
        alert(error);
      }
    };
    fetchStockData();
    // eslint-disable-next-line
  }, [props.symbol]);

  const handleTimeRangeChange = (range) => {
    if (timeRange !== range) {
      const days = StockTimeRanges[range];
      const chart = extractDataForChart(priceHistory, days, range);
      setTimeRange(range);
      setChartData(chart);
    }
  };

  return (
    <>
      <Box sx={{ display: "flex", justifyContent: "flex-end", mb: 1 }}>
        <ButtonGroup variant='text'>
          {Object.keys(StockTimeRanges).map((key, i) => (
            <Button
              onClick={() => handleTimeRangeChange(key)}
              key={i}
              sx={{
                backgroundColor: timeRange === key ? "#e6f0ff" : "transparent",
                ":hover": {
                  backgroundColor: "#e6f0ff",
                },
              }}
            >
              {key}
            </Button>
          ))}
        </ButtonGroup>
      </Box>
      {chartData ? (
        <ResponsiveContainer height='100%' width='100%'>
          <AreaChart data={chartData} margin={0}>
            <defs>
              <linearGradient id='purple' x1='0' y1='0' x2='0' y2='1'>
                <stop offset='5%' stopColor='#8884d8' stopOpacity={0.8} />
                <stop offset='95%' stopColor='#8884d8' stopOpacity={0.1} />
              </linearGradient>
            </defs>
            <XAxis dataKey='dateShort' minTickGap={20} dy={6} />
            <YAxis dy={-2} />
            <CartesianGrid strokeDasharray='3 3' />
            <Tooltip content={<StockChartTooltip />} />
            <Area
              type='monotone'
              dataKey='price'
              stroke='#8884d8'
              fillOpacity={1}
              fill='url(#purple)'
            />
          </AreaChart>
        </ResponsiveContainer>
      ) : (
        <CircularProgress sx={{ margin: "auto", mt: 5 }} />
      )}
    </>
  );
}
