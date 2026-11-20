from dataclasses import dataclass


@dataclass
class Link:
    value: str
    next: 'Link | None' = None


class LinkedList:
    def __init__(self):
        self.head = None
        self.tail = None
        self.size = 0

    def __len__(self):
        return self.size

    def __iter__(self):
        node = self.head
        while node is not None:
            yield node.value
            node = node.next

    def clear(self):
        self.head = self.tail = None
        self.size = 0

    def append(self, value):
        node = Link(value)
        if self.tail is None:
            self.head = node
        else:
            self.tail.next = node
        self.tail = node
        self.size += 1

    def index(self, value):
        for index, item in enumerate(self):
            if item == value:
                return index
        raise ValueError('Valor no encontrado.')

    def pop(self, index):
        if not 0 <= index < self.size:
            raise IndexError('Índice fuera de rango.')
        previous = None
        node = self.head
        for _ in range(index):
            previous, node = node, node.next
        if previous is None:
            self.head = node.next
        else:
            previous.next = node.next
        if node is self.tail:
            self.tail = previous
        self.size -= 1
        return node.value
