package AlphaTris;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by ivan on 20/06/2016.
 *
 */
class Engine
{
    private boolean termination;
    private final ConcurrentHashMap<TrisState, Double> explored;
    private final TrisPool pool;
    final int maxElements;
    final int maxDepth;

    public Engine(int maxElements, int depth) {
        explored = new ConcurrentHashMap<>();
        pool = new TrisPool();
        this.maxElements = maxElements;
        this.maxDepth = depth;
        for (int i = 0; i < 10000 * TrisState.size; i++) {
            TrisState s = new TrisState();
            pool.all.add(s);
        }
    }


    public TrisState nextState(TrisState current)
    {
        if(current.isTerminal)
            return current;


        termination = false;

        ArrayList<TrisState> successors = successorsMax(current);
        TrisState temp = successors.parallelStream().map(x -> new StateWrap(x, parallelRoutine(x, maxDepth)))
                .max(StateWrap::compareTo).get().state;


        current.reset(temp);
        return current;
    }

    public void refresh()
    {
        explored.clear();
        pool.refresh();
    }



    private double evalMin(TrisState state, double alpha, double beta, int depth)
    {
        if(termination)
            throw new Interruption();

        if(state.isTerminal)
        {
            explored.put(state, state.value);
            return state.value;
        }

        if(explored.containsKey(state))
        {
            double value = explored.get(state);
            pool.dispose(state);
            return value;
        }


        if(depth == 0)
        {
            explored.put(state, state.heuristicValue);
            return state.heuristicValue;
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


    private double evalMax(TrisState state, double alpha, double beta, int depth)
    {
        if(termination)
            throw new Interruption();

        if(state.isTerminal)
        {
            explored.put(state, state.value);
            return state.value;
        }

        if(explored.containsKey(state))
        {
            double value = explored.get(state);
            pool.dispose(state);
            return value;
        }


        if(depth == 0)
        {
            explored.put(state, state.heuristicValue);
            return state.heuristicValue;
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

    private double parallelRoutine(TrisState state, int depth)
    {
        if (termination)
            return Double.NEGATIVE_INFINITY;

        try
        {
            double val = evalMin(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth - 1);
            if (val == TrisState.maxValue)
                termination = true;
            return val;
        } catch (Interruption e)
        {
            return Double.NEGATIVE_INFINITY;
        }
    }




    private ArrayList<TrisState> successorsMin(TrisState current)
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

    private ArrayList<TrisState> successorsMax(TrisState current)
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
                    temp.revalue();
                    if(queue.size() < maxElements)
                    {
                        queue.add(temp);
                        temp = pool.getCopy(current);
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