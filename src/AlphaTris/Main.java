package AlphaTris;

public class Main {

    public static void main(String[] args) {
        int depth = 4;
        int serie = 4;
        int size = 10;
        int iteration = 10;
        int beam = 10;
        TrisState.init(serie, size);
        System.out.println("serie " + serie +" size " + size + " depth " + depth + " beam " + beam +  " iteration " + iteration);
        test(new SimpleEngine(beam, depth), iteration, size);
        test(new LazyEngine(beam, depth), iteration, size);
        test(new SoftPoolEngine(beam, depth), iteration, size);
        test(new HardPoolEngine(beam, depth), iteration, size);
        test(new LazyPoolEngine(beam, depth), iteration, size);
        test(new PreallocEngine(beam, depth), iteration, size);
        System.out.println();

    }


    public static void test(IEngine ab, int iteration, int size) {
        TrisState t1 = new TrisState();
        TrisState t2 = new TrisState();
        TrisState t3 = new TrisState();
        TrisState t4 = new TrisState();
        TrisState t5 = new TrisState();
        TrisState t6 = new TrisState();
        t2.state[0][0] = -1;
        t3.state[0][size - 1] = -1;
        t4.state[size - 1][0] = -1;
        t5.state[size - 1][size - 1] = -1;
        t6.state[size / 2][size / 2] = -1;


        long time = System.currentTimeMillis();
        for (int i = 0; i < iteration; i++) {
            ab.nextState(t1);
            ab.nextState(t2);
            ab.nextState(t3);
            ab.nextState(t4);
            ab.nextState(t5);
            ab.nextState(t6);
        }
        System.out.println(ab.getClass());
        System.out.println((System.currentTimeMillis() - time) / iteration);

    }
}
