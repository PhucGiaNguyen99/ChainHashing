import java.util.ArrayList;
import java.util.Objects;

public class Map<K, V> {
    class HashNode<K, V> {
        K key;
        V value;
        final int hashCode;

        // reference to the next node
        HashNode<K, V> next;

        public HashNode(K key, V value, int hashCode) {
            this.key = key;
            this.value = value;
            this.hashCode = hashCode;
        }
    }

    /**
     * An buckets array to store the array of chains.
     */
    private ArrayList<HashNode<K, V>> bucketsArray;

    /**
     * Number of buckets in the chain array.
     */
    private int numBuckets;

    /**
     * Size of the chain array.
     */
    private int size;

    public Map() {
        bucketsArray = new ArrayList<>();
        numBuckets = 10;
        size = 0;

        // create an empty chains array
        for (int i = 0; i < numBuckets; i++) {
            bucketsArray.add(null);
        }
    }

    /**
     * Get number of chains in the chain array.
     *
     * @return current number of element in the chain array.
     */
    public int getSize() {
        return size;
    }

    /**
     * Check if the buckets array is empty.
     *
     * @return true if the buckets array is empty. Otherwise, return false.
     */
    public boolean isEmpty() {
        return getSize() == 0;
    }

    /**
     * Create a hash code for given key.
     *
     * @param key given key needed to find hash code
     * @return hash code of given key
     */
    private final int hashCode(K key) {
        return Objects.hashCode(key);
    }

    /**
     * Hash function to find the index in the buckets array of given key.
     *
     * @param key given key needed to find the index
     * @return index of given key in the buckets array
     */
    private int getBucketIndex(K key) {
        int hashCode = hashCode(key);
        int index = hashCode % numBuckets;

        // key.hashCode() can be negative
        index = index < 0 ? index * -1 : index;
        return index;
    }

    /**
     * Return the value of given key.
     *
     * @param key given key needed to find key value
     * @return the value of given key
     */
    public V get(K key) {
        int bucketIndex = getBucketIndex(key);
        int hashCode = hashCode(key);

        HashNode<K, V> head = bucketsArray.get(bucketIndex);

        // search the key in chain
        while (head != null) {
            if (head.key.equals(key) && head.hashCode == hashCode) {
                return head.value;
            }
            head = head.next;
        }
        // cannot find the value of given key
        return null;
    }

    /**
     * Remove hashNode with given key
     *
     * @param key given key to remove the hashNode
     * @return value of given key
     */
    public V remove(K key) {
        int bucketIndex = getBucketIndex(key);
        int hashCode = hashCode(key);

        // get head of chain
        HashNode<K, V> head = bucketsArray.get(bucketIndex);

        // go check if there is a chain in the element at such index
        HashNode<K, V> prev = null;
        while (head != null) {
            // stop the loop when you found the hash node of given key
            if (head.key.equals(key) && hashCode == head.hashCode) {
                break;
            }
            prev = head;
            head = head.next;
        }

        if (head == null) {
            return null;
        }

        // decrement the size
        size--;

        if (prev != null) {
            prev.next = head.next;
        } else {
            bucketsArray.set(bucketIndex, head.next);
        }
        return head.value;
    }

    public void add(K key, V value) {
        int bucketIndex = getBucketIndex(key);
        int hashCode = hashCode(key);
        HashNode<K, V> head = bucketsArray.get(bucketIndex);

        // check if key already existed. If yes, replace the hashNode with new value
        while (head != null) {
            if (head.key.equals(key) && head.hashCode == hashCode) {
                head.value = value;
                return;
            }
            head = head.next;
        }

        size++;

        // insert key at the front of the chain
        head = bucketsArray.get(bucketIndex);
        HashNode<K, V> newNode = new HashNode<>(key, value, hashCode);
        newNode.next = head;
        bucketsArray.set(bucketIndex, newNode);

        // if the load factor goes beyond the threshold, then double the hash table size
        if ((1.0 * size) / numBuckets >= 0.7) {
            ArrayList<HashNode<K, V>> temp = bucketsArray;
            bucketsArray = new ArrayList<>();
            numBuckets = 2 * numBuckets;
            size = 0;
            for (int i = 0; i < numBuckets; i++) {
                bucketsArray.add(null);
            }
            for (HashNode<K, V> headNode : temp) {
                while (headNode != null) {
                    add(headNode.key, headNode.value);
                    headNode = headNode.next;
                }
            }
        }
    }

}
