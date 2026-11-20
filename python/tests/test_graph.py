import unittest
from main import Lab


class GraphTests(unittest.TestCase):
    def test_directed_bfs_and_dfs_show_unreachable_vertices(self):
        vertices = ['A', 'B', 'C', 'D', 'E']
        edges = [('A', 'B'), ('A', 'C'), ('B', 'D'), ('C', 'D')]
        self.assertEqual(Lab.graph_walk(vertices, edges, 'A', True, False),
                         ([(None, 'A'), ('A', 'B'), ('A', 'C'), ('B', 'D')], ['E']))
        self.assertEqual(Lab.graph_walk(vertices, edges, 'A', True, True),
                         ([(None, 'A'), ('A', 'B'), ('B', 'D'), ('A', 'C')], ['E']))

    def test_undirected_search_follows_a_connection_in_reverse(self):
        self.assertEqual(Lab.graph_walk(['A', 'B'], [('A', 'B')], 'B', False, False),
                         ([(None, 'B'), ('B', 'A')], []))
        self.assertEqual(Lab.graph_walk(['A', 'B'], [('A', 'B')], 'B', True, False),
                         ([(None, 'B')], ['A']))
