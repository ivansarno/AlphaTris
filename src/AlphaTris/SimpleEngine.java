package AlphaTris;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ivan on 11/06/2016.
 *
 */
public class SimpleEngine implements IEngine
{
    protected int maxElements;
    protected int maxDepth;
    protected boolean termination;
    protected ConcurrentHashMap<TrisState, Double> explored;

    public SimpleEngine(int maxElements, int depth)
    {
        this.maxElements = maxElements;
        this.maxDepth = depth;
        explored = new ConcurrentHashMap<>();
    }

    public TrisState nextState(TrisState current)
    {
        if(current.isTerminal)
            return current;


        termination = false;

        current = successorsMax(current).parallelStream().map(x -> new StateWrap(x, parallelRoutine(x, maxDepth)))
                .max(StateWrap::compareTo).get().state;

        explored.clear();
        System.gc();
        return current;
    }


    protected double parallelMin(TrisState state, double alpha, double beta, int depth)
    {
        if(termination)
            throw new ABT();

        if(explored.containsKey(state))
            return explored.get(state);

        if(state.isTerminal)
            return state.value;


        if(depth == 0)
            return state.heuristicValue;


        double current = Double.POSITIVE_INFINITY;
        for (TrisState s: successorsMin(state))
        {

            current = Math.min(parallelMax(s, alpha, beta, depth-1), current);
            if(current <= alpha)
                return  current;
            beta = Math.min(current, beta);


            if(beta == TrisState.minValue)
                break;

        }

        explored.put(state, current);
        return current;
    }


    protected double parallelMax(TrisState state, double alpha, double beta, int depth)
    {
        if(termination)
            throw new ABT();

        if(explored.containsKey(state))
            return explored.get(state);

        if(state.isTerminal)
        {
            explored.put(state, state.value);
            return state.value;
        }


        if(depth == 0)
        {
            explored.put(state, state.heuristicValue);
            return state.heuristicValue;
        }


        double current = Double.NEGATIVE_INFINITY;
        for (TrisState s : successorsMax(state))
        {
            current = Math.max(parallelMin(s, alpha, beta, depth-1), current);
            if(current >= beta)
                return current;
            alpha = Math.max(alpha, current);


            if(alpha == TrisState.maxValue)
                break;

        }
        explored.put(state, current);
        return current;
    }

    protected double parallelRoutine(TrisState state, int depth)
    {
        if (termination)
            return Double.NEGATIVE_INFINITY;

        try
        {
            double val = parallelMin(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth - 1);
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
        TrisState temp = new TrisState(current);
        for(int i = 0; i< TrisState.size; i++)
            for (int j = 0; j < TrisState.size; j++)
            {
                if (current.state[i][j] == 0)
                {
                    temp.state[i][j] = -1;
                    if(queue.size() < maxElements)
                    {
                        queue.add(temp);
                        temp = new TrisState(current);
                        continue;
                    }
                    if(TrisState.comparatorMin(temp, queue.peek()) == 1)
                    {
                        temp.state[i][j] = 0;
                        temp.softReset(current);
                    }
                    else
                    {
                        queue.add(temp);
                        temp = queue.poll();
                        temp.reset(current);
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
        TrisState temp = new TrisState(current);
        for(int i = 0; i< TrisState.size; i++)
            for (int j = 0; j < TrisState.size; j++)
            {
                if (current.state[i][j] == 0)
                {
                    temp.state[i][j] = 1;
                    temp.revalue();
                    if(queue.size() < maxElements)
                    {
                        queue.add(temp);
                        temp = new TrisState(current);
                        continue;
                    }
                    if(TrisState.comparatorMax(temp, queue.peek()) == 1)
                    {
                        temp.state[i][j] = 0;
                        temp.softReset(current);
                    }
                    else
                    {
                        queue.add(temp);
                        temp = queue.poll();
                        temp.reset(current);
                    }

                }
            }
        successors.addAll(queue);
        successors.sort(TrisState::comparatorMax);
        return successors;
    }

}




