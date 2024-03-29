import React, { useState, useEffect } from "react";
import { makeStyles } from "@mui/styles";
import {
  Alert,
  Box,
  Table,
  TableContainer,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  Pagination,
  Paper,
  CircularProgress,
  Typography,
} from "@mui/material";
import { fetchHoldings } from "../../helpers/PortfolioHoldingsHelpers.jsx";
import {
  formatFloat,
  getColorStringByValue,
  getArrowDirection,
} from "../../helpers/Helpers.jsx";

const useStyles = makeStyles((theme) => ({
  tableHeaderCell: {
    fontWeight: "bold",
    backgroundColor: "#333",
    color: theme.palette.common.white,
  },
  tableRow: {
    "&:nth-of-type(odd)": {
      backgroundColor: "#fafafa",
    },
  },
}));

export default function PortfolioHoldings(props) {
  const classes = useStyles();
  const [holdings, setHoldings] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      const response = await fetchHoldings(page);
      setLoading(false);
      if (response.errorMessage) {
        setError(true);
      } else {
        setHoldings(response.holdings);
        setTotalPages(response.totalPages);
      }
    };
    fetchData();
  }, [page]);

  const handlePageChange = (value) => {
    setPage(value);
  };

  if (loading) {
    return (
      <Box display='flex' justifyContent='center'>
        <CircularProgress sx={{ margin: "auto", mt: 10 }} />
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity='error' sx={{ mt: 2 }}>
        Couldn't fetch your portfolio holdings.
      </Alert>
    );
  }

  if (holdings.length === 0) {
    return (
      <Alert severity='info' sx={{ mt: 2 }}>
        You don't have any holdings.
      </Alert>
    );
  }

  return (
    <Box sx={{ mt: 2 }}>
      <Box display='flex' gap={5}>
        <Box py={2}>
          <Typography variant='body2'>TOTAL VALUE</Typography>
          <Typography variant='h6' fontWeight='bold'>
            ${formatFloat(props.stats.totalValue)}
          </Typography>
        </Box>
        <Box py={2}>
          <Typography variant='body2'>TOTAL GAIN/LOSS</Typography>
          <Box display='flex'>
            <Typography
              variant='h6'
              fontWeight='bold'
              color={getColorStringByValue(props.stats.totalGainOrLoss)}
            >
              {props.stats.totalGainOrLoss < 0.0 && "-"}$
              {formatFloat(Math.abs(props.stats.totalGainOrLoss))}
            </Typography>
            {getArrowDirection(props.stats.totalGainOrLoss)}
          </Box>
        </Box>
      </Box>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell className={classes.tableHeaderCell}>#</TableCell>
              <TableCell className={classes.tableHeaderCell}>Symbol</TableCell>
              <TableCell className={classes.tableHeaderCell}>Name</TableCell>
              <TableCell className={classes.tableHeaderCell}>
                Current Price
              </TableCell>
              <TableCell className={classes.tableHeaderCell}>
                Quantity
              </TableCell>
              <TableCell className={classes.tableHeaderCell}>
                Total Purchase Price
              </TableCell>
              <TableCell className={classes.tableHeaderCell}>
                Total Cash Value
              </TableCell>
              <TableCell className={classes.tableHeaderCell}>
                Total Gain/Loss
              </TableCell>
              <TableCell className={classes.tableHeaderCell}>Type</TableCell>
              <TableCell className={classes.tableHeaderCell}>
                Duration
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {holdings.map((holdings, idx) => (
              <TableRow key={holdings.id} className={classes.tableRow}>
                <TableCell>{10 * page + idx + 1}</TableCell>
                <TableCell>{holdings.symbol}</TableCell>
                <TableCell>{holdings.name}</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>
                  ${formatFloat(holdings.currentPrice)}
                </TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>
                  {holdings.quantity}
                </TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>
                  ${formatFloat(holdings.purchasePrice)}
                </TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>
                  ${formatFloat(holdings.quantity * holdings.currentPrice)}
                </TableCell>
                <TableCell>{holdings.totalGainOrLoss}</TableCell>
                <TableCell>{holdings.type}</TableCell>
                <TableCell>{holdings.duration}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <Pagination
        count={totalPages}
        page={page + 1}
        onChange={(_event, page) => handlePageChange(page - 1)}
        color='primary'
        sx={{ mt: 1 }}
      />
    </Box>
  );
}
