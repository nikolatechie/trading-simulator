import { MONTHS_SHORT } from "../data/constants";

export const formatDateTime = (dateTime) => {
  const year = dateTime.substring(0, 4);
  const month = Number(dateTime.substring(5, 7));
  const day = dateTime.substring(8, 10);
  const hour = dateTime.substring(11, 13);
  const min = dateTime.substring(14, 16);
  return `${day}-${MONTHS_SHORT[month]}-${year} at ${hour}:${min}`;
};
