import React from "react";
import { Box, Typography } from "@mui/material";
import { formatFloat } from "../../../helpers/Helpers.jsx";

export const CashBalance = React.memo(({ cash }) => (
  <Box display='flex'>
    <Typography variant='h5' sx={{ mr: 1 }}>
      Cash:
    </Typography>
    <Typography variant='h5' fontWeight='bold'>
      ${formatFloat(cash)}
    </Typography>
  </Box>
));
