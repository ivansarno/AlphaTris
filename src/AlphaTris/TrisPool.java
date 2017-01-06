/**
 AlphaTris

 Copyright 2016 Ivan Sarno

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 */
package AlphaTris;


import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

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
