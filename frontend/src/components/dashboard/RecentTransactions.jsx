import React, { useEffect, useState } from "react";
import { Box, Paper, Typography } from "@mui/material";
import { formatDateTime, formatFloat } from "../../helpers/Helpers";
import ArrowUpwardIcon from "@mui/icons-material/ArrowUpward";
import ArrowDownwardIcon from "@mui/icons-material/ArrowDownward";
import { makeStyles } from "@mui/styles";
import { BASE_API_URL, ENDPOINTS } from '../../data/constants';

const useStyles = makeStyles((theme) => ({
  transactionList: {
    display: "flex",
    flexDirection: "column",
  },
  transactionItem: {
    display: "flex",
    alignItems: "center",
    padding: theme.spacing(1),
    marginBottom: theme.spacing(1),
    borderRadius: theme.spacing(1),
    backgroundColor: "#f4f4f4",
  },
  transactionAction: {
    display: "flex",
    alignItems: "center",
  },
  actionIcon: {
    marginRight: theme.spacing(1),
  },
  buy: {
    color: "green",
  },
  sell: {
    color: "red",
  },
}));

export default function RecentTransactions() {
  const classes = useStyles();
  const [transactions, setTransactions] = useState([]);

  useEffect(() => {
    const fetchRecentTransactions = async () => {
      try {
        const token = localStorage.getItem("jwt");
        const response = await fetch(
          `${BASE_API_URL}${ENDPOINTS.TRANSACTION}?recent=true&size=3`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );
        const data = await response.json();
        if (response.ok) {
          setTransactions(data.content);
        } else {
          console.log(data);
        }
      } catch (error) {
        console.log(error);
      }
    };
    fetchRecentTransactions();
  }, []);

  const getActionIcon = (action) => {
    if (action === "Buy") {
      return (
        <ArrowUpwardIcon className={`${classes.actionIcon} ${classes.buy}`} />
      );
    } else if (action === "Sell") {
      return (
        <ArrowDownwardIcon
          className={`${classes.actionIcon} ${classes.sell}`}
        />
      );
    }
    return null;
  };

  return (
    <Box component={Paper} padding={2} width='100%' height='100%'>
      <Typography variant='h6' mb={1}>
        Recent transactions
      </Typography>
      {transactions.length === 0 ? (
        <Typography fontStyle='italic'>
          No recent transactions found.
        </Typography>
      ) : (
        <Box className={classes.transactionList}>
          {transactions.map((transaction) => (
            <Box key={transaction.id} className={classes.transactionItem}>
              <Box
                display='flex'
                gap={2}
                alignItems='center'
                flexWrap={{ xs: "wrap-reverse", md: "nowrap" }}
              >
                <Typography variant='subtitle2' fontWeight='bold' mr={1}>
                  {formatDateTime(transaction.dateTime)}
                </Typography>
                <Typography
                  variant='body1'
                  className={classes.transactionAction}
                >
                  {getActionIcon(transaction.action)}
                  {transaction.action}
                </Typography>
                <Typography variant='body1'>{transaction.symbol}</Typography>
                <Typography variant='body1'>
                  ${formatFloat(transaction.tradePrice)}
                </Typography>
              </Box>
            </Box>
          ))}
        </Box>
      )}
    </Box>
  );
}
