package AlphaTris;

/**
 * Created by ivan on 29/06/16.
 */
public class PreallocEngine extends LazyPoolEngine
{

    public PreallocEngine(int maxElements, int depth) {
        super(maxElements, depth);
        for(int i=0; i<100000; i++)
        {
            TrisState s = new TrisState();
            pool.pool.add(s);
            pool.all.add(s);
        }
       // System.out.println(pool.pool.size());
    }
}
