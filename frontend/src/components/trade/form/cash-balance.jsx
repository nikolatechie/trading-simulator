import React from "react";
import { Box, Typography } from "@mui/material";
import { formatCash } from "../../../helpers/trade-form-helpers";

export const CashBalance = React.memo(({ cash }) => (
  <Box display='flex'>
    <Typography variant='h5' sx={{ mr: 1 }}>
      Cash:
    </Typography>
    <Typography variant='h5' fontWeight='bold'>
      ${formatCash(cash)}
    </Typography>
  </Box>
));
