package AlphaTris;


import java.util.Collection;
import java.util.Deque;
import java.util.Enumeration;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ivan on 13/06/2016.
 *
 */
public class TrisPool
{
    protected Deque<TrisState> pool;
    protected int size;
    protected int serie;
    protected final AtomicInteger requests = new AtomicInteger();
    protected final AtomicInteger allocations = new AtomicInteger();

    public TrisPool(int size, int serie)
    {
        this.size = size;
        this.serie = serie;
        this.pool = new ConcurrentLinkedDeque<>();
    }

    public TrisState getNew()
    {
        requests.incrementAndGet();
        TrisState t = pool.poll();
        if(t == null)
        {
            allocations.incrementAndGet();
            return new TrisState(serie, size);
        }
        t.trisReset();
        return t;
    }
    public TrisState getCopy(TrisState source)
    {
        requests.incrementAndGet();
        TrisState t = pool.poll();
        if(t == null)
        {
            allocations.incrementAndGet();
            return new TrisState(TrisState.arrayCopy(source.state), serie, size);
        }
        t.trisReset(source);
        return t;
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
