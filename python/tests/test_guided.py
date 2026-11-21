import tkinter as tk
import unittest
from main import Lab


class GuidedLoadTests(unittest.TestCase):
    """Pruebas de la carga guiada con la interfaz real. Se omiten si no hay pantalla."""

    @classmethod
    def setUpClass(cls):
        try:
            cls.root = tk.Tk()
        except tk.TclError:
            raise unittest.SkipTest('No hay pantalla disponible para Tkinter.')
        cls.root.withdraw()

    @classmethod
    def tearDownClass(cls):
        cls.root.destroy()

    def setUp(self):
        self.lab = Lab(self.root)

    def tearDown(self):
        for child in self.root.winfo_children():
            child.destroy()

    def step(self, name, first='', second=''):
        a, b = self.lab.guide_entries[name]
        a.delete(0, 'end'); b.delete(0, 'end'); a.insert(0, first); b.insert(0, second)
        self.lab.guided_next(name, a, b)

    def test_stack_asks_quantity_then_each_value(self):
        self.lab.guided_start('Pilas')
        self.step('Pilas', '0')
        self.assertEqual(self.lab.guided['Pilas']['stage'], 'count')   # cantidad inválida
        self.step('Pilas', '3')
        self.step('Pilas', 'x')                                        # dato inválido: no avanza
        self.assertEqual(self.lab.guided['Pilas']['done'], 0)
        for value in ['3', '7', '9']:
            self.step('Pilas', value)
        self.assertEqual(self.lab.data['Pilas'], ['3', '7', '9'])

    def test_tree_rejects_duplicates_during_guided_load(self):
        self.lab.guided_start('Árboles')
        self.step('Árboles', '3')
        self.step('Árboles', '50'); self.step('Árboles', '50')
        self.assertEqual(self.lab.guided['Árboles']['done'], 1)
        self.assertEqual(self.lab.data['Árboles'], ['50'])

    def test_matrix_load_by_row_and_column(self):
        self.lab.guided_start('Matrices')
        self.step('Matrices', '2', '2')
        for value in ['1', '2', '3', '4']:
            self.step('Matrices', value)
        self.assertEqual(self.lab.data['Matrices'], [['1', '2'], ['3', '4']])

    def test_graph_load_and_unreachable_vertices(self):
        self.lab.guided_start('Grafos')
        self.step('Grafos', '3')
        for vertex in 'ABC':
            self.step('Grafos', vertex)
        self.step('Grafos', '1'); self.step('Grafos', 'A', 'B')
        self.lab.action('Grafos', 'BFS', 'A', '')
        self.assertIn('Inalcanzables: C', self.lab.views['Grafos'][1].cget('text'))


if __name__ == '__main__':
    unittest.main()
