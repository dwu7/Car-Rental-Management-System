
package carsystem;


public class LLQueue<E> {

    static class Node<E> {

        private E element; // reference to the element stored at this node
        private Node<E> next; // reference to the subsequent node in the list

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
    protected Node<E> front;
    protected Node<E> rear;
    protected int size;

    public LLQueue() { // constructor
        front = null;
        rear = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public E first() { // returns (but does not remove) the first element
        if (isEmpty()) {
            return null;
        }
        return front.getElement();
    }

    public E last() { // returns (but does not remove) the last element
        if (isEmpty()) {
            return null;
        }
        return rear.getElement();
    }

    public void enqueue(E elem) {
        Node<E> newest = new Node<E>(elem, null);
        if (isEmpty()) {
            front = newest; // special case of a previously empty queue 
        } else {
            rear.setNext(newest); // add node at the tail of the list 
        }
        rear = newest; // update the reference to the tail node 
        size++;
    }

    public E dequeue() {
        if (isEmpty()) {
            return null;
        }
        E answer = front.getElement();
        front = front.getNext();
        size--;
        if (size == 0) {
            rear = null; // the queue is now empty
        }
        return answer;
    }


    // Display method: prints the waiting reservations, the first in the line first
    public void Display() {
        if (isEmpty()) {
            System.out.println("Nobody is waiting.");
            return;
        }
        int s = size();
        for (int i = 0; i < s; i++) {
            E ele = dequeue();
            System.out.println((i + 1) + ") " + ele);
            enqueue(ele);
        }
    }

    // ==================== PROJECT METHODS ====================
    // Method 1: Take the first reservation that waits for a vehicle type, the others keep their order
    public Reservation takeFirstFor(String type) {
        int s = size();
        Reservation taken = null;
        for (int i = 0; i < s; i++) {
            E ele = dequeue();
            Reservation r = (Reservation) ele;
            if (taken == null && r.getWantedType().equalsIgnoreCase(type)) {
                taken = r;
            } else {
                enqueue(ele);
            }
        }
        return taken;
    }

    // Method 2: A waiting renter changes the wanted vehicle type and keeps the same place in the line
    public Reservation changeWantedType(int renterId, String newType) {
        int s = size();
        Reservation changed = null;
        for (int i = 0; i < s; i++) {
            E ele = dequeue();
            Reservation r = (Reservation) ele;
            if (changed == null && r.getRenter().getId() == renterId) {
                r.setWantedType(newType);
                changed = r;
            }
            enqueue(ele);
        }
        return changed;
    }

    // Method 3: A renter leaves the line, returns the removed reservation (null if the renter is not waiting)
    public Reservation leaveLine(int renterId) {
        int s = size();
        Reservation removed = null;
        for (int i = 0; i < s; i++) {
            E ele = dequeue();
            Reservation r = (Reservation) ele;
            if (removed == null && r.getRenter().getId() == renterId) {
                r.setStatus("Cancelled");
                removed = r;
            } else {
                enqueue(ele);
            }
        }
        return removed;
    }
}
