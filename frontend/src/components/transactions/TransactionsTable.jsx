import React from "react";
import { makeStyles } from "@mui/styles";
import {
  Table,
  TableContainer,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  Paper,
} from "@mui/material";
import { formatDateTime, formatFloat } from "../../helpers/Helpers.jsx";
import { HEADER_CELLS, PAGE_SIZE } from "../../helpers/transactionsHelpers.js";

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

export const TransactionsTable = ({ transactions, page }) => {
  const classes = useStyles();
  return (
    <TableContainer component={Paper} sx={{ mt: 1 }}>
      <Table>
        <TableHead>
          <TableRow>
            {HEADER_CELLS.map((cell, idx) => (
              <TableCell key={idx} className={classes.tableHeaderCell}>
                {cell}
              </TableCell>
            ))}
          </TableRow>
        </TableHead>
        <TableBody>
          {transactions.map((transaction, idx) => (
            <TableRow key={transaction.id} className={classes.tableRow}>
              <TableCell>{PAGE_SIZE * page + idx + 1}</TableCell>
              <TableCell sx={{ fontWeight: "bold" }}>
                {transaction.action}
              </TableCell>
              <TableCell>{transaction.symbol}</TableCell>
              <TableCell>{transaction.name}</TableCell>
              <TableCell>
                ${formatFloat(transaction.tradePrice / transaction.quantity)}
              </TableCell>
              <TableCell>{transaction.quantity}</TableCell>
              <TableCell sx={{ fontWeight: "bold" }}>
                ${formatFloat(transaction.tradePrice)}
              </TableCell>
              <TableCell>{transaction.type}</TableCell>
              <TableCell>{transaction.duration}</TableCell>
              <TableCell>{formatDateTime(transaction.dateTime)}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};
