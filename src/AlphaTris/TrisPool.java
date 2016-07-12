package AlphaTris;


import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by ivan on 13/06/2016.
 *
 */
class TrisPool
{
    private final Deque<TrisState> pool;
    final List<TrisState> all;


    TrisPool(int allocation)
    {
        pool = new ConcurrentLinkedDeque<>();
        all = new Vector<>(allocation);
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

}
