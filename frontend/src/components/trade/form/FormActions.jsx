import React from "react";
import { Box, Button } from "@mui/material";
import KeyboardArrowRightIcon from "@mui/icons-material/KeyboardArrowRight";

export const FormActions = React.memo((props) => (
  <Box className={props.classes.rowContainer} sx={{ mt: 1 }}>
    <Button
      variant='outlined'
      size='large'
      sx={{ maxWidth: 300 }}
      onClick={() => props.handleClearOrder()}
    >
      Clear Order
    </Button>
    <Button
      variant='contained'
      type='submit'
      size='large'
      endIcon={<KeyboardArrowRightIcon />}
      sx={{ maxWidth: 300 }}
    >
      Preview Order
    </Button>
  </Box>
));
