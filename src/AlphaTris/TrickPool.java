package AlphaTris;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Created by ivan on 14/06/2016.
 *
 */
public class TrickPool extends TrisPool
{
    List<TrisState> all;

    public TrickPool(int size, int serie)
    {
        super(size, serie);
        all = new Vector<>(1000);
    }

    @Override
    public TrisState getNew()
    {
        requests.incrementAndGet();
        TrisState t = pool.poll();
        if(t == null)
        {
            allocations.incrementAndGet();
            t = new TrisState(serie, size);
            all.add(t);
            return t;
        }
        t.trisReset();
        return t;
    }

    @Override
    public TrisState getCopy(TrisState source)
    {
        requests.incrementAndGet();
        TrisState t = pool.poll();
        if(t == null)
        {
            allocations.incrementAndGet();
            t = new TrisState(TrisState.arrayCopy(source.state), serie, size);
            all.add(t);
            return t;
        }
        t.trisReset(source);
        return t;
    }

    public void refresh()
    {
       pool.clear();
       pool.addAll(all);
    }
}
