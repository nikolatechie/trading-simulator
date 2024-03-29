import { ActionTypes } from "../data/constants";

export const columns = [
  { headerName: "#" },
  { field: "symbol", headerName: "Symbol" },
  { field: "name", headerName: "Name" },
  { field: "exchange", headerName: "Exchange" },
  { field: "country", headerName: "Country" },
  { field: "currency", headerName: "Currency" },
  { field: "mic_code", headerName: "MIC" },
];

export const initialState = {
  stocks: [],
  page: 0,
  totalPages: 1,
  canFetchStocks: true,
  isFetching: false,
};

export const reducer = (state, action) => {
  switch (action.type) {
    case ActionTypes.FETCH_START:
      return { ...state, isFetching: true };
    case ActionTypes.FETCH_SUCCESS:
      return {
        ...state,
        isFetching: false,
        canFetchStocks: true,
        totalPages: action.payload.totalPages,
        stocks: action.payload.content,
      };
    case ActionTypes.FETCH_FAILURE:
      return {
        ...state,
        canFetchStocks: false,
        isFetching: false,
      };
    case ActionTypes.SET_PAGE:
      return {
        ...state,
        page: action.payload,
      };
    default:
      throw new Error("Invalid reducer action type:", action.type);
  }
};
