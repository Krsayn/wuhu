package array;

import java.util.HashMap;
import java.util.Map;

public class question760 {
    public int[] anagramMappings(int[] A, int[] B) {
        int[] result = new int[A.length];
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < B.length; i++) {
            map.put(B[i], i);
        }
        for (int i = 0; i < A.length; i++) {
            result[i] = map.get(A[i]);
        }
        return result;
    }
}
