import React from "react";
import { Typography, Card, CardContent } from "@mui/material";
import { formatFloat } from "../../helpers/Helpers.jsx";

export const StockChartTooltip = ({ active, payload }) => {
  if (active && payload && payload.length) {
    return (
      <Card variant='outlined'>
        <CardContent>
          <Typography variant='body1'>
            Date: {payload[0].payload.dateLong}
          </Typography>
          <Typography variant='body1'>
            Price: ${formatFloat(payload[0].payload.price)}
          </Typography>
        </CardContent>
      </Card>
    );
  }
  return null;
};
