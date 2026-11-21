"""Comparación pila vs. cola: la misma secuencia de entrada, distinto orden de salida."""
import tkinter as tk

from validation import MAX_ITEMS, parse_count, parse_number

BG = '#10131d'; PANEL = '#1c2131'; PURPLE = '#a78bfa'; TEXT = '#f5f3ff'


class StackQueueComparison:
    """Modelo: guarda cada dato en una pila (LIFO) y en una cola (FIFO) a la vez."""

    def __init__(self):
        self.clear()

    def clear(self):
        self.stack, self.queue = [], []
        self.entry, self.stack_out, self.queue_out = [], [], []

    def add(self, value):
        if not self.stack and self.entry:      # el experimento anterior terminó: empieza uno nuevo
            self.clear()
        self.stack.append(value)               # PUSH: entra por la cima (final de la lista)
        self.queue.append(value)               # ENQUEUE: entra por el final (REAR)
        self.entry.append(value)

    def remove_one(self):
        if not self.stack:
            raise ValueError('La pila y la cola están vacías: no hay elementos para retirar.')
        popped = self.stack.pop()              # POP: sale la cima (el último en entrar)
        dequeued = self.queue.pop(0)           # DEQUEUE: sale el frente (el primero en entrar)
        self.stack_out.append(popped)
        self.queue_out.append(dequeued)
        return popped, dequeued

    def remove_all(self):
        if not self.stack:
            raise ValueError('La pila y la cola están vacías: no hay elementos para retirar.')
        while self.stack:
            self.remove_one()

    def explanation(self):
        if self.stack or not self.entry:
            return ''
        return (f'Entrada: {", ".join(self.entry)}. Salida de la pila: {", ".join(self.stack_out)} (LIFO). '
                f'Salida de la cola: {", ".join(self.queue_out)} (FIFO). El orden es distinto porque la pila '
                'retira por la cima (el último en entrar) y la cola por el frente (el primero en entrar).')


class CompareView(tk.Frame):
    def __init__(self, parent, back):
        super().__init__(parent, bg=PANEL)
        self.model = StackQueueComparison()
        self.total = 0
        self.done = 0
        top = tk.Frame(self, bg=PANEL); top.pack(fill='x', padx=12, pady=12)
        tk.Label(top, text='Comparar pila y cola', bg=PANEL, fg=PURPLE, font=('Segoe UI', 14, 'bold')).pack(side='left')
        tk.Button(top, text='← Menú principal', bg='#383057', fg='white', relief='flat', command=back).pack(side='right', padx=3)
        guided = tk.Frame(self, bg=PANEL); guided.pack(fill='x', padx=12)
        tk.Label(guided, text=f'Cantidad (1-{MAX_ITEMS}):', bg=PANEL, fg=TEXT).pack(side='left')
        self.count = tk.Entry(guided, width=8); self.count.pack(side='left', padx=6)
        tk.Button(guided, text='Iniciar carga', bg='#493574', fg='white', relief='flat', command=self.start).pack(side='left', padx=3)
        tk.Label(guided, text='Dato:', bg=PANEL, fg=TEXT).pack(side='left', padx=(12, 0))
        self.value = tk.Entry(guided, width=12); self.value.pack(side='left', padx=6)
        self.value.bind('<Return>', lambda e: self.add())
        tk.Button(guided, text='Ingresar dato', bg='#493574', fg='white', relief='flat', command=self.add).pack(side='left', padx=3)
        controls = tk.Frame(self, bg=PANEL); controls.pack(fill='x', padx=12, pady=(8, 0))
        for text, command in (('Retirar uno de cada', self.remove_one), ('Retirar todo', self.remove_all), ('Reiniciar', self.reset)):
            tk.Button(controls, text=text, bg='#383057', fg='white', relief='flat', command=command).pack(side='left', padx=3)
        self.prompt = tk.Label(self, text='Paso 1: indica cuántos datos vas a ingresar y pulsa «Iniciar carga».',
                               bg=PANEL, fg='#c4b5fd', anchor='w', justify='left', wraplength=1000, font=('Segoe UI', 10))
        self.prompt.pack(fill='x', padx=14, pady=(6, 0))
        self.canvas = tk.Canvas(self, bg=BG, highlightthickness=0, height=330)
        self.canvas.pack(fill='both', expand=True, padx=12, pady=8)
        self.canvas.bind('<Configure>', lambda e: self.draw())
        self.status = tk.Label(self, text='Ingresa datos: se guardan en la pila y en la cola a la vez.', bg=PANEL, fg=PURPLE,
                               anchor='w', justify='left', wraplength=1000, font=('Segoe UI', 11))
        self.status.pack(fill='x', padx=14, pady=12)
        self.draw()

    def say(self, text):
        self.status.config(text=text)

    def start(self):
        try:
            self.total = parse_count(self.count.get(), 'La cantidad de datos')
        except ValueError as error:
            self.say(f'⚠ {error}'); return
        self.done = 0
        self.model.clear()
        self.draw()
        self.prompt.config(text=f'Dato 1 de {self.total}: escribe el valor y pulsa «Ingresar dato».')
        self.say(f'Se ingresarán {self.total} datos en la pila y en la cola.')
        self.value.focus_set()

    def add(self):
        if self.total == 0:
            self.say('⚠ Primero indica cuántos datos vas a ingresar y pulsa «Iniciar carga».'); return
        try:
            number = parse_number(self.value.get(), f'El dato {self.done + 1}')
        except ValueError as error:
            self.say(f'⚠ {error}'); return
        self.model.add(str(number))
        self.value.delete(0, 'end')
        self.done += 1
        if self.done >= self.total:
            self.prompt.config(text=f'Carga completa: {self.done} datos. Usa «Retirar uno de cada» o «Retirar todo».')
            self.total = 0
        else:
            self.prompt.config(text=f'Dato {self.done + 1} de {self.total}: escribe el valor y pulsa «Ingresar dato».')
        self.say(f'{number} guardado con PUSH en la pila y ENQUEUE en la cola. Entrada: {", ".join(self.model.entry)}.')
        self.draw()

    def remove_one(self):
        try:
            popped, dequeued = self.model.remove_one()
        except ValueError as error:
            self.say(f'⚠ {error}'); return
        self.say(f'POP sacó {popped} (la cima) y DEQUEUE sacó {dequeued} (el frente). ' + self.model.explanation())
        self.draw()

    def remove_all(self):
        try:
            self.model.remove_all()
        except ValueError as error:
            self.say(f'⚠ {error}'); return
        self.say('Se retiraron todos los elementos. ' + self.model.explanation())
        self.draw()

    def reset(self):
        self.model.clear()
        self.total = self.done = 0
        self.prompt.config(text='Paso 1: indica cuántos datos vas a ingresar y pulsa «Iniciar carga».')
        self.say('Comparación reiniciada.')
        self.draw()

    def draw(self):
        c, m = self.canvas, self.model
        c.delete('all')
        count = max(len(m.stack), len(m.stack_out), 1)
        c.configure(scrollregion=(0, 0, max(c.winfo_width(), 130 + count * 78), 330))
        c.create_text(20, 14, anchor='w', text='PILA · LIFO (sale primero el último)', fill=PURPLE, font=('Segoe UI', 11, 'bold'))
        for i, value in enumerate(m.stack):
            x = 40 + i * 78
            c.create_rectangle(x, 50, x + 64, 95, fill='#352951', outline=PURPLE, width=2)
            c.create_text(x + 32, 72, text=value, fill=TEXT, font=('Segoe UI', 12, 'bold'))
            if i == len(m.stack) - 1:
                c.create_text(x + 32, 38, text='TOP ▼', fill='#34d399', font=('Segoe UI', 10, 'bold'))
        c.create_text(20, 130, anchor='w', text='COLA · FIFO (sale primero el primero)', fill=PURPLE, font=('Segoe UI', 11, 'bold'))
        for i, value in enumerate(m.queue):
            x = 40 + i * 78
            c.create_rectangle(x, 166, x + 64, 211, fill='#352951', outline=PURPLE, width=2)
            c.create_text(x + 32, 188, text=value, fill=TEXT, font=('Segoe UI', 12, 'bold'))
            if i == 0:
                c.create_text(x + 32, 154, text='FRONT ▼', fill='#34d399', font=('Segoe UI', 10, 'bold'))
            if i == len(m.queue) - 1 and len(m.queue) > 1:
                c.create_text(x + 32, 224, text='▲ REAR', fill='#fbbf24', font=('Segoe UI', 10, 'bold'))
        c.create_text(20, 256, anchor='w', text='Entrada: ' + (', '.join(m.entry) or '—'), fill=TEXT, font=('Segoe UI', 11))
        c.create_text(20, 284, anchor='w', text='Salida de la pila: ' + (', '.join(m.stack_out) or '—'), fill=TEXT, font=('Segoe UI', 11))
        c.create_text(20, 310, anchor='w', text='Salida de la cola: ' + (', '.join(m.queue_out) or '—'), fill=TEXT, font=('Segoe UI', 11))
