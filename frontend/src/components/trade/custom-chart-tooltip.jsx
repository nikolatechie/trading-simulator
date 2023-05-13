import React from "react";
import { Typography, Card, CardContent } from "@mui/material";

export const CustomChartTooltip = ({ active, payload }) => {
  if (active && payload && payload.length) {
    return (
      <Card variant='outlined'>
        <CardContent>
          <Typography variant='body1'>
            Date: {payload[0].payload.dateLong}
          </Typography>
          <Typography variant='body1'>
            Price: {payload[0].payload.price}
          </Typography>
        </CardContent>
      </Card>
    );
  }
};
