import java.util.Arrays;

public class theortical {
//    public void func1(FibonacciHeap heap, int i) {
//        FibonacciHeap.HeapNode[] arr = new FibonacciHeap.HeapNode[i];
//        for (int j = i - 1; j < -1; j--) {
//            FibonacciHeap.HeapNode node1 = heap.insert(j);
//            arr[j] = node1;
//        }
//        int log = (int) (Math.log(i) / Math.log(2));
//        for (int k = log; k > 0; k--) {
//            FibonacciHeap.HeapNode node2 = arr[k];
//            heap.decreaseKey(node2, i + 1);
//        }
//
//    }




    public static void main(String[] args) {
        FibonacciHeap heap = new FibonacciHeap();
        int i = (int) (Math.pow(2, 5));
        FibonacciHeap.HeapNode[] arr = new FibonacciHeap.HeapNode[i+1];
        int j;
        double start = System.nanoTime();
        for(j = i-1 ; j > -2; j--) {
            arr[j+1] = heap.insert(j);
        }
        heap.deleteMin();
        int log = (int) (Math.log(i) / Math.log(2));
        for (int k = log; k > 0; k--) {
//            System.out.println(arr[i - (int)(Math.pow(2, k))].getKey());
            heap.decreaseKey(arr[i - (int)(Math.pow(2, k))], i + 1);

        }
        double end = System.nanoTime();;
        System.out.println(FibonacciHeap.totalLinks());
        System.out.println("cuts: ");
        System.out.println(FibonacciHeap.totalCuts());
        System.out.println(heap.potential());
        System.out.println(heap.getMarked());
        System.out.println((end-start)/Math.pow(10, 6));
    }
}


