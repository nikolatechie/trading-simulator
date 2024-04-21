import React, { useState, useRef, useEffect } from "react";
import { makeStyles } from "@mui/styles";
import { Alert, Box, Pagination } from "@mui/material";
import { TransactionsStats } from "./TransactionsStats";
import { ExportTransactions } from "./ExportTransactions.jsx";
import TransactionsFilter from "./TransactionsFilter.jsx";
import {
  fetchTransactionsHelper,
  initialStats,
  fetchStatsHelper,
} from "../../helpers/transactionsHelpers.js";
import { TransactionsTable } from "./TransactionsTable.jsx";
import { FIRST_TRANSACTION_DATE } from "../../data/constants.js";

const useStyles = makeStyles(() => ({
  rowContainer: {
    width: "80%",
    marginLeft: "auto",
    marginRight: "auto",
  },
}));

export default function TransactionsPage() {
  const classes = useStyles();
  const [stats, setStats] = useState(initialStats);
  const [transactions, setTransactions] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const today = new Date().toISOString().split("T")[0]; // Get current date in YYYY-MM-DD format
  const [startDate, setStartDate] = useState(FIRST_TRANSACTION_DATE);
  const [endDate, setEndDate] = useState(today);
  const [searchTerm, setSearchTerm] = useState("");
  const prevStartDate = useRef(FIRST_TRANSACTION_DATE.valueOf());
  const prevEndDate = useRef(today.valueOf());
  const prevSearchTerm = useRef("");

  useEffect(() => {
    const fetchStats = async () => {
      const response = await fetchStatsHelper();
      if (response.errorMessage) {
        alert(response.errorMessage);
      } else {
        setStats(response);
      }
    };
    fetchStats();
  }, []);

  const fetchTransactions = async () => {
    prevStartDate.current = startDate;
    prevEndDate.current = endDate;
    prevSearchTerm.current = searchTerm;
    const response = await fetchTransactionsHelper(
      page,
      startDate,
      endDate,
      searchTerm
    );
    if (response.errorMessage) {
      alert(response.errorMessage);
    } else {
      setTransactions(response.content);
      setTotalPages(response.totalPages);
    }
  };

  useEffect(() => {
    fetchTransactions();
    // eslint-disable-next-line
  }, [page]);

  const handlePageChange = (value) => {
    if (
      prevStartDate.current !== startDate ||
      prevEndDate.current !== endDate ||
      prevSearchTerm.current !== searchTerm
    ) {
      setPage(0);
      fetchTransactions();
    } else {
      setPage(value);
    }
  };

  const handleStartDateChange = (event) => {
    setStartDate(event.target.value);
  };

  const handleEndDateChange = (event) => {
    setEndDate(event.target.value);
  };

  const handleSearchTermChange = (event) => {
    setSearchTerm(event.target.value);
  };

  const handleSearch = async () => {
    setPage(0);
    await fetchTransactions(page, startDate, endDate, searchTerm);
  };

  return (
    <Box className={classes.rowContainer} my={5}>
      <TransactionsStats
        totalCashFlow={stats.totalCashFlow}
        totalInvestment={stats.totalInvestment}
        mostTradedStock={stats.mostTradedStock}
      />
      <TransactionsFilter
        startDate={startDate}
        handleStartDateChange={handleStartDateChange}
        endDate={endDate}
        handleEndDateChange={handleEndDateChange}
        searchTerm={searchTerm}
        handleSearchTermChange={handleSearchTermChange}
        handleSearch={handleSearch}
      />
      <TransactionsTable transactions={transactions} page={page} />
      {transactions.length === 0 ? (
        <Box width='100%'>
          <Alert severity='info'>You don't have any transactions.</Alert>
        </Box>
      ) : (
        <Box display='flex' justifyContent='space-between'>
          <Pagination
            count={totalPages}
            page={page + 1}
            onChange={(_event, page) => handlePageChange(page - 1)}
            color='primary'
            sx={{ mt: 1 }}
          />
          <ExportTransactions />
        </Box>
      )}
    </Box>
  );
}
