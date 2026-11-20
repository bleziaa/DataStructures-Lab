import unittest
from main import Lab

class TreeTests(unittest.TestCase):
    def test_traversals_preserve_insertion_shape(self):
        data=['8','3','10','1','6','14','4','7','13']
        self.assertEqual(Lab.tree_traversal(data,'Inorden'),['1','3','4','6','7','8','10','13','14'])
        self.assertEqual(Lab.tree_traversal(data,'Preorden'),['8','3','1','6','4','7','10','14','13'])
        self.assertEqual(Lab.tree_traversal(data,'Postorden'),['1','4','7','6','3','13','14','10','8'])
if __name__=='__main__':unittest.main()

class TreeDeletionTests(unittest.TestCase):
    def test_deleting_root_keeps_subtrees_and_promotes_successor(self):
        tree = ['8', '3', '10', '1', '6', '14', '4', '7', '13', '9']
        remaining = Lab.delete_tree_node(tree, '8')
        self.assertEqual(Lab.tree_traversal(remaining, 'Preorden'),
                         ['9', '3', '1', '6', '4', '7', '10', '14', '13'])
