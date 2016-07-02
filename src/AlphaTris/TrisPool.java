package AlphaTris;


import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ivan on 13/06/2016.
 *
 */
class TrisPool
{
    Deque<TrisState> pool;
    List<TrisState> all;


    TrisPool()
    {
        pool = new ConcurrentLinkedDeque<>();
        all = new Vector<>(1000);
    }


    public TrisState getNew()
    {

        TrisState temp = pool.poll();
        if(temp == null)
        {

            temp = new TrisState();
            all.add(temp);
            return temp;
        }
        temp.reset();
        return temp;
    }

    TrisState getCopy(TrisState source)
    {

        TrisState temp = pool.poll();
        if(temp == null)
        {
            temp = new TrisState(source);
            all.add(temp);
            return temp;
        }
        temp.reset(source);
        return temp;
    }

    void refresh()
    {
        pool.clear();
        pool.addAll(all);
    }

    void dispose(TrisState s)
    {
        pool.push(s);
    }

    void disposeAll(Collection<TrisState> c)
    {
        for (TrisState t : c)
            pool.push(t);
    }
}
