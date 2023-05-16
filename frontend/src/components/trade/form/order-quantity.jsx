import React from "react";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { Box, TextField, Button } from "@mui/material";

export const OrderQuantity = React.memo((props) => (
  <Box className={props.classes.rowContainer}>
    <TextField
      label='Quantity'
      type='number'
      value={props.quantity}
      onChange={props.handleQuantityChange}
      inputProps={{ min: 1 }}
      required
    />
    <Button
      variant='text'
      startIcon={<VisibilityIcon />}
      onClick={async () => await props.handleShowMax()}
    >
      Show Max
    </Button>
  </Box>
));
