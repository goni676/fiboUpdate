import java.util.Arrays;

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap {
    private HeapNode head;
    private HeapNode min;
    private int size;
    private int roots_num;
    private static int total_links;
    private static int total_cuts;
    private int marked;

    /**
     * public boolean isEmpty()
     * <p>
     * Returns true if and only if the heap is empty.
     */
    public boolean isEmpty() {
        return head == null;
    }

    /**
     * public HeapNode insert(int key)
     * <p>
     * Creates a node (of type HeapNode) which contains the given key, and inserts
     * it into the heap. The added key is assumed not to already belong to the heap.
     * <p>
     * Returns the newly created node.
     */
    public HeapNode getHead() {

        return head;
    }

    public void setHead(HeapNode head) {
        this.head = head;
    }

    public int getMarked() {
        return marked;
    }

    public int getRoots_num() {
        return roots_num;
    }

    public HeapNode insert(int key) {
        HeapNode node = new HeapNode(key);
        size++;
        return insertNode(node);
    }

    private HeapNode insertNode(HeapNode node) {
        if (!isEmpty()) {
            HeapNode curr_head = getHead();
            node.setNext(curr_head);
            HeapNode curr_prev = curr_head.getPrev();
            curr_prev.setNext(node);
            node.setPrev(curr_prev);
            curr_head.setPrev(node);
            node.setParent(null);

        } else {
            min = node;
            node.setNext(node);
            node.setPrev(node);
        }
        setHead(node);
        roots_num++;
        if (node.getKey() < min.getKey()) {
            min = node;
        }
        return node;
    }

    /**
     * public void deleteMin()
     * <p>
     * Deletes the node containing the minimum key.
     */
    public void deleteMin() {
        if (isEmpty()) {
            return;
        }
        if (size == 1) {
            head = null;
            min = null;
            size = 0;
            roots_num = 0;
            return;

        }
        HeapNode deleted = min;
        HeapNode deletedNext = min.getNext();
        HeapNode child = deleted.getChild();

        if (child != null) {
            HeapNode sibling = child;
            do {
                HeapNode nSibling = sibling.getNext();
                removeNode(sibling);
                if (sibling.isMark()){
                    marked--;
                    sibling.setMark(false);
                }
                insertNode(sibling);
                sibling = nSibling;
            } while (deleted.getChild() != null);
        }
        min = deletedNext;
        removeNode(deleted);
        roots_num--;
        consolidate();
        size--;

    }

    private void removeNode(HeapNode node) {
        if (node == head) {
            head = head.next != head ? head.next : null;
        }
        if (node.parent != null) {
            node.parent.child = node.next != node ? node.next : null;
        }
        if(node.prev!=null)
        {
            node.prev.next = node.next;
        }
        if(node.next!=null) {
            node.next.prev =node.prev;
        }
    }
    private void consolidate() {
        int result = (int) (Math.log(size) / Math.log(2));
        HeapNode[] arr = new HeapNode[result + 1];
        head.prev.next=null;
        head.prev=null;
        HeapNode node1 = head;

        while (node1!=null) {
            int rank = node1.getRank();
            HeapNode node1Next =node1.next;
            while (arr[rank] != null) {
                HeapNode node2 = arr[rank];
                if (node1.getKey() > node2.getKey()) {
                    HeapNode tmp = node1;
                    node1 = node2;
                    node2 = tmp;

                }
                link(node2, node1);
                arr[rank] = null;
                if (rank < result) {
                    rank++;
                } else {
                    break;
                }

            }
            arr[rank] = node1;
            node1 = node1Next;
        }
        head = null;
        roots_num = 0;
        for (HeapNode n : arr) {
            if (n != null) {
                insertNode(n);
            }
        }
    }

    private void link(HeapNode node_greater, HeapNode node_smaller) {
        removeNode(node_greater);
        roots_num--;
        if (node_smaller.child != null) {
            HeapNode curr_child = node_smaller.child;
            node_greater.next = curr_child;
            HeapNode curr_prev = curr_child.prev;
            curr_prev.next = node_greater;
            node_greater.prev = curr_prev;
            curr_child.prev = node_greater;

        } else {
            node_greater.prev = node_greater;
            node_greater.next = node_greater;
        }
        node_smaller.child = node_greater;
        node_greater.setParent(node_smaller);
        node_smaller.setRank(node_smaller.getRank() + 1);
//        node_greater.setMark(false);

        total_links++;
    }


    /**
     * public HeapNode findMin()
     *
     * Returns the node of the heap whose key is minimal, or null if the heap is
     * empty.
     *
     */
    public HeapNode findMin() {
        if (isEmpty()){
            return null;
        }
        return min;
    }

    /**
     * public void meld (FibonacciHeap heap2)
     *
     * Melds heap2 with the current heap.
     *
     */
    public void meld(FibonacciHeap heap2) {

        if (heap2.isEmpty()){
            return;
        }
//      if current heap is empty - set all fields in it to their corresponding fields in heap2
        if (isEmpty()){
            head = heap2.getHead();
            min = heap2.findMin();
            size = heap2.size();
            roots_num = heap2.roots_num;
            marked = heap2.marked;
            return;
        }

//      pointers updating
        HeapNode first_original = getHead();
        HeapNode last_original = getHead().getPrev();
        HeapNode first_new = heap2.getHead();
        HeapNode last_new = heap2.getHead().getPrev();
        first_original.setPrev(last_new);
        last_new.setNext(first_original);
        last_original.setNext(first_new);
        first_new.setPrev(first_original);

//      fields updating
        marked += heap2.getMarked();
        roots_num += heap2.getRoots_num();
        size += heap2.size();
        if (min.getKey() > heap2.findMin().getKey()){
            min = heap2.findMin();
        }
    }

    /**
     * public int size()
     *
     * Returns the number of elements in the heap.
     *
     */
    public int size() {
        return this.size;
    }

    /**
     * public int[] countersRep()
     *
     * Return an array of counters. The i-th entry contains the number of trees of
     * order i in the heap. Note: The size of of the array depends on the maximum
     * order of a tree, and an empty heap returns an empty array.
     *
     */
    public int[] countersRep() {
        // If the heap is empty -> return an empty array
        if (isEmpty()){
            int[] arr = new int[0];
            return arr;
        }
        //
        int result = (int) (Math.log(size) / Math.log(2));
        int [] arr = new int[result + 1];
        int i = 0;

        HeapNode curr = getHead();

        // Loop through the root list
        while (i < roots_num){
            int rank = curr.getRank();
            // The cell which corresponds to the current root's rank should be incremented by 1
            arr[rank] += 1;
            curr = curr.getNext();
            i++;
        }

        int maxRank = result;
        while (arr [maxRank] == 0){
            maxRank--;
        }
        // maxRank holds the maximal tree's rank
        int[] finalArray = new int[maxRank+1];

        // Copy the relevant counters
        for (int j = 0; j<=maxRank; j++){
            finalArray[j] = arr[j];
        }
        return finalArray;
    }

    /**
     * public void delete(HeapNode x)
     *
     * Deletes the node x from the heap. It is assumed that x indeed belongs to the
     * heap.
     *
     */

    public void delete(HeapNode x) {
        //Worst Case - O(n)
        //Amortized  - O(log n)
        decreaseKey(x,Integer.MAX_VALUE);// Set the key of the target node to be the smallest in the heap (min)
        deleteMin(); // Delete the smallest element in the heap (x)
    }

//    private void heapify(HeapNode node) {
//        HeapNode parent = node.parent;
//        while (parent != null) {
//            if (parent.getKey() > node.getKey()) {
//                swapHeapify(node, parent);
//                node = parent;
//                parent = parent.parent;
//                //HeapPrinter.print(this, false);
//            }
//        }
//        min=node;
//    }
//
//    private void swapHeapify(HeapNode child, HeapNode parent) {
//        int childKey=child.key;
//        String childInfo=child.info;
//        child.info=parent.info;
//        child.key=parent.key;
//        parent.info=childInfo;
//        parent.key=childKey;
//    }
// alternative solution?
//	HeapNode childNext = child.next;
//	HeapNode childPrev = child.prev;
//	HeapNode childChild = child.child;
//	HeapNode parentParent = parent.parent;
//
//	boolean childMark = child.isMark();
//	int childRank = child.rank;
//
//	child.next = parent.next;
//	child.prev = parent.prev;
//	parent.prev.next = child;
//	parent.next.prev = child;
//	parent.next = childNext;
//	parent.prev = childPrev;
//	childPrev.next = parent;
//	childNext.prev = parent;
//	child.child=parent;
//	parent.parent=child;
//	parent.child=childChild;
//	childChild.parent=parent;
//	child.mark = parent.mark;
//	child.parent = parentParent;
//	child.rank = parent.rank;
//
//	if (parent == head) {
//		head = child;
//	}
//	HeapPrinter.print(this, false);
//
//
//	parent.mark = childMark;
//
//	parent.rank = childRank;
    //////////////////////////////
    /**
     * public void decreaseKey(HeapNode x, int delta)
     *
     * Decreases the key of the node x by a non-negative value delta. The structure
     * of the heap should be updated to reflect this change (for example, the
     * cascading cuts procedure should be applied if needed).
     */
    public void decreaseKey(HeapNode x, int delta) {
        // Worst case - O(log n)
        // Amortized - O(1)

        // When calling the method from 'delete', ensure that the key has minimal value
        if (delta == Integer.MAX_VALUE){
            x.setKey(Integer.MIN_VALUE);
        }
        // Update the node's key
        else{
            x.setKey(x.getKey() - delta);
        }
        HeapNode parent = x.getParent();


        if (parent != null && x.getKey() < parent.getKey()) {
            // Call cascading cuts to fix the heap's structure
            cascadingCut(x, parent);
        }
        if (x.getKey() < min.getKey()) {
            min = x;
        }

    }
    private void cascadingCut(HeapNode child, HeapNode parent) {
        // The cascadingCut procedure recurses its way up the tree until
        // either a root or an unmarked node is found
        // Worst case  - O(log n)
        cut(child, parent);

        // Mark the parent if necessary
        if(!parent.isMark() && parent.getParent() != null){
            parent.setMark(true);
            marked++;
            return;
        }
        // If parent is null - the tree top has been reached, no more cutting required
        if (parent.getParent() == null){
            return;
        }
        // If parent is marked - continue climbing the tree and perform cascadingCut
        if (parent.isMark()){
            cascadingCut(parent, parent.getParent());
        }
    }

    private void cut(HeapNode child, HeapNode parent){
        // Worst case O(1)
        total_cuts++;
        // Check if child is the direct child of parent
        if (parent.getChild() == child) {
            // If child is parent's only child - disconnect child from parent
            if (child.getNext() == child) {
                parent.setChild(null);
            } else {
                // If parent has another child - update its child pointer
                parent.setChild(child.next);
            }
        }
        child.setParent(null);
        // Disconnect child from its siblings
        if (child.getNext() != child) {
            child.prev.next = child.next;
            child.next.prev = child.prev;
        }

        if (child.isMark()){
            child.setMark(false);
            marked--;
        }
        // Add child to the roots list
        insertNode(child);

        parent.setRank(parent.getRank()-1);
    }
    /**
     * public int potential()
     *
     * This function returns the current potential of the heap, which is: Potential
     * = #trees + 2*#marked
     *
     * In words: The potential equals to the number of trees in the heap plus twice
     * the number of marked nodes in the heap.
     */
    public int potential() {
        return roots_num + 2*marked;
    }

    /**
     * public static int totalLinks()
     *
     * This static function returns the total number of link operations made during
     * the run-time of the program. A link operation is the operation which gets as
     * input two trees of the same rank, and generates a tree of rank bigger by one,
     * by hanging the tree which has larger value in its root under the other tree.
     */
    public static int totalLinks() {
        return total_links;
    }

    /**
     * public static int totalCuts()
     *
     * This static function returns the total number of cut operations made during
     * the run-time of the program. A cut operation is the operation which
     * disconnects a subtree from its parent (during decreaseKey/delete methods).
     */
    public static int totalCuts() {
        return total_cuts;
    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     *
     * This static function returns the k smallest elements in a Fibonacci heap that
     * contains a single tree. The function should run in O(k*deg(H)). (deg(H) is
     * the degree of the only tree in H.)
     *
     * ###CRITICAL### : you are NOT allowed to change H.
     */
    public static int[] kMin(FibonacciHeap H, int k) {
        // Worst case - O(k*deg(H))

        int[] arr = new int[k];
        FibonacciHeap auxiliary = new FibonacciHeap();

        // Insert the sole tree's root to the new heap
        HeapNode last_inserted = auxiliary.insert(H.getHead().getKey());

        // Create a pointer to the "twin" node in the original heap
        last_inserted.setTwin(H.getHead());
        HeapNode sibling;

        for (int i = 0; i<k; i++){
            // Add the minimum element in auxiliary to arr. Next, delete it from auxiliary
            HeapNode currMin = auxiliary.findMin();
            arr[i] = currMin.getKey();
            auxiliary.deleteMin();
            HeapNode child = currMin.getTwin().getChild();

            // Add currMin's original children to auxiliary
            if (child != null) {
                last_inserted = auxiliary.insert(child.getKey());
                last_inserted.setTwin(child);
                sibling = child.getNext();
                while (sibling != child) {
                    last_inserted = auxiliary.insert(sibling.getKey());
                    last_inserted.setTwin(sibling);
                    sibling = sibling.getNext();
                }
            }
        }
        return arr;
    }



    /**
     * public class HeapNode
     *
     * If you wish to implement classes other than FibonacciHeap (for example
     * HeapNode), do it in this file, not in another file.
     *
     */
    public static class HeapNode {
        private String info;
        private int rank;
        private boolean mark;
        private HeapNode child;
        private HeapNode next;
        private HeapNode prev;
        private HeapNode parent;
        public int key;
        public HeapNode twin;

        public HeapNode(int key) {
            this.key = key;
            this.prev = this;
            this.next = this;
        }


        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public HeapNode getChild() {
            return child;
        }

        public void setChild(HeapNode child) {
            this.child = child;
        }

        public HeapNode getNext() {
            return next;
        }

        public void setNext(HeapNode next) {
            this.next = next;
        }

        public HeapNode getTwin() {
            return twin;
        }

        public void setTwin(HeapNode twin) {
            this.twin = twin;
        }

        public HeapNode getPrev() {
            return prev;
        }

        public void setPrev(HeapNode prev) {
            this.prev = prev;
        }

        public boolean isMark() {
            return mark;
        }

        public void setMark(boolean mark) {
            this.mark = mark;
        }

        public HeapNode getParent() {
            return parent;
        }

        public void setParent(HeapNode parent) {
            this.parent = parent;
        }

        public int getKey() {
            return this.key;
        }

        public void setKey(int key) {
            this.key = key;
        }
    }
}
