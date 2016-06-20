package AlphaTris;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ivan on 11/06/2016.
 */
public class IntensivePoolEngine implements IEngine
{
    protected double upper;
    protected double lower;
    protected boolean termination;
    protected ConcurrentHashMap<TrisState, Double> explored;
    protected TrisPool pool;
    protected int maxElements;
    protected final AtomicInteger resets = new AtomicInteger();

    public IntensivePoolEngine(int serie, int size, int maxElements)
    {
        explored = new ConcurrentHashMap<>();
        pool = new TrisPool(size, serie);
        this.maxElements =maxElements;
    }

    public TrisState parallelNextState(TrisState current, int depth)
    {
        if(current.isTerminal())
            return current;


        upper = current.maxVal();
        lower = current.minVal();
        termination = false;

        ArrayList<TrisState> successors = successorsMax(current);
        current = successors.parallelStream().map(x -> new StateWrap(x, parallelRoutine(x, depth)))
                .max(StateWrap::compareTo).get().state;

        pool.disposeAll(successors);
        pool.disposeAll(explored.keys());

        System.out.println("nodi explored " + explored.size());
        explored.clear();
        System.out.println("nodi nella pool: " + pool.pool.size());
        System.out.println("nodi richiesti " + pool.requests.intValue());
        System.out.println("nodi allocati: " + pool.allocations.intValue());
        System.out.println("nodi resettati " + resets.intValue());
        pool.allocations.set(0);
        pool.requests.set(0);


        return pool.getCopy(current);
    }


    protected double parallelMin(TrisState state, double alpha, double beta, int depth)
    {
        if(termination)
        {
            pool.dispose(state);
            throw new ABT();
        }


        if(explored.containsKey(state))
        {
            double value = explored.get(state);
            pool.dispose(state);
            return value;
        }

        if(state.isTerminal())
        {
            explored.put(state, state.eval());
            return state.eval();
        }


        if(depth == 0)
        {
            explored.put(state, state.heuristic());
            return state.heuristic();
        }


        double current = Double.POSITIVE_INFINITY;
        ArrayList<TrisState> successors = successorsMin(state);
        int u = 0;
        try
        {
            TrisState min = null;
            for (TrisState s: successors)
            {
                u++;
                current = Math.min(parallelMax(s, alpha, beta, depth-1), current);
                if(current <= alpha)
                {
                    min = s;
                    break;
                }

                beta = Math.min(current, beta);


                if(beta == lower)
                    break;

            }
            explored.put(state, current);
            return current;
        }
        catch (ABT e)
        {
            pool.dispose(state);
            throw e;
        }
        finally
        {
            for (;u<successors.size(); u++)
                pool.dispose(successors.get(u));
        }

    }


    protected double parallelMax(TrisState state, double alpha, double beta, int depth)
    {
        if(termination)
        {
            pool.dispose(state);
            throw new ABT();
        }


        if(explored.containsKey(state))
        {
            double value = explored.get(state);
            pool.dispose(state);
            return value;
        }

        if(state.isTerminal())
        {
            explored.put(state, state.eval());
            return state.eval();
        }


        if(depth == 0)
        {
            explored.put(state, state.heuristic());
            return state.heuristic();
        }


        double current = Double.NEGATIVE_INFINITY;
        ArrayList<TrisState> successors = successorsMin(state);
        int u = 0;
        try
        {
            for (TrisState s : successors)
            {
                u++;
                current = Math.max(parallelMin(s, alpha, beta, depth-1), current);
                if(current >= beta)
                    break;
                alpha = Math.max(alpha, current);


                if(alpha == upper)
                    break;

            }
            explored.put(state, current);
            return current;
        }
        catch (ABT e)
        {
            pool.dispose(state);
            throw e;
        }
        finally
        {
            for (;u<successors.size(); u++)
                pool.dispose(successors.get(u));
        }

    }

    protected double parallelRoutine(TrisState state, int depth)
    {
        if (termination)
            return Double.NEGATIVE_INFINITY;

        try
        {
            double val = parallelMin(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth - 1);
            if (val == upper)
                termination = true;
            return val;
        } catch (ABT e)
        {
            return Double.NEGATIVE_INFINITY;
        }
    }




    protected ArrayList<TrisState> successorsMin(TrisState current)
    {
        PriorityQueue<TrisState> queue = new PriorityQueue<>(maxElements, TrisState::comparatorMax);
        ArrayList<TrisState> successors = new ArrayList<>();
        TrisState temp = pool.getCopy(current);
        for(int i=0; i< current.size; i++)
            for (int j = 0; j < current.size; j++)
            {
                if (current.state[i][j] == 0)
                {
                    temp.state[i][j] = -1;
                    if(queue.size() < maxElements)
                    {
                        queue.add(temp);
                        temp = pool.getCopy(current);
                        continue;
                    }
                    if(TrisState.comparatorMin(temp, queue.peek()) == 1)
                        temp.state[i][j] = 0;
                    else
                    {
                        queue.add(temp);
                        temp = queue.poll();
                        temp.trisReset(current);
                        resets.incrementAndGet();
                    }

                }
            }
        successors.addAll(queue);
        successors.sort(TrisState::comparatorMin);
        return successors;
    }

    protected ArrayList<TrisState> successorsMax(TrisState current)
    {
        PriorityQueue<TrisState> queue = new PriorityQueue<>(maxElements, TrisState::comparatorMin);
        ArrayList<TrisState> successors = new ArrayList<>();
        TrisState temp = pool.getCopy(current);
        for(int i=0; i< current.size; i++)
            for (int j = 0; j < current.size; j++)
            {
                if (current.state[i][j] == 0)
                {
                    temp.state[i][j] = 1;
                    if(queue.size() < maxElements)
                    {
                        queue.add(temp);
                        temp = pool.getCopy(current);
                        continue;
                    }
                    if(TrisState.comparatorMax(temp, queue.peek()) == 1)
                        temp.state[i][j] = 0;
                    else
                    {
                        queue.add(temp);
                        temp = queue.poll();
                        temp.trisReset(current);
                        resets.incrementAndGet();
                    }

                }
            }
        successors.addAll(queue);
        successors.sort(TrisState::comparatorMax);
        return successors;
    }

}




