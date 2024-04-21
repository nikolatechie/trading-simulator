import React, { useEffect, useRef, useReducer } from "react";
import { makeStyles } from "@mui/styles";
import {
  Alert,
  Button,
  Box,
  Table,
  TableContainer,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  TextField,
  Paper,
  Pagination,
  CircularProgress,
} from "@mui/material";
import { PAGE_SIZE, ActionTypes, BASE_API_URL, ENDPOINTS } from "../../data/constants";
import {
  reducer,
  initialState,
  columns,
} from "../../helpers/stockSearchHelpers";

const useStyles = makeStyles((theme) => ({
  search: {
    marginBottom: theme.spacing(2),
    marginRight: theme.spacing(2),
  },
  pagination: {
    marginTop: theme.spacing(1),
  },
  tableHeaderCell: {
    fontWeight: "bold",
    backgroundColor: "#333",
    color: theme.palette.common.white,
  },
  tableRow: {
    "&:nth-of-type(odd)": {
      backgroundColor: "#fafafa",
    },
    "&:hover": {
      backgroundColor: theme.palette.action.hover,
      cursor: "pointer",
    },
  },
}));

export default function StockSearch(props) {
  const classes = useStyles();
  const [state, dispatch] = useReducer(reducer, initialState);
  const prevSearchTerm = useRef("");
  const searchTerm = useRef("");

  const fetchStockInfoList = async () => {
    prevSearchTerm.current = searchTerm.current.value;
    dispatch({ type: ActionTypes.FETCH_START });
    try {
      const token = localStorage.getItem("jwt");
      const searchParam =
        searchTerm.current.value !== ""
          ? `&search=${searchTerm.current.value}`
          : "";
      const url = `${BASE_API_URL}${ENDPOINTS.STOCKS}?page=${state.page}${searchParam}`;
      const response = await fetch(url, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });
      const data = await response.json();
      if (response.ok) {
        dispatch({ type: ActionTypes.FETCH_SUCCESS, payload: data });
      } else {
        dispatch({ type: ActionTypes.FETCH_FAILURE });
      }
    } catch (err) {
      console.log(err);
      dispatch({ type: ActionTypes.FETCH_FAILURE });
    }
  };

  useEffect(() => {
    fetchStockInfoList();
    // eslint-disable-next-line
  }, [state.page]);

  const handlePageChange = (value) => {
    if (searchTerm.current.value !== prevSearchTerm.current) {
      // Fetch the first page of the stock if the searchTerm has changed since the last fetch
      dispatch({
        type: ActionTypes.SET_PAGE,
        payload: 0,
      });
      fetchStockInfoList();
    } else {
      dispatch({ type: ActionTypes.SET_PAGE, payload: value - 1 });
    }
  };

  const handleSearch = () => {
    dispatch({ type: ActionTypes.SET_PAGE, payload: 0 });
    fetchStockInfoList();
  };

  return (
    <>
      <Box
        sx={{
          display: "flex",
          flexDirection: "row",
          alignItems: "center",
          mt: 1,
        }}
      >
        <TextField
          className={classes.search}
          variant='standard'
          label='Stock symbol/name'
          inputRef={searchTerm}
        />
        <Button
          variant='contained'
          onClick={handleSearch}
          disabled={state.isFetching}
          sx={{ marginBottom: "5px" }}
        >
          Search
        </Button>
        {state.isFetching && (
          <CircularProgress size={30} sx={{ ml: 2, mb: 0.5 }} />
        )}
      </Box>
      <TableContainer component={Paper} sx={{ maxHeight: 425 }}>
        <Table stickyHeader>
          <TableHead>
            <TableRow>
              {columns.map((column) => (
                <TableCell
                  key={column.headerName}
                  className={classes.tableHeaderCell}
                >
                  {column.headerName}
                </TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {state.stocks.map((row, index) => (
              <TableRow
                key={row.id}
                className={classes.tableRow}
                onClick={() =>
                  props.handleSelectStock({
                    symbol: row.symbol,
                    name: row.name,
                    exchange: row.exchange,
                    currency: row.currency,
                  })
                }
              >
                <TableCell>{PAGE_SIZE * state.page + index + 1}</TableCell>
                {columns.slice(1).map((column) => (
                  <TableCell
                    key={column.field}
                    sx={{
                      fontWeight: column.field === "symbol" ? "bold" : "normal",
                    }}
                  >
                    {row[column.field]}
                  </TableCell>
                ))}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <Pagination
        className={classes.pagination}
        count={state.totalPages}
        page={state.page + 1}
        onChange={(_event, page) => handlePageChange(page)}
        color='primary'
        hidden={state.stocks.length === 0 || !state.canFetchStocks}
      />
      {!state.canFetchStocks ? (
        <Alert severity='error'>Couldn't fetch stock data.</Alert>
      ) : state.stocks.length === 0 && !state.isFetching ? (
        <Alert severity='info'>No stocks match the given pattern.</Alert>
      ) : null}
    </>
  );
}
