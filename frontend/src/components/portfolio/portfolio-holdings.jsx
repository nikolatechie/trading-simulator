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
} from "@mui/material";
import { fetchHoldings } from "../../helpers/portfolio-holdings-helpers.jsx";
import { formatFloat } from "../../helpers/helpers";

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

export default function PortfolioHoldings() {
  const classes = useStyles();
  const [holdings, setHoldings] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      const response = await fetchHoldings(page);
      setLoading(false);
      if (response.errorMessage) {
        alert(response.errorMessage);
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

  return (
    <Box>
      {loading && (
        <Box display='flex' justifyContent='center'>
          <CircularProgress sx={{ margin: "auto", mt: 10 }} />
        </Box>
      )}
      <TableContainer component={Paper} hidden={holdings.length === 0}>
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
                Purchase Price
              </TableCell>
              <TableCell className={classes.tableHeaderCell}>
                Quantity
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
                  ${formatFloat(holdings.purchasePrice / holdings.quantity)}
                </TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>
                  {holdings.quantity}
                </TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>
                  ${formatFloat(holdings.purchasePrice)}
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
        hidden={holdings.length === 0}
        color='primary'
        sx={{ mt: 1 }}
      />
      {holdings.length === 0 && !loading && (
        <Alert severity='info'>You don't have any holdings.</Alert>
      )}
    </Box>
  );
}
