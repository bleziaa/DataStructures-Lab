import math
import sys
import tkinter as tk
from tkinter import ttk
from collections import deque

from linked_list import LinkedList

BG='#10131d'; PANEL='#1c2131'; PURPLE='#a78bfa'; TEXT='#f5f3ff'

class Lab:
    def __init__(self, root):
        root.title('DataStructures Lab — Python'); root.geometry('1100x720'); root.minsize(770, 460); root.configure(bg=BG)
        self.data={k:[] for k in ['Arreglos','Matrices','Listas','Pilas','Colas','Árboles','Grafos']}
        self.data['Listas'] = LinkedList()
        self.edges = []
        self.directed = False
        self.animation_jobs = {}
        self.active_nodes = {}
        self.pulses = {}
        self.centers = {}
        style = ttk.Style(root)
        style.theme_use('clam')
        style.configure('TNotebook', background=BG, borderwidth=0)
        style.configure('TNotebook.Tab', background=PANEL, foreground=TEXT, padding=(12, 8))
        style.map('TNotebook.Tab', background=[('selected', '#493574')])
        tk.Label(root,text='DATASTRUCTURES LAB  •  PYTHON',bg=BG,fg=PURPLE,font=('Segoe UI',19,'bold')).pack(pady=14)
        self.tabs=ttk.Notebook(root);self.tabs.pack(fill='both',expand=True,padx=15,pady=10)
        self.views={}
        for name in self.data:
            frame=tk.Frame(self.tabs,bg=PANEL);self.tabs.add(frame,text=name)
            self.make_tab(frame,name)
        self.tabs.bind('<<NotebookTabChanged>>',lambda e:self.draw(self.tabs.tab(self.tabs.select(),'text')))
        self.draw('Arreglos')
    def make_tab(self,f,name):
        top=tk.Frame(f,bg=PANEL);top.pack(fill='x',padx=12,pady=12)
        tk.Label(top,text='Filas / valor:' if name == 'Matrices' else 'Valor / vértice:',bg=PANEL,fg=TEXT).pack(side='left')
        entry=tk.Entry(top,width=15);entry.pack(side='left',padx=6)
        tk.Label(top,text='Columnas / fila,columna:' if name == 'Matrices' else 'Segundo valor / índice:',bg=PANEL,fg=TEXT).pack(side='left')
        extra=tk.Entry(top,width=13);extra.pack(side='left',padx=6)
        ops=['Insertar','Eliminar','Buscar','Recorrer','Reiniciar']
        if name=='Pilas':ops=['PUSH','POP','PEEK','Reiniciar']
        if name=='Colas':ops=['ENQUEUE','DEQUEUE','PEEK','Reiniciar']
        if name=='Grafos':
            ops=['Vértice','Arista','Quitar vértice','Quitar arista','BFS','DFS','Vecinos','Reiniciar']
            mode = tk.BooleanVar(value=self.directed)
            toggle = tk.Checkbutton(f, text='Grafo dirigido', variable=mode, bg=PANEL, fg=TEXT,
                                    selectcolor=BG, activebackground=PANEL, activeforeground=TEXT,
                                    command=lambda: self.set_directed(mode))
            toggle.pack(anchor='w', padx=12)
        controls = tk.Frame(f, bg=PANEL)
        controls.pack(fill='x', padx=12)
        if name=='Árboles':ops=['Insertar','Eliminar','Buscar','Inorden','Preorden','Postorden','Reiniciar']
        if name=='Matrices':ops=['Crear (filas, columnas)','Asignar','Consultar','Recorrer','Reiniciar']
        for op in ops:
            tk.Button(controls,text=op,bg='#383057',fg='white',relief='flat',command=lambda o=op,n=name:self.action(n,o,entry.get(),extra.get())).pack(side='left',padx=3)
        viewport = tk.Frame(f, bg=PANEL)
        viewport.pack(fill='both', expand=True, padx=12, pady=8)
        canvas = tk.Canvas(viewport, bg=BG, highlightthickness=0)
        vertical = ttk.Scrollbar(viewport, orient='vertical', command=canvas.yview)
        horizontal = ttk.Scrollbar(viewport, orient='horizontal', command=canvas.xview)
        canvas.configure(xscrollcommand=horizontal.set, yscrollcommand=vertical.set)
        canvas.grid(row=0, column=0, sticky='nsew')
        vertical.grid(row=0, column=1, sticky='ns')
        horizontal.grid(row=1, column=0, sticky='ew')
        viewport.rowconfigure(0, weight=1)
        viewport.columnconfigure(0, weight=1)
        status=tk.Label(f,text='Introduce datos y elige una operación.',bg=PANEL,fg=PURPLE,anchor='w',justify='left',wraplength=680,font=('Segoe UI',11));status.pack(fill='x',padx=14,pady=12)
        canvas.bind('<Configure>',lambda e,n=name:self.draw(n))
        self.views[name]=(canvas,status); self.active_nodes[name]=set()
    def set_directed(self, variable):
        if self.edges:
            variable.set(self.directed)
            self.views['Grafos'][1].config(text='Quita las aristas antes de cambiar el tipo de grafo.')
            return
        self.directed = variable.get()
        self.draw('Grafos')

    @staticmethod
    def graph_walk(vertices, edges, start, directed, depth_first):
        adjacency = {node: [] for node in vertices}
        for source, target in edges:
            adjacency[source].append(target)
            if not directed:
                adjacency[target].append(source)
        queue = deque([(None, start)])
        seen = set()
        steps = []
        while queue:
            parent, current = queue.pop() if depth_first else queue.popleft()
            if current in seen:
                continue
            seen.add(current)
            steps.append((parent, current))
            children = reversed(adjacency[current]) if depth_first else adjacency[current]
            queue.extend((current, child) for child in children if child not in seen)
        return steps, [node for node in vertices if node not in seen]

    @staticmethod
    def required(value):
        if not value:
            raise ValueError('Escribe un valor.')
        if len(value) > 32:
            raise ValueError('Usa máximo 32 caracteres.')
        return value

    @staticmethod
    def integer(value, label):
        try:
            return int(value)
        except ValueError:
            raise ValueError(f'{label} debe ser un número entero.') from None

    def graph_action(self, data, operation, first, second):
        if operation == 'Vértice':
            if self.required(first) in data:
                raise ValueError('El vértice ya existe.')
            if len(data) >= 32:
                raise ValueError('El grafo admite hasta 32 vértices.')
            data.append(first)
            return f'Vértice {first} creado.', [(None, first)]
        if first not in data:
            raise ValueError('El primer vértice no existe.')
        if operation == 'Quitar vértice':
            data.remove(first)
            self.edges[:] = [edge for edge in self.edges if first not in edge]
            return f'Vértice {first} y sus aristas eliminados.', []
        if operation in ('Arista', 'Quitar arista'):
            if second not in data or first == second:
                raise ValueError('Indica dos vértices distintos que existan.')
            pair = (first, second)
            existing = pair if pair in self.edges else None
            if existing is None and not self.directed and pair[::-1] in self.edges:
                existing = pair[::-1]
            if operation == 'Arista':
                if existing:
                    raise ValueError('La arista ya existe.')
                self.edges.append(pair)
                return f'Arista {first} → {second} creada.', [pair]
            if existing is None:
                raise ValueError('La arista no existe.')
            self.edges.remove(existing)
            return f'Arista {first} → {second} eliminada.', []
        if operation == 'Vecinos':
            neighbors = [v if u == first else u for u, v in self.edges
                         if u == first or (not self.directed and v == first)]
            return 'Vecinos: ' + (', '.join(neighbors) or 'ninguno'), [(first, node) for node in neighbors]
        steps, unreachable = self.graph_walk(data, self.edges, first, self.directed, operation == 'DFS')
        order = ' → '.join(node for _, node in steps)
        return f'{operation}: {order} | Inalcanzables: {", ".join(unreachable) or "ninguno"}', steps

    def matrix_action(self, data, operation, first, second):
        if operation.startswith('Crear'):
            rows = self.integer(first, 'Filas')
            cols = self.integer(second, 'Columnas')
            if not 1 <= rows <= 8 or not 1 <= cols <= 12:
                raise ValueError('Usa 1–8 filas y 1–12 columnas.')
            data[:] = [[None] * cols for _ in range(rows)]
            return f'Matriz {rows}×{cols} creada.', []
        if not data:
            raise ValueError('Crea una matriz primero.')
        if operation == 'Recorrer':
            cells = [(row, col) for row, values in enumerate(data) for col in range(len(values))]
            return 'Recorrido fila por fila.', [(None, cell) for cell in cells]
        parts = second.split(',')
        if len(parts) != 2:
            raise ValueError('Escribe fila,columna en el segundo campo.')
        row, col = (self.integer(part, label) for part, label in zip(parts, ('Fila', 'Columna')))
        if not (0 <= row < len(data) and 0 <= col < len(data[0])):
            raise ValueError('Coordenadas fuera de rango. Los índices empiezan en cero.')
        if operation == 'Asignar':
            data[row][col] = self.required(first)
        value = data[row][col]
        return f'Celda ({row},{col}): {value if value is not None else "vacía"}.', [(None, (row, col))]

    def linear_action(self, name, data, operation, first, second):
        tree = name == 'Árboles'
        if operation in ('Inorden', 'Preorden', 'Postorden'):
            order = self.tree_traversal(data, operation)
            return f'{operation}: ' + (' → '.join(order) or 'vacío'), [(None, item) for item in order]
        if operation in ('Insertar', 'PUSH', 'ENQUEUE'):
            value = self.required(first)
            if tree:
                value = str(self.integer(value, 'El valor del árbol'))
                if value in data:
                    raise ValueError('El valor ya existe en el árbol.')
            index = self.integer(second, 'El índice') if name == 'Arreglos' and second else len(data)
            if not 0 <= index <= len(data):
                raise ValueError(f'Índice fuera de rango. Usa de 0 a {len(data)}.')
            if tree and len(data) >= 80:
                raise ValueError('El árbol admite hasta 80 nodos.')
            if name == 'Listas':
                data.append(value)
            else:
                data.insert(index, value)
            return f'Insertado: {value}.', [(None, value if tree else index)]
        if not data:
            raise ValueError('La estructura está vacía.')
        if operation in ('POP', 'DEQUEUE', 'PEEK'):
            index = len(data) - 1 if name == 'Pilas' else 0
            if operation == 'PEEK':
                return f'Próximo: {data[index]}.', [(None, index)]
            return f'Extraído: {data.pop(index)}.', []
        if operation == 'Recorrer':
            return ' → '.join(data), [(index - 1 if index else None, index) for index in range(len(data))]
        value = self.required(first)
        if tree:
            value = str(self.integer(value, 'El valor del árbol'))
        if value not in data:
            raise ValueError('Valor no encontrado.')
        index = data.index(value)
        if operation == 'Eliminar':
            if tree:
                data[:] = self.delete_tree_node(data, value)
            else:
                data.pop(index)
            return f'Eliminado: {value}.', []
        message = f'Encontrado: {value}.' if tree else f'Encontrado en posición {index}.'
        return message, [(None, value if tree else index)]

    def action(self, name, operation, first, second):
        data = self.data[name]
        first, second = first.strip(), second.strip()
        self.cancel_animation(name)
        removal = None
        if operation in ('Eliminar', 'POP', 'DEQUEUE', 'Quitar vértice') and data:
            if name == 'Grafos':
                key = first
            elif name == 'Árboles':
                try:
                    key = str(int(first))
                except ValueError:
                    key = None
            elif operation in ('POP', 'DEQUEUE'):
                key = len(data) - 1 if operation == 'POP' else 0
            else:
                key = data.index(first) if first in data else None
            removal = self.centers.get(name, {}).get(key)
        try:
            if operation == 'Reiniciar':
                data.clear()
                if name == 'Grafos':
                    self.edges.clear()
                message, steps = 'Estructura reiniciada.', []
            elif name == 'Grafos':
                message, steps = self.graph_action(data, operation, first, second)
            elif name == 'Matrices':
                message, steps = self.matrix_action(data, operation, first, second)
            else:
                message, steps = self.linear_action(name, data, operation, first, second)
        except ValueError as error:
            message, steps = f'⚠ {error}', []
            removal = None
        self.views[name][1].config(text=message)
        self.draw(name)
        if steps:
            self.animate(name, steps)
        elif removal:
            self.animate_removal(name, removal)

    def draw(self,n):
        if n not in self.views:return
        c,_=self.views[n];c.delete('all');d=self.data[n];w=max(c.winfo_width(),600);h=max(c.winfo_height(),350)
        self.centers[n] = {}
        c.configure(scrollregion=(0, 0, w, h))
        if not d:c.create_text(w/2,h/2,text='Sin datos • Inserta elementos para visualizar los nodos',fill='#aaa4c5',font=('Segoe UI',15));return
        if n=='Matrices':
            c.configure(scrollregion=(0, 0, max(w, 130 + len(d[0]) * 75), max(h, 70 + len(d) * 60)))
            for r,row in enumerate(d):
                for j,v in enumerate(row):
                    x=60+j*75;y=55+r*60;c.create_rectangle(x,y,x+64,y+45,fill='#0f766e' if (r,j) in self.active_nodes[n] else '#352951',outline=PURPLE,width=2);c.create_text(x+32,y+22,text=str(v)[:9] if v is not None else '·',fill=TEXT)
            return
        if n=='Grafos':
            extent = max(350, int(32 * len(d)))
            w, h = max(w, extent), max(h, extent)
            c.configure(scrollregion=(0, 0, w, h))
            pos={v:(w/2+min(w,h)*.33*math.cos(2*math.pi*i/len(d)),h/2+min(w,h)*.33*math.sin(2*math.pi*i/len(d))) for i,v in enumerate(d)}
            for u,v in self.edges:
                self.edge(c, n, u, v, pos[u], pos[v], arrow=self.directed,
                          curved=self.directed and (v,u) in self.edges)
            for v,(x,y) in pos.items():
                self.centers[n][v] = (x, y, v)
                self.node(c,x,y,v,v in self.active_nodes[n])
            return
        if n=='Árboles':
            order = self.tree_traversal(d, 'Inorden')
            width = max(w, 84 * (len(d) + 1))
            positions = {item: (width * (i + 1) / (len(d) + 1), 0) for i, item in enumerate(order)}
            def paint(values, depth):
                if not values:
                    return
                value, *children = values
                x, _ = positions[value]
                y = 50 + depth * 80
                positions[value] = (x, y)
                for side in ([v for v in children if int(v) < int(value)],
                             [v for v in children if int(v) > int(value)]):
                    if side:
                        target, _ = positions[side[0]]
                        self.edge(c, n, value, side[0], (x, y), (target, y + 80))
                        paint(side, depth + 1)
                self.centers[n][value] = (x, y, value)
                self.node(c, x, y, value, value in self.active_nodes[n])
            paint(d, 0)
            c.configure(scrollregion=(0, 0, width, max(h, max(y for _, y in positions.values()) + 45)))
            return
        columns = max(1, (w - 40) // 95)
        positions = [(60 + (i % columns) * 95, 60 + (i // columns) * 100) for i in range(len(d))]
        if n == 'Pilas':
            h = max(h, 100 + len(d) * 65)
            positions = [(w / 2, h - 55 - i * 65) for i in range(len(d))]
        c.configure(scrollregion=(0, 0, w, max(h, max(y for _, y in positions) + 75)))
        for i, ((x, y), value) in enumerate(zip(positions, d)):
            if i and n in ('Listas', 'Colas', 'Arreglos'):
                self.edge(c, n, i - 1, i, positions[i - 1], positions[i], arrow=True)
            self.centers[n][i] = (x, y, value)
            self.node(c, x, y, value, i in self.active_nodes[n])
            if n == 'Arreglos':
                c.create_text(x, y + 39, text=str(i), fill='#aaa4c5')
        if n == 'Pilas':
            c.create_text(w / 2 + 87, positions[-1][1], text='← TOP', fill=PURPLE)
        if n == 'Colas':
            for label, (x, y), offset in [('FRONT', positions[0], -40), ('REAR', positions[-1], 40)]:
                c.create_text(x, y + offset, text=label, fill=PURPLE)

    def edge(self, canvas, name, source, target, start, end, arrow=False, curved=False):
        x, y = start
        a, b = end
        length = math.hypot(a - x, b - y) or 1
        dx, dy = (a - x) / length, (b - y) / length
        x, y, a, b = x + 28 * dx, y + 28 * dy, a - 31 * dx, b - 31 * dy
        bend = 23 if curved else 0
        mid = ((x + a) / 2 - dy * bend, (y + b) / 2 + dx * bend)
        pulse = self.pulses.get(name)
        active = pulse and pulse[:2] == (source, target)
        if pulse and name == 'Grafos' and not self.directed and pulse[:2] == (target, source):
            active = True
        canvas.create_line(x, y, *mid, a, b, smooth=True, fill='#5eead4' if active else '#657b9f',
                           width=3 if active else 2, arrow='last' if arrow else 'none')
        if active:
            t = pulse[2] if pulse[:2] == (source, target) else 1 - pulse[2]
            px = (1-t)**2*x + 2*t*(1-t)*mid[0] + t*t*a
            py = (1-t)**2*y + 2*t*(1-t)*mid[1] + t*t*b
            canvas.create_oval(px-6, py-6, px+6, py+6, fill='#faffb1', outline='')

    @staticmethod
    def delete_tree_node(data, target):
        if not data:
            return []
        value, *children = data
        left = [child for child in children if int(child) < int(value)]
        right = [child for child in children if int(child) > int(value)]
        if int(target) < int(value):
            return [value] + Lab.delete_tree_node(left, target) + right
        if int(target) > int(value):
            return [value] + left + Lab.delete_tree_node(right, target)
        if not left or not right:
            return left or right
        successor = min(right, key=int)
        return [successor] + left + Lab.delete_tree_node(right, successor)

    @staticmethod
    def tree_traversal(data,mode):
        def walk(items):
            if not items:return []
            root=items[0];left=[x for x in items[1:] if int(x)<int(root)];right=[x for x in items[1:] if int(x)>int(root)]
            if mode=='Inorden':return walk(left)+[root]+walk(right)
            if mode=='Preorden':return [root]+walk(left)+walk(right)
            return walk(left)+walk(right)+[root]
        return walk(data)
    def cancel_animation(self, name):
        job = self.animation_jobs.pop(name, None)
        if job is not None:
            self.views[name][0].after_cancel(job)
        self.active_nodes[name].clear()
        self.pulses.pop(name, None)
        self.views[name][0].delete('removal')

    def animate_removal(self, name, node):
        canvas = self.views[name][0]
        x, y, value = node

        def frame(index):
            self.animation_jobs.pop(name, None)
            canvas.delete('removal')
            if index == 10:
                return
            radius = 28 - index * 2
            offset = index * 3
            canvas.create_oval(x - radius, y - radius - offset, x + radius, y + radius - offset,
                               outline='#fb7185', width=2, fill='#512c45', tags='removal')
            if index < 5:
                canvas.create_text(x, y - offset, text=str(value)[:9], fill=TEXT, tags='removal')
            self.animation_jobs[name] = canvas.after(24, lambda: frame(index + 1))
        frame(0)

    def animate(self, name, sequence):
        canvas = self.views[name][0]
        frames = 9
        def step(index, frame=0):
            self.animation_jobs.pop(name, None)
            if index == len(sequence):
                self.pulses.pop(name, None)
                self.active_nodes[name].clear()
                self.draw(name)
                return
            source, target = sequence[index]
            if source is not None:
                self.pulses[name] = (source, target, frame / (frames - 1))
            else:
                self.pulses.pop(name, None)
            self.active_nodes[name] = {target if frame >= frames // 2 or source is None else source}
            self.draw(name)
            if frame + 1 == frames:
                callback = lambda: step(index + 1)
            else:
                callback = lambda: step(index, frame + 1)
            self.animation_jobs[name] = canvas.after(26, callback)
        step(0)

    def node(self,c,x,y,value,active=False):
        c.create_oval(x-27,y-27,x+27,y+27,fill='#0f766e' if active else '#493574',outline='#5eead4' if active else PURPLE,width=3)

        c.create_text(x,y,text=str(value)[:9],fill=TEXT,font=('Segoe UI',11,'bold'))

if __name__ == '__main__':
    root = tk.Tk()
    lab = Lab(root)
    if '--smoke-test' in sys.argv:
        lab.action('Listas', 'Insertar', '1', '')
        lab.action('Listas', 'Insertar', '2', '')
        lab.action('Listas', 'Eliminar', '1', '')
        if list(lab.data['Listas']) != ['2']:
            raise SystemExit('La prueba de la lista enlazada falló.')
        lab.action('Grafos', 'Vértice', 'A', '')
        lab.action('Grafos', 'Vértice', 'B', '')
        lab.action('Grafos', 'Arista', 'A', 'B')
        lab.action('Grafos', 'BFS', 'A', '')
        root.update()
        if 'A → B' not in lab.views['Grafos'][1].cget('text'):
            raise SystemExit('La prueba gráfica BFS falló.')
        root.after(550, root.destroy)
    root.mainloop()
