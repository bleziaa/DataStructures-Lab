import unittest
from linked_list import LinkedList

class LinkedListTests(unittest.TestCase):
    def test_append_search_and_remove(self):
        values = LinkedList(['1', '2', '3'])
        self.assertEqual(list(values), ['1', '2', '3'])
        self.assertEqual(values.index('2'), 1)
        self.assertEqual(values.pop(1), '2')
        self.assertEqual(list(values), ['1', '3'])
        values.clear()
        self.assertFalse(values)
        values.append('4')
        self.assertEqual(list(values), ['4'])

    def test_insert_at_positions(self):
        values = LinkedList()
        values.insert(0, '2')
        values.insert(0, '1')
        values.insert(2, '3')
        self.assertEqual(list(values), ['1', '2', '3'])
        self.assertEqual(values.pop(), '3')
        self.assertEqual(values.pop(0), '1')
        self.assertEqual(values.pop(), '2')
        self.assertEqual(len(values), 0)
