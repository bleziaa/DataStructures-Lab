import math
import sys
import tkinter as tk
from tkinter import ttk
from collections import deque

from linked_list import LinkedList
from validation import MAX_ITEMS, parse_count, parse_number
from compare import CompareView

BG='#17283d'; PANEL='#263b56'; PURPLE='#e0c3ff'; TEXT='#ffffff'
ACCENT='#67e8f9'; MUTED='#d1ddec'; BUTTON='#3c587c'; HOVER='#5277a3'

class Lab:
    GUIDE_HINTS = {
        'Arreglos': 'Carga guiada: pulsa «Iniciar carga». Se pedirá cuántas posiciones tendrá el arreglo y luego el valor de cada una.',
        'Matrices': 'Carga guiada: pulsa «Iniciar carga». Se pedirán filas y columnas, se mostrará el total de elementos y luego cada valor con su fila y columna.',
        'Listas': 'Carga guiada: pulsa «Iniciar carga». Se pedirá cuántos datos vas a ingresar y luego cada uno.',
        'Pilas': 'Carga guiada: pulsa «Iniciar carga». Se pedirá cuántos datos vas a apilar y luego cada uno.',
        'Colas': 'Carga guiada: pulsa «Iniciar carga». Se pedirá cuántos datos vas a encolar y luego cada uno.',
        'Árboles': 'Carga guiada: pulsa «Iniciar carga». Se pedirá cuántos valores vas a insertar y luego cada uno (sin duplicados).',
        'Grafos': 'Carga guiada: pulsa «Iniciar carga». Se pedirá la cantidad de vértices, el ID de cada uno, la cantidad de aristas y cada conexión.',
    }

    def __init__(self, root):
        self.root = root
        root.title('DataStructures Lab | Visual Studio'); root.geometry('1220x790'); root.minsize(900, 600); root.configure(bg=BG)
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
        style.configure('TNotebook.Tab', background=BUTTON, foreground=TEXT, padding=(17, 11), font=('DejaVu Sans', 10, 'bold'))
        style.map('TNotebook.Tab', background=[('selected', '#5277a3')], foreground=[('selected', '#ffffff')])
        tk.Label(root,text='◈  DATASTRUCTURES LAB',bg=BG,fg=ACCENT,font=('DejaVu Sans',21,'bold')).pack(pady=(17,2))
        tk.Label(root,text='LABORATORIO INTERACTIVO / PYTHON EDITION',bg=BG,fg=MUTED,font=('DejaVu Sans',9)).pack(pady=(0,12))
        self.lab_frame=tk.Frame(root,bg=BG)
        self.tabs=ttk.Notebook(self.lab_frame);self.tabs.pack(fill='both',expand=True,padx=15,pady=10)
        self.views={}
        self.editors={}
        self.selected={}
        self.guided={k:{'stage':None,'total':0,'done':0,'cols':0,'maxe':0} for k in self.data}
        self.prompts={}; self.guide_entries={}
        for name in self.data:
            frame=tk.Frame(self.tabs,bg=PANEL);self.tabs.add(frame,text=name)
            self.make_tab(frame,name)
        self.tabs.bind('<<NotebookTabChanged>>',lambda e:self.draw(self.tabs.tab(self.tabs.select(),'text')))
        self.draw('Arreglos')
        self.menu=tk.Frame(root,bg=BG)
        self.build_menu()
        self.compare=CompareView(root,self.show_menu)
        self.show_menu()

    def build_menu(self):
        tk.Label(self.menu,text='Explora las estructuras de datos',bg=BG,fg=TEXT,
                 font=('DejaVu Sans',17,'bold')).pack(pady=(14,5))
        tk.Label(self.menu,text='Selecciona un módulo para visualizar y experimentar.',
                 bg=BG,fg=MUTED,font=('DejaVu Sans',10)).pack(pady=(0,22))
        grid=tk.Frame(self.menu,bg=BG)
        grid.pack(fill='both',expand=True,padx=45)
        options=[
            ('▦','Arreglos','Posiciones e índices'),
            ('▤','Matrices','Filas y columnas'),
            ('⛓','Listas','Nodos enlazados'),
            ('▥','Pilas','Último en entrar, primero en salir'),
            ('⇄','Colas','Primero en entrar, primero en salir'),
            ('♧','Árboles','Jerarquías y recorridos'),
            ('✧','Grafos','Vértices, rutas y conexiones'),
            ('⇆','Comparar','Pilas frente a colas')]
        for i,(symbol,name,description) in enumerate(options):
            r,c=divmod(i,4)
            tile=tk.Frame(grid,bg=PANEL,highlightbackground='#7296bd',highlightthickness=1)
            tile.grid(row=r,column=c,padx=8,pady=9,sticky='nsew')
            tk.Label(tile,text=symbol,bg=PANEL,fg=ACCENT,font=('DejaVu Sans',26)).pack(pady=(15,5))
            tk.Label(tile,text=name,bg=PANEL,fg=TEXT,font=('DejaVu Sans',12,'bold')).pack()
            tk.Label(tile,text=description,bg=PANEL,fg=MUTED,font=('DejaVu Sans',9),
                     wraplength=190).pack(pady=(6,12))
            callback=self.open_compare if name=='Comparar' else (lambda n=name:self.open_structure(n))
            btn=tk.Button(tile,text='EXPLORAR  →',bg=BUTTON,fg=TEXT,activebackground=HOVER,
                          activeforeground='white',relief='flat',cursor='hand2',
                          font=('DejaVu Sans',9,'bold'),command=callback)
            btn.pack(fill='x',padx=12,pady=(0,14),ipady=7)
        for c in range(4): grid.grid_columnconfigure(c,weight=1,uniform='tile')
        for r in range(2): grid.grid_rowconfigure(r,weight=1)
        tk.Button(self.menu,text='Salir del laboratorio',bg=BG,fg=MUTED,relief='flat',
                  activebackground=BG,activeforeground=TEXT,cursor='hand2',
                  command=self.root.destroy).pack(pady=(6,16))

    def show_menu(self):
        for frame in (self.lab_frame,self.compare):
            frame.pack_forget()
        self.menu.pack(fill='both',expand=True)

    def open_structure(self,name):
        self.menu.pack_forget()
        self.lab_frame.pack(fill='both',expand=True)
        self.tabs.select(list(self.data).index(name))
        self.draw(name)

    def open_compare(self):
        self.menu.pack_forget()
        self.compare.pack(fill='both',expand=True)

    def make_tab(self,f,name):
        header=tk.Frame(f,bg=PANEL)
        header.pack(fill='x',padx=18,pady=(15,8))
        tk.Label(header,text=name.upper(),bg=PANEL,fg=ACCENT,font=('DejaVu Sans',17,'bold')).pack(side='left')
        tk.Button(header,text='← Menú principal',bg=BUTTON,fg=TEXT,relief='flat',
                  command=self.show_menu).pack(side='right')
        editor=tk.LabelFrame(f,text='  1 · EDITOR DE DATOS  ',bg=PANEL,fg=PURPLE,
                             font=('DejaVu Sans',10,'bold'),bd=1,padx=12,pady=10)
        editor.pack(fill='x',padx=18,pady=(0,10))
        labels={'Matrices':('Valor (o filas al crear)','Coordenada fila,columna (o columnas al crear)'),
                'Arreglos':('Valor','Índice (0, 1, 2...)'),
                'Grafos':('Vértice de origen','Vértice de destino'),
                'Árboles':('Valor del nodo','No necesario')}
        label1,label2=labels.get(name,('Valor','Segundo valor (opcional)'))
        tk.Label(editor,text=label1+':',bg=PANEL,fg=TEXT).grid(row=0,column=0,sticky='w',padx=(0,8))
        entry=tk.Entry(editor,width=20,font=('DejaVu Sans',11),bg='#f5f9ff',fg='#13243b',insertbackground='#13243b',relief='solid',bd=2)
        entry.grid(row=0,column=1,sticky='ew',padx=(0,22))
        tk.Label(editor,text=label2+':',bg=PANEL,fg=TEXT).grid(row=0,column=2,sticky='w',padx=(0,8))
        extra=tk.Entry(editor,width=21,font=('DejaVu Sans',11),bg='#f5f9ff',fg='#13243b',insertbackground='#13243b',relief='solid',bd=2)
        extra.grid(row=0,column=3,sticky='ew')
        editor.columnconfigure(1,weight=1)
        editor.columnconfigure(3,weight=1)
        self.editors[name]=(entry,extra)
        tk.Label(editor,text='Selecciona un elemento en el dibujo para editar su posición.',
                 bg=PANEL,fg=MUTED).grid(row=1,column=0,columnspan=4,sticky='w',pady=(9,0))
        ops=['Insertar','Eliminar','Buscar','Recorrer','Reiniciar']
        # Arreglos : botones de operaciones
        if name=='Arreglos':ops=['Insertar','Consultar','Modificar','Eliminar','Buscar','Recorrer','Reiniciar']
        # Pilas : botones de operaciones
        if name=='Pilas':ops=['PUSH','POP','PEEK','Reiniciar']
        # Colas : botones de operaciones
        if name=='Colas':ops=['ENQUEUE','DEQUEUE','PEEK','Reiniciar']
        # Grafos : botones de operaciones
        if name=='Grafos':
            ops=['Vértice','Arista','Quitar vértice','Quitar arista','BFS','DFS','Vecinos','Adyacencia','Reiniciar']
            mode = tk.BooleanVar(value=self.directed)
            toggle = tk.Checkbutton(f, text='Grafo dirigido', variable=mode, bg=PANEL, fg=TEXT,
                                    selectcolor=BG, activebackground=PANEL, activeforeground=TEXT,
                                    command=lambda: self.set_directed(mode))
            toggle.pack(anchor='w', padx=12)
        tk.Label(f,text='2 · OPERACIONES',bg=PANEL,fg=PURPLE,anchor='w',
                 font=('DejaVu Sans',10,'bold')).pack(fill='x',padx=18,pady=(3,4))
        operations_canvas = tk.Canvas(f, bg=PANEL, height=43, highlightthickness=0)
        operations_canvas.pack(fill='x', padx=12)
        controls = tk.Frame(operations_canvas, bg=PANEL)
        operations_canvas.create_window((0, 0), window=controls, anchor='nw')
        controls.bind('<Configure>', lambda e, c=operations_canvas: c.configure(scrollregion=c.bbox('all')))
        operations_scroll = ttk.Scrollbar(f, orient='horizontal', command=operations_canvas.xview)
        operations_scroll.pack(fill='x', padx=12)
        operations_canvas.configure(xscrollcommand=operations_scroll.set)
        # Árboles : botones de operaciones
        if name=='Árboles':ops=['Insertar','Eliminar','Buscar','Inorden','Preorden','Postorden','Reiniciar']
        # Matrices : botones de operaciones
        if name=='Matrices':ops=['Crear (filas, columnas)','Asignar','Consultar','Recorrer','Reiniciar']
        for op in ops:
            tk.Button(controls,text=op,bg=BUTTON,fg='white',relief='flat',command=lambda o=op,n=name:self.action(n,o,entry.get(),extra.get())).pack(side='left',padx=3)
        tk.Label(f,text='3 · CARGA GUIADA',bg=PANEL,fg=PURPLE,anchor='w',
                 font=('DejaVu Sans',10,'bold')).pack(fill='x',padx=18,pady=(12,2))
        self.build_guided(f,name)
        tk.Label(f,text='4 · VISUALIZACIÓN INTERACTIVA · haz clic en un elemento',
                 bg=PANEL,fg=PURPLE,anchor='w',font=('DejaVu Sans',10,'bold')).pack(fill='x',padx=18,pady=(12,2))
        viewport = tk.Frame(f, bg=PANEL)
        viewport.pack(fill='both', expand=True, padx=12, pady=8)
        canvas = tk.Canvas(viewport, bg='#1c304b', highlightthickness=0)
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
        canvas.bind('<Button-1>',lambda e,n=name:self.select_at(n,e))
        self.views[name]=(canvas,status); self.active_nodes[name]=set()
    def select_at(self,name,event):
        canvas,status=self.views[name]
        x,y=canvas.canvasx(event.x),canvas.canvasy(event.y)
        if name=='Matrices':
            data=self.data[name]
            if not data:return
            col=int((x-60)//75)
            row=int((y-55)//60)
            if not (0<=row<len(data) and 0<=col<len(data[0])):return
            if not (60+col*75<=x<=124+col*75 and 55+row*60<=y<=100+row*60):return
            key=(row,col)
            value=data[row][col]
            self.selected[name]=key
            first,second=self.editors[name]
            first.delete(0,'end')
            if value is not None:first.insert(0,str(value))
            second.delete(0,'end');second.insert(0,f'{row},{col}')
            status.config(text=f'Celda ({row},{col}) seleccionada. Escribe un valor y pulsa Asignar.')
        else:
            found=None
            for key,(cx,cy,value) in self.centers.get(name,{}).items():
                if (x-cx)**2+(y-cy)**2<=30**2:
                    found=(key,value);break
            if found is None:return
            key,value=found
            self.selected[name]=key
            first,second=self.editors[name]
            first.delete(0,'end');first.insert(0,str(value))
            second.delete(0,'end')
            if name=='Arreglos':second.insert(0,str(key))
            status.config(text=f'Elemento seleccionado: {value} (posición {key}).')
        self.draw(name)

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
        if operation == 'Adyacencia':
            if not data:
                raise ValueError('El grafo no tiene vértices.')
            lines = []
            for vertex in data:
                linked = [v if u == vertex else u for u, v in self.edges
                          if u == vertex or (not self.directed and v == vertex)]
                lines.append(f'{vertex} → {", ".join(linked) or "—"}')
            kind = 'dirigido' if self.directed else 'no dirigido'
            return f'Lista de adyacencia ({kind}): ' + ' | '.join(lines), []
        if first not in data:
            raise ValueError('El vértice indicado no existe.')
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
        return f'{operation} desde {first}: {order} | Inalcanzables: {", ".join(unreachable) or "ninguno"}', steps

    def matrix_action(self, data, operation, first, second):
        if operation.startswith('Crear'):
            rows = self.integer(first, 'Filas')
            cols = self.integer(second, 'Columnas')
            if not 1 <= rows <= 8 or not 1 <= cols <= 12:
                raise ValueError('Usa 1–8 filas y 1–12 columnas.')
            data[:] = [[None] * cols for _ in range(rows)]
            self.selected.pop('Matrices',None)
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
            data[row][col] = str(parse_number(first, 'El valor'))
        value = data[row][col]
        return f'Celda ({row},{col}): {value if value is not None else "vacía"}.', [(None, (row, col))]

    def linear_action(self, name, data, operation, first, second):
        tree = name == 'Árboles'
        if operation in ('Inorden', 'Preorden', 'Postorden'):
            order = self.tree_traversal(data, operation)
            return f'{operation}: ' + (' → '.join(order) or 'vacío'), [(None, item) for item in order]
        if operation in ('Insertar', 'PUSH', 'ENQUEUE'):
            value = str(parse_number(first, 'El valor del árbol' if tree else 'El valor'))
            if tree and value in data:
                raise ValueError('El valor ya existe en el árbol. No se admiten duplicados.')
            index = self.integer(second, 'El índice') if name == 'Arreglos' and second else len(data)
            if not 0 <= index <= len(data):
                raise ValueError(f'Índice fuera de rango. Usa de 0 a {len(data)}.')
            if tree and len(data) >= 80:
                raise ValueError('El árbol admite hasta 80 nodos.')
            # Listas : inserción en lista enlazada
            if name == 'Listas':
                data.append(value)
            else:
                data.insert(index, value)
            return f'Insertado: {value}.', [(None, value if tree else index)]
        if not data:
            raise ValueError('La estructura está vacía.')
        # Arreglos : consulta y modificación por índice
        if name == 'Arreglos' and operation in ('Consultar', 'Modificar'):
            if not second:
                raise ValueError('Escribe el índice en el segundo campo.')
            index = self.integer(second, 'El índice')
            if not 0 <= index < len(data):
                raise ValueError(f'Índice fuera de rango. Usa de 0 a {len(data) - 1}.')
            if operation == 'Consultar':
                return f'Posición {index}: {data[index]}.', [(None, index)]
            old, data[index] = data[index], str(parse_number(first, 'El valor'))
            return f'Posición {index} modificada: {old} → {data[index]}.', [(None, index)]
        # Pilas y colas : extracción y consulta
        if operation in ('POP', 'DEQUEUE', 'PEEK'):
            index = len(data) - 1 if name == 'Pilas' else 0
            if operation == 'PEEK':
                return f'Próximo: {data[index]}.', [(None, index)]
            return f'Extraído: {data.pop(index)}.', []
        if operation == 'Recorrer':
            return ' → '.join(data), [(index - 1 if index else None, index) for index in range(len(data))]
        value = str(parse_number(first, 'El valor del árbol' if tree else 'El valor'))
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
        ok = True
        try:
            if operation == 'Reiniciar':
                self.reset_guided(name)
                self.selected.pop(name,None)
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
            ok = False
        self.views[name][1].config(text=message)
        self.draw(name)
        if steps:
            self.animate(name, steps)
        elif removal:
            self.animate_removal(name, removal)
        return ok

    def build_guided(self,f,name):
        box=tk.Frame(f,bg=PANEL);box.pack(fill='x',padx=12,pady=(8,0))
        tk.Label(box,text='Carga guiada:',bg=PANEL,fg=TEXT,font=('Segoe UI',10,'bold')).pack(side='left')
        tk.Button(box,text='Iniciar carga',bg='#5a6ca0',fg='white',relief='flat',command=lambda:self.guided_start(name)).pack(side='left',padx=6)
        tk.Label(box,text='Dato / cantidad:',bg=PANEL,fg=TEXT).pack(side='left')
        first=tk.Entry(box,width=13);first.pack(side='left',padx=6)
        second=tk.Entry(box,width=11)
        if name in ('Matrices','Grafos'):
            tk.Label(box,text='Columnas:' if name=='Matrices' else 'Hasta (arista):',bg=PANEL,fg=TEXT).pack(side='left')
            second.pack(side='left',padx=6)
        tk.Button(box,text='Siguiente',bg='#5a6ca0',fg='white',relief='flat',command=lambda:self.guided_next(name,first,second)).pack(side='left',padx=3)
        for entry in (first,second):
            entry.bind('<Return>',lambda e:self.guided_next(name,first,second))
        prompt=tk.Label(f,text=self.GUIDE_HINTS[name],bg=PANEL,fg='#f2ddff',anchor='w',justify='left',wraplength=1000,font=('Segoe UI',10))
        prompt.pack(fill='x',padx=14,pady=(4,0))
        self.prompts[name]=prompt; self.guide_entries[name]=(first,second)

    def set_prompt(self,name,text):
        self.prompts[name].config(text=text)

    def clear_guided_entries(self,name):
        for entry in self.guide_entries[name]:
            entry.delete(0,'end')
        self.guide_entries[name][0].focus_set()

    def reset_guided(self,name):
        self.guided[name].update(stage=None,total=0,done=0,cols=0,maxe=0)
        self.set_prompt(name,self.GUIDE_HINTS[name])

    def guided_prompt(self,name):
        g=self.guided[name];stage=g['stage'];n=g['done']+1;total=g['total']
        if stage=='values':
            where=f' (posición {len(self.data[name])})' if name=='Arreglos' else ''
            text=f'Dato {n} de {total}{where}: escribe el valor y pulsa «Siguiente».'
        elif stage=='cells':
            row,col=divmod(g['done'],g['cols'])
            text=f'Elemento {n} de {total}: próxima celda vacía ({row},{col}). Escribe el valor y, opcionalmente, la coordenada fila,columna en «Columnas»; pulsa «Siguiente».'
        elif stage=='vertices':
            text=f'Vértice {n} de {total}: escribe su identificador (único) y pulsa «Siguiente».'
        elif stage=='edges':
            text=f'Arista {n} de {total}: escribe el vértice de origen en «Dato / cantidad» y el de destino en «Hasta» y pulsa «Siguiente».'
        else:
            text=self.GUIDE_HINTS[name]
        self.set_prompt(name,text)

    def guided_finish(self,name,summary):
        self.guided[name].update(stage=None,total=0,done=0)
        extra=' Escribe un vértice inicial en «Valor / vértice» y pulsa BFS o DFS.' if name=='Grafos' else ' Puedes iniciar otra carga o usar las operaciones.'
        self.set_prompt(name,f'Carga completa: {summary}'+extra)

    def guided_start(self,name):
        g=self.guided[name];status=self.views[name][1]
        if name=='Grafos' and self.data['Grafos']:
            status.config(text='⚠ El grafo ya tiene vértices. Pulsa «Reiniciar» antes de usar la carga guiada.');return
        g.update(total=0,done=0,cols=0,maxe=0,stage='dims' if name=='Matrices' else 'nv' if name=='Grafos' else 'count')
        text={'Matrices':'Paso 1: escribe las filas en «Dato / cantidad» (1-8) y las columnas en «Columnas» (1-12) y pulsa «Siguiente».',
              'Grafos':f'Paso 1: escribe cuántos vértices tendrá el grafo (1-{MAX_ITEMS}) y pulsa «Siguiente».'}
        self.set_prompt(name,text.get(name,f'Paso 1: escribe cuántos datos vas a ingresar (1-{MAX_ITEMS}) y pulsa «Siguiente».'))
        status.config(text='Carga guiada iniciada.')
        self.clear_guided_entries(name)

    def guided_next(self,name,first,second):
        g=self.guided[name];status=self.views[name][1];a=first.get().strip();b=second.get().strip();stage=g['stage'];advanced=False
        try:
            if stage is None:
                raise ValueError('Pulsa «Iniciar carga» primero.')
            if stage=='count':
                g.update(total=parse_count(a,'La cantidad de datos'),done=0,stage='values');advanced=True
            elif stage=='values':
                operation={'Arreglos':'Insertar','Listas':'Insertar','Pilas':'PUSH','Colas':'ENQUEUE','Árboles':'Insertar'}[name]
                position=str(len(self.data[name])) if name=='Arreglos' else ''
                if self.action(name,operation,a,position):
                    g['done']+=1;advanced=True
                    if g['done']>=g['total']:
                        self.guided_finish(name,f'{g["total"]} datos ingresados.');self.clear_guided_entries(name);return
            elif stage=='dims':
                rows=parse_count(a,'Las filas',8);cols=parse_count(b,'Las columnas',12)
                self.action('Matrices','Crear (filas, columnas)',str(rows),str(cols))
                g.update(stage='cells',total=rows*cols,done=0,cols=cols);advanced=True
                status.config(text=f'Matriz {rows}×{cols} creada. Total de elementos: {rows*cols}.')
            elif stage=='cells':
                matrix=self.data['Matrices']
                empty=[(r,c) for r,line in enumerate(matrix) for c,value in enumerate(line) if value is None]
                if not empty:
                    self.guided_finish(name,'todas las celdas llenas.');self.clear_guided_entries(name);return
                if b:
                    parts=b.split(',')
                    if len(parts)!=2:
                        raise ValueError('Escribe la coordenada como fila,columna; por ejemplo 3,0.')
                    row,col=(self.integer(part.strip(),label) for part,label in zip(parts,('Fila','Columna')))
                    if not (0<=row<len(matrix) and 0<=col<len(matrix[0])):
                        raise ValueError('Coordenada fuera de rango; los índices empiezan en cero.')
                else:
                    row,col=empty[0]
                if self.action('Matrices','Asignar',a,f'{row},{col}'):
                    g['done']=sum(value is not None for line in matrix for value in line)
                    advanced=True
                    if g['done']>=g['total']:
                        self.guided_finish(name,f'{g["total"]} elementos ingresados.');self.clear_guided_entries(name);return
            elif stage=='nv':
                g.update(total=parse_count(a,'La cantidad de vértices'),done=0,stage='vertices');advanced=True
            elif stage=='vertices':
                if self.action('Grafos','Vértice',a,''):
                    g['done']+=1;advanced=True
                    if g['done']>=g['total']:
                        n=len(self.data['Grafos']);maxe=n*(n-1) if self.directed else n*(n-1)//2
                        if maxe==0:
                            self.guided_finish(name,f'{n} vértice y ninguna arista posible.');self.clear_guided_entries(name);return
                        g.update(stage='ne',maxe=maxe,done=0,total=0)
                        self.set_prompt(name,f'Escribe cuántas aristas tendrá el grafo (0-{maxe}) y pulsa «Siguiente».')
                        self.clear_guided_entries(name);return
            elif stage=='ne':
                if a == '0':
                    self.guided_finish(name, f'{len(self.data["Grafos"])} vértices y 0 aristas.')
                    self.clear_guided_entries(name)
                    return
                g.update(total=parse_count(a,'La cantidad de aristas',g['maxe']),done=0,stage='edges');advanced=True
            elif stage=='edges':
                if self.action('Grafos','Arista',a,b):
                    g['done']+=1;advanced=True
                    if g['done']>=g['total']:
                        self.guided_finish(name,f'{len(self.data["Grafos"])} vértices y {g["total"]} aristas.');self.clear_guided_entries(name);return
        except ValueError as error:
            status.config(text=f'⚠ {error}');return
        if advanced:
            self.guided_prompt(name);self.clear_guided_entries(name)

    def draw(self,n):
        if n not in self.views:return
        c,_=self.views[n];c.delete('all');d=self.data[n];w=max(c.winfo_width(),600);h=max(c.winfo_height(),350)
        self.centers[n] = {}
        c.configure(scrollregion=(0, 0, w, h))
        if not d:c.create_text(w/2,h/2,text='Sin datos • Inserta elementos para visualizar los nodos',fill='#e1d6f3',font=('Segoe UI',15));return
        # Matrices : cuadrícula, coordenadas y valores
        if n=='Matrices':
            c.configure(scrollregion=(0, 0, max(w, 130 + len(d[0]) * 75), max(h, 70 + len(d) * 60)))
            for r,row in enumerate(d):
                for j,v in enumerate(row):
                    x=60+j*75;y=55+r*60
                    c.create_rectangle(x,y,x+64,y+45,fill='#137f83' if (r,j) in self.active_nodes[n] else '#455e82',outline=ACCENT if self.selected.get(n)==(r,j) else PURPLE,width=4 if self.selected.get(n)==(r,j) else 3)
                    c.create_text(x+32,y+17,text=str(v)[:9] if v is not None else 'Vacía',fill=TEXT if v is not None else '#e1d6f3',font=('Segoe UI',11,'bold'))
                    c.create_text(x+32,y+35,text=f'({r},{j})',fill='#f2ddff',font=('Segoe UI',8))
            return
        # Grafos : vértices y aristas
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
                self.node(c,x,y,v,v in self.active_nodes[n] or self.selected.get(n)==v)
            return
        # Árboles : nodos y niveles
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
                self.node(c, x, y, value, value in self.active_nodes[n] or self.selected.get(n)==value)
            paint(d, 0)
            c.configure(scrollregion=(0, 0, width, max(h, max(y for _, y in positions.values()) + 45)))
            return
        columns = max(1, (w - 40) // 95)
        positions = [(60 + (i % columns) * 95, 60 + (i // columns) * 100) for i in range(len(d))]
        # Pilas : disposición vertical y top
        if n == 'Pilas':
            h = max(h, 100 + len(d) * 65)
            positions = [(w / 2, h - 55 - i * 65) for i in range(len(d))]
        c.configure(scrollregion=(0, 0, w, max(h, max(y for _, y in positions) + 75)))
        for i, ((x, y), value) in enumerate(zip(positions, d)):
            if i and n in ('Listas', 'Colas', 'Arreglos'):
                self.edge(c, n, i - 1, i, positions[i - 1], positions[i], arrow=True)
            self.centers[n][i] = (x, y, value)
            self.node(c, x, y, value, i in self.active_nodes[n] or self.selected.get(n)==i)
            # Arreglos : índices visibles
            if n == 'Arreglos':
                c.create_text(x, y + 39, text=str(i), fill='#e1d6f3')
        if n == 'Pilas':
            c.create_text(w / 2 + 87, positions[-1][1], text='← TOP', fill=PURPLE)
        # Colas : extremos front y rear
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
        canvas.create_line(x, y, *mid, a, b, smooth=True, fill='#5eead4' if active else '#a8c8eb',
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
                               outline='#fb7185', width=2, fill='#834d65', tags='removal')
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
        c.create_oval(x-27,y-27,x+27,y+27,fill='#137f83' if active else '#5a6ca0',outline='#5eead4' if active else PURPLE,width=3)

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
