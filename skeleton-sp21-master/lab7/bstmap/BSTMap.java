package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable, V> implements Map61B{
    private BSTNode root;
    private int size;
    private class BSTNode {
        private K key;
        private V value;
        private BSTNode left, right;

        private BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
        }

        private BSTNode put(BSTNode x, K key, V value) {
            if (key == null || value == null || x == null) {
                throw new IllegalArgumentException();
            }
            if (x.key == null) {
                x.key = key;
                x.value = value;
                return x;
            }
            int cmp = key.compareTo(x.key);
            if (cmp < 0) {
                x.left = put(x.left, key, value);
            } else if (cmp > 0) {
                x.right = put(x.right, key, value);
            } else {
                x.value = value;
            }
            return x;
        }

        private boolean containsKey(BSTNode node, Object key) {
            if (node == null) {
                return false;
            }
            if (node.key == null) {
                return false;
            }
            if (node.key == key) {
                return true;
            }
            int cmp = node.key.compareTo(key);
            if (cmp > 0) {
                return containsKey(node.left, key);
            } else {
                return containsKey(node.right, key);
            }
        }

        private Object get(Object key) {
            if (!containsKey(root, key)) {
                return null;
            }
            if (root.key == key) {
                return root.value;
            }
            int cmp = root.key.compareTo(key);
            if (cmp > 0) {
                return root.left.get(key);
            } else {
                return root.right.get(key);
            }
        }
    }

    public BSTMap() {
        root = new BSTNode(null, null);
        size = 0;
    }
    @Override
    public void clear() {
        root.left = null;
        root.right = null;
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return root.containsKey(root, key);
    }

    @Override
    public Object get(Object key) {
        return root.get(key);

    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(Object key, Object value) {
        if (!containsKey(key)) {
            size += 1;
        }
        root.put(root, (K)key, (V)value);
    }

    @Override
    public Set keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException();
    }
}
