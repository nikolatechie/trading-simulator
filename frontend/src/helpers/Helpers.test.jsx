import { formatFloat } from './Helpers';

test("'1234.56' should be formated as '1,234.56'", () => {
  expect(formatFloat(1234.56)).toBe('1,234.56');
});