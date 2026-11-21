import unittest
from validation import MAX_ITEMS, parse_count, parse_number


class ValidationTests(unittest.TestCase):
    def test_count_accepts_positive_integers_up_to_the_limit(self):
        self.assertEqual(parse_count('5', 'La cantidad'), 5)
        self.assertEqual(parse_count(str(MAX_ITEMS), 'La cantidad'), MAX_ITEMS)

    def test_count_rejects_invalid_values(self):
        for bad in ['0', '-3', 'abc', '2.5', '', ' ', '1_0', str(MAX_ITEMS + 1)]:
            with self.subTest(bad=bad):
                with self.assertRaises(ValueError):
                    parse_count(bad, 'La cantidad')

    def test_number_accepts_integers_in_range(self):
        self.assertEqual(parse_number(' 42 '), 42)
        self.assertEqual(parse_number('-9999'), -9999)
        self.assertEqual(parse_number('9999'), 9999)

    def test_number_rejects_text_decimals_and_out_of_range(self):
        for bad in ['', 'abc', '3.5', '1e3', '10000', '-10000']:
            with self.subTest(bad=bad):
                with self.assertRaises(ValueError):
                    parse_number(bad)


if __name__ == '__main__':
    unittest.main()
