package codingInterview;

import java.util.HashSet;

public class coding_01_01 {
    public boolean isUnique(String astr) {
        HashSet<Character> set = new HashSet<>();
        for (int i = 0; i < astr.length(); i++) {
            if (!set.add(astr.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
