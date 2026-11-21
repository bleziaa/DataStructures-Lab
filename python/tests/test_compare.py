import unittest
from compare import StackQueueComparison


class StackQueueComparisonTests(unittest.TestCase):
    def test_same_input_gives_opposite_output_order(self):
        model = StackQueueComparison()
        for value in ['3', '7', '9']:
            model.add(value)
        model.remove_all()
        self.assertEqual(model.stack_out, ['9', '7', '3'])   # LIFO
        self.assertEqual(model.queue_out, ['3', '7', '9'])   # FIFO
        self.assertIn('LIFO', model.explanation())

    def test_remove_one_takes_top_and_front(self):
        model = StackQueueComparison()
        for value in ['1', '2']:
            model.add(value)
        self.assertEqual(model.remove_one(), ('2', '1'))

    def test_removing_from_empty_structures_raises(self):
        with self.assertRaises(ValueError):
            StackQueueComparison().remove_one()
        with self.assertRaises(ValueError):
            StackQueueComparison().remove_all()

    def test_new_experiment_starts_after_emptying(self):
        model = StackQueueComparison()
        model.add('1'); model.remove_all(); model.add('5')
        self.assertEqual((model.entry, model.stack_out, model.queue_out), (['5'], [], []))


if __name__ == '__main__':
    unittest.main()
