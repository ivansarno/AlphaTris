package AlphaTris;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ivan on 14/06/2016.
 *
 */
public class SoftPoolEngine implements  IEngine
{
    protected boolean termination;
    protected ConcurrentHashMap<TrisState, Double> explored;
    protected TrisPool pool;
    protected int maxElements;
    protected int depth;
    protected final AtomicInteger resets = new AtomicInteger();

    public SoftPoolEngine(int maxElements, int depth)
    {
        explored = new ConcurrentHashMap<>();
        pool = new TrisPool();
        this.maxElements =maxElements;
        this.depth = depth;
    }

    public TrisState nextState(TrisState current)
    {
        if(current.isTerminal())
            return current;


        termination = false;

        ArrayList<TrisState> successors = successorsMax(current);
        current = successors.parallelStream().map(x -> new StateWrap(x, parallelRoutine(x, depth)))
                .max(StateWrap::compareTo).get().state;


        pool.allocations.set(0);
        pool.requests.set(0);
        pool.refresh();
        return pool.getCopy(current);
    }


    protected double evalMin(TrisState state, double alpha, double beta, int depth)
    {
        if(termination)
            throw new ABT();


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

        {

            for (TrisState s: successors)
            {

                current = Math.min(evalMax(s, alpha, beta, depth-1), current);
                if(current <= alpha)
                {

                    break;
                }

                beta = Math.min(current, beta);


                if(beta == TrisState.minValue)
                    break;

            }
            explored.put(state, current);
            return current;
        }



    }


    protected double evalMax(TrisState state, double alpha, double beta, int depth)
    {
        if(termination)
            throw new ABT();


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


        {
            for (TrisState s : successors)
            {

                current = Math.max(evalMin(s, alpha, beta, depth-1), current);
                if(current >= beta)
                    break;
                alpha = Math.max(alpha, current);


                if(alpha == TrisState.maxValue)
                    break;

            }
            explored.put(state, current);
            return current;
        }



    }

    protected double parallelRoutine(TrisState state, int depth)
    {
        if (termination)
            return Double.NEGATIVE_INFINITY;

        try
        {
            double val = evalMin(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth - 1);
            if (val == TrisState.maxValue)
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
        for(int i = 0; i< TrisState.size; i++)
            for (int j = 0; j < TrisState.size; j++)
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
                        temp.reset(current);
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
        for(int i = 0; i< TrisState.size; i++)
            for (int j = 0; j < TrisState.size; j++)
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
                        temp.reset(current);
                        resets.incrementAndGet();
                    }

                }
            }
        successors.addAll(queue);
        successors.sort(TrisState::comparatorMax);
        return successors;
    }

}
