package AlphaTris;


import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by ivan on 13/06/2016.
 *
 */
class TrisPool
{
    private final Deque<TrisState> pool; //lista di tutti i nodi disponibili
    final List<TrisState> all;//lista di tutti i nodi allocati


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

    //rende disponibili tutti i nodi allocati
    void refresh()
    {
        pool.clear();
        pool.addAll(all);
    }

    //rende disponibile un nodo
    void dispose(TrisState s)
    {
        pool.push(s);
    }
}
