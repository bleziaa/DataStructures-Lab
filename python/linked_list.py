"""Lista simplemente enlazada utilizada por el laboratorio visual."""

class Node:
    def __init__(self, value, next_node=None):
        self.value = value
        self.next = next_node


class LinkedList:
    def __init__(self, values=()):
        self.head = None
        self.tail = None
        self._size = 0
        for value in values:
            self.append(value)

    def __len__(self):
        return self._size

    def __bool__(self):
        return self._size != 0

    def __iter__(self):
        current = self.head
        while current is not None:
            yield current.value
            current = current.next

    def __contains__(self, value):
        return any(item == value for item in self)

    def __getitem__(self, index):
        if isinstance(index, slice):
            return list(self)[index]
        if index < 0:
            index += self._size
        if not 0 <= index < self._size:
            raise IndexError('Índice fuera de rango')
        current = self.head
        for _ in range(index):
            current = current.next
        return current.value

    def index(self, value):
        for index, item in enumerate(self):
            if item == value:
                return index
        raise ValueError(f'{value!r} no está en la lista')

    def append(self, value):
        node = Node(value)
        if self.tail is None:
            self.head = node
        else:
            self.tail.next = node
        self.tail = node
        self._size += 1

    def insert(self, index, value):
        if index < 0:
            index = max(0, self._size + index)
        if index >= self._size:
            self.append(value)
            return
        if index <= 0:
            self.head = Node(value, self.head)
            if self.tail is None:
                self.tail = self.head
        else:
            previous = self._node_at(index - 1)
            previous.next = Node(value, previous.next)
        self._size += 1

    def _node_at(self, index):
        current = self.head
        for _ in range(index):
            current = current.next
        return current

    def pop(self, index=-1):
        if not self._size:
            raise IndexError('La lista está vacía')
        if index < 0:
            index += self._size
        if not 0 <= index < self._size:
            raise IndexError('Índice fuera de rango')
        if index == 0:
            removed = self.head
            self.head = removed.next
            if self._size == 1:
                self.tail = None
        else:
            previous = self._node_at(index - 1)
            removed = previous.next
            previous.next = removed.next
            if index == self._size - 1:
                self.tail = previous
        self._size -= 1
        return removed.value

    def clear(self):
        self.head = self.tail = None
        self._size = 0
