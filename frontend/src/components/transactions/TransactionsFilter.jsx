import React from "react";
import { Box, Button, TextField } from "@mui/material";

export default function TransactionsFilter(props) {
  return (
    <Box display='flex' alignItems='end' gap={2} mb={2}>
      <TextField
        id='start-date'
        type='date'
        label='Start date'
        variant='standard'
        value={props.startDate}
        onChange={props.handleStartDateChange}
      />
      <TextField
        id='end-date'
        type='date'
        label='End date'
        variant='standard'
        value={props.endDate}
        onChange={props.handleEndDateChange}
      />
      <TextField
        id='search-term'
        type='text'
        label='Stock symbol/name'
        variant='standard'
        value={props.searchTerm}
        onChange={props.handleSearchTermChange}
      />
      <Button variant='contained' color='primary' onClick={props.handleSearch}>
        Search
      </Button>
    </Box>
  );
}
