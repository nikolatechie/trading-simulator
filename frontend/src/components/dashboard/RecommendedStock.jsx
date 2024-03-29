import React, { useEffect, useState } from "react";
import {
  Box,
  Button,
  CircularProgress,
  Paper,
  Typography,
  Divider,
} from "@mui/material";
import { formatFloat } from "../../helpers/Helpers";
import { BASE_API_URL, ENDPOINTS } from '../../data/constants';

export default function RecommendedStock() {
  const [stock, setStock] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchRecommendedStock = async () => {
      try {
        setLoading(true);
        const token = localStorage.getItem("jwt");
        const response = await fetch(
          `${BASE_API_URL}${ENDPOINTS.STOCK_RECOMMENDATION}`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setLoading(false);
        const data = await response.json();
        if (response.ok) {
          setStock(data);
        } else {
          console.log(data);
        }
      } catch (error) {
        setLoading(false);
        console.log(error);
      }
    };
    fetchRecommendedStock();
  }, []);

  return (
    <Box component={Paper} elevation={3} padding={2} height='100%'>
      <Box>
        <Typography variant='h6'>Recommended Stock</Typography>
        <Box>
          <Divider style={{ margin: "10px 0" }} />
          {loading && <CircularProgress size={30} />}
          {!loading && stock === null && (
            <Typography fontStyle='italic'>
              Failed to fetch recommended stock.
            </Typography>
          )}
          {!loading && stock !== null && (
            <Box>
              <Box display='flex'>
                <Typography variant='h6'>{stock.symbol}</Typography>
                <Box ml={3}>
                  <Typography variant='subtitle1'>{stock.name}</Typography>
                  <Typography variant='body2'>{stock.exchange}</Typography>
                  <Typography variant='h6'>
                    ${formatFloat(stock.currentPrice)}
                  </Typography>
                </Box>
              </Box>
              <Divider style={{ margin: "10px 0" }} />
              <Box display='flex' justifyContent='flex-end'>
                <Button variant='outlined' color='primary'>
                  Trade
                </Button>
              </Box>
            </Box>
          )}
        </Box>
      </Box>
    </Box>
  );
}
