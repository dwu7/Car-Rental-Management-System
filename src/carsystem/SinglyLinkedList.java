
package carsystem;

public class SinglyLinkedList<E> {

    static class Node<E> {

        private E element;
        private Node<E> next;

        public Node(E e, Node<E> n) {
            element = e;
            next = n;
        }

        public E getElement() {
            return element;
        }

        public Node<E> getNext() {
            return next;
        }

        public void setNext(Node<E> n) {
            next = n;
        }

        public void setelement(E element) {
            this.element = element;
        }
    }

    Node<E> head;
    Node<E> tail;
    private int size;

    public SinglyLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public E first() {
        if (isEmpty()) {
            return null;
        }
        return head.getElement();
    }

    public E last() {
        if (isEmpty()) {
            return null;
        }
        return tail.getElement();
    }

    public void addFirst(E e) {
        Node<E> newest = new Node<E>(e, null);
        newest.setNext(head);
        head = newest;
        if (size == 0) {
            tail = head;
        }
        size++;
    }

    public void addLast(E e) {
        Node<E> newest = new Node<E>(e, null);
        if (isEmpty()) {
            head = newest;
        } else {
            tail.setNext(newest);
        }
        tail = newest;
        size++;
    }

    public E removeFirst() {
        if (isEmpty()) {
            return null;
        }
        E answer = head.getElement();
        head = head.getNext();
        size--;
        if (size == 0) {
            tail = null;
        }
        return answer;
    }

    public E removeLast() {
        if (size == 0) {
            return null;
        } else {
            E data = tail.getElement();
            if (size == 1) {
                head = tail = null;
            } else {
                Node<E> temp1 = head;
                Node<E> temp2 = null;
                while (temp1.getNext() != null) {
                    temp2 = temp1;
                    temp1 = temp1.getNext();
                }
                temp2.setNext(null);
                tail = temp2;
            }
            size--;
            return data;
        }
    }

    // Display method: prints all the vehicles of the fleet
    public void printList() {
        if (isEmpty()) {
            System.out.println("The fleet is empty.");
            return;
        }
        Node<E> current = head;
        while (current != null) {
            System.out.println(current.getElement());
            current = current.getNext();
        }
    }

    // ==================== PROJECT METHODS ====================
    // Method 1: Search for a vehicle by its id
    public Vehicle searchVehicle(int id) {
        Node<E> current = head;
        while (current != null) {
            Vehicle v = (Vehicle) current.getElement();
            if (v.getId() == id) {
                return v;
            }
            current = current.getNext();
        }
        return null;
    }

    // Method 2: Delete a vehicle from the fleet (keeps the node before it while walking)
    public Vehicle deleteVehicle(int id) {
        Node<E> previous = null;
        Node<E> current = head;
        while (current != null) {
            Vehicle v = (Vehicle) current.getElement();
            if (v.getId() == id) {
                if (previous == null) {
                    removeFirst();
                } else {
                    previous.setNext(current.getNext());
                    if (current == tail) {
                        tail = previous;
                    }
                    size--;
                }
                return v;
            }
            previous = current;
            current = current.getNext();
        }
        return null;
    }

    // Method 3: Print the available vehicles of a type ("All" for any type) with a price per day in a range
    public int listAvailable(String type, double minPrice, double maxPrice) {
        Node<E> current = head;
        int count = 0;
        while (current != null) {
            Vehicle v = (Vehicle) current.getElement();
            boolean typeOk = type.equalsIgnoreCase("All") || v.getType().equalsIgnoreCase(type);
            if (v.isAvailable() && typeOk && v.getPricePerDay() >= minPrice && v.getPricePerDay() <= maxPrice) {
                System.out.println(v);
                count++;
            }
            current = current.getNext();
        }
        return count;
    }

    // Method 4: The cheapest available vehicle of a type (null if all of them are rented)
    public Vehicle cheapestAvailable(String type) {
        Node<E> current = head;
        Vehicle cheapest = null;
        while (current != null) {
            Vehicle v = (Vehicle) current.getElement();
            if (v.isAvailable() && v.getType().equalsIgnoreCase(type)) {
                if (cheapest == null || v.getPricePerDay() < cheapest.getPricePerDay()) {
                    cheapest = v;
                }
            }
            current = current.getNext();
        }
        return cheapest;
    }
}
