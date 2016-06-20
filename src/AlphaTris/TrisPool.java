package AlphaTris;


import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ivan on 13/06/2016.
 *
 */
public class TrisPool
{
    protected Deque<TrisState> pool;
    List<TrisState> all;
    protected final AtomicInteger requests = new AtomicInteger();
    protected final AtomicInteger allocations = new AtomicInteger();

    public TrisPool()
    {
        pool = new ConcurrentLinkedDeque<>();
        all = new Vector<>(1000);
    }


    public TrisState getNew()
    {
        requests.incrementAndGet();
        TrisState t = pool.poll();
        if(t == null)
        {
            allocations.incrementAndGet();
            t = new TrisState();
            all.add(t);
            return t;
        }
        t.reset();
        return t;
    }

    public TrisState getCopy(TrisState source)
    {
        requests.incrementAndGet();
        TrisState t = pool.poll();
        if(t == null)
        {
            allocations.incrementAndGet();
            t = new TrisState(source);
            all.add(t);
            return t;
        }
        t.reset(source);
        return t;
    }

    public void refresh()
    {
        pool.clear();
        pool.addAll(all);
    }

    public void dispose(TrisState s)
    {
        pool.push(s);
    }

    public void disposeAll(Collection<TrisState> c)
    {
        for (TrisState t : c)
            pool.push(t);
    }
    public void disposeAll(Enumeration<TrisState> c)
    {
        while (c.hasMoreElements())
            pool.push(c.nextElement());
    }
}
