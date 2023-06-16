import React from "react";
import { Box, Button, Typography } from "@mui/material";

export const ExportTransactions = () => {
  return (
    <Box display='flex' alignItems='center' gap={1} mt={1}>
      <Typography>Export as:</Typography>
      <Button variant='outlined'>CSV</Button>
      <Button variant='outlined'>PDF</Button>
    </Box>
  );
};
