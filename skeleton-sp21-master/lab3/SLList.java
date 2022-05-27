public class SLList {
    Node sentinel;

    public SLList() {
        this.sentinel = new Node();
    }

    private static class Node {
        int item;
        Node next;
    }

    public int findFirst(int n) {
        return findFirstHelper(n, 0, sentinel.next);
    }

    private int findFirstHelper (int n, int index, Node curr) {
        if (curr == null) {
            return -1;
        }
        if (curr.item == n) {
            return index;
        } else {
            return findFirstHelper(n, index + 1, curr.next);
        }
    }
}
