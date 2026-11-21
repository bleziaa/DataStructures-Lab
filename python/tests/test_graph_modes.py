import tkinter as tk
import unittest
from main import Lab

class GraphModeTests(unittest.TestCase):
    def setUp(self):
        try:
            self.root = tk.Tk()
        except tk.TclError:
            self.skipTest('Requiere pantalla')
        self.root.withdraw()
        self.lab = Lab(self.root)

    def tearDown(self):
        if hasattr(self, 'root'):
            self.root.destroy()

    def enter(self, first, second=''):
        a, b = self.lab.guide_entries['Grafos']
        a.delete(0, 'end'); a.insert(0, first)
        b.delete(0, 'end'); b.insert(0, second)
        self.lab.guided_next('Grafos', a, b)

    def test_zero_edges_allowed(self):
        self.lab.guided_start('Grafos')
        for item in ('2', 'A', 'B', '0'):
            self.enter(item)
        self.assertEqual(self.lab.data['Grafos'], ['A', 'B'])
        self.assertEqual(self.lab.edges, [])
        self.assertIsNone(self.lab.guided['Grafos']['stage'])

    def test_directed_traversal(self):
        mode = tk.BooleanVar(master=self.root, value=True)
        self.lab.set_directed(mode)
        for vertex in ('A', 'B'):
            self.lab.action('Grafos', 'Vértice', vertex, '')
        self.lab.action('Grafos', 'Arista', 'A', 'B')
        self.lab.action('Grafos', 'BFS', 'B', '')
        self.assertIn('Inalcanzables: A', self.lab.views['Grafos'][1].cget('text'))
