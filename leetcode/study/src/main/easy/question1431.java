package easy;

import java.util.ArrayList;
import java.util.List;

public class question1431 {
    public List<Boolean> kidsWithCandies(int[] candies, int extraCandies) {
        List<Boolean> bool = new ArrayList<>(candies.length);
        int max = 0;
        for (int num : candies) {
            if (max < num)
                max = num;
        }
        for (int candy : candies) {
            if (candy + extraCandies < max) {
                bool.add(false);
            } else {
                bool.add(true);
            }
        }
        return bool;
    }
}
