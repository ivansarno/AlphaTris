package AlphaTris;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TrisState t = new TrisState(5,5);
        List<TrisState> s1 = t.successorsMin();
        IntensivePoolEngine e = new IntensivePoolEngine(5, 5, 10);
        List<TrisState> s2 = e.successorsMin(t);
        s1.sort(TrisState::comparatorMin);
        System.out.println(s1.equals(s2));
    }
}
