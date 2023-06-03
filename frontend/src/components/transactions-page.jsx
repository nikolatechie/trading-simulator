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
import { formatDateTime, formatFloat } from "../helpers/helpers.jsx";

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
  rowContainer: {
    width: "80%",
    marginLeft: "auto",
    marginRight: "auto",
    marginTop: theme.spacing(5),
  },
}));

export default function TransactionsPage() {
  const classes = useStyles();
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  useEffect(() => {
    const fetchTransactions = async () => {
      setLoading(true);
      try {
        const token = localStorage.getItem("jwt");
        const response = await fetch(
          `http://localhost:8080/api/transaction?page=${page}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );
        const data = await response.json();
        setLoading(false);
        if (response.ok) {
          setTransactions(data.content);
          setTotalPages(data.totalPages);
        } else {
          alert(data.errorMessage);
        }
      } catch (error) {
        setLoading(false);
        console.error("Error occurred while fetching transactions:", error);
      }
    };
    fetchTransactions();
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

  if (transactions.length === 0) {
    return (
      <Box className={classes.rowContainer}>
        <Alert severity='info'>You don't have any transactions.</Alert>
      </Box>
    );
  }

  return (
    <Box className={classes.rowContainer}>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell className={classes.tableHeaderCell}>#</TableCell>
              <TableCell className={classes.tableHeaderCell}>Action</TableCell>
              <TableCell className={classes.tableHeaderCell}>Symbol</TableCell>
              <TableCell className={classes.tableHeaderCell}>Name</TableCell>
              <TableCell className={classes.tableHeaderCell}>Price</TableCell>
              <TableCell className={classes.tableHeaderCell}>
                Quantity
              </TableCell>
              <TableCell className={classes.tableHeaderCell}>
                Total Cash Value
              </TableCell>
              <TableCell className={classes.tableHeaderCell}>Type</TableCell>
              <TableCell className={classes.tableHeaderCell}>
                Duration
              </TableCell>
              <TableCell className={classes.tableHeaderCell}>
                Date and Time
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {transactions.map((transaction, idx) => (
              <TableRow key={transaction.id} className={classes.tableRow}>
                <TableCell>{10 * page + idx + 1}</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>
                  {transaction.action}
                </TableCell>
                <TableCell>{transaction.symbol}</TableCell>
                <TableCell>{transaction.name}</TableCell>
                <TableCell>
                  $
                  {formatFloat(
                    transaction.purchasePrice / transaction.quantity
                  )}
                </TableCell>
                <TableCell>{transaction.quantity}</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>
                  ${formatFloat(transaction.purchasePrice)}
                </TableCell>
                <TableCell>{transaction.type}</TableCell>
                <TableCell>{transaction.duration}</TableCell>
                <TableCell>{formatDateTime(transaction.dateTime)}</TableCell>
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
