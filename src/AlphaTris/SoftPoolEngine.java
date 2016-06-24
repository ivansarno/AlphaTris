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
    private boolean termination;
    private ConcurrentHashMap<TrisState, Double> explored;
    TrisPool pool;
    int maxElements;
    int depth;
    final AtomicInteger resets = new AtomicInteger();

    public SoftPoolEngine(int maxElements, int depth)
    {
        explored = new ConcurrentHashMap<>();
        pool = new TrisPool();
        this.maxElements =maxElements;
        this.depth = depth;
    }

    public TrisState nextState(TrisState current)
    {
        if(current.isTerminal)
            return current;


        termination = false;

        ArrayList<TrisState> successors = successorsMax(current);
        TrisState temp = successors.parallelStream().map(x -> new StateWrap(x, parallelRoutine(x, depth)))
                .max(StateWrap::compareTo).get().state;


        pool.allocations.set(0);
        pool.requests.set(0);
        pool.refresh();
        current.reset(temp);
        return current;
    }
    public TrisState nextState2(TrisState current)
    {
        if(current.isTerminal)
            return current;


        termination = false;

        ArrayList<StateWrap> successors = new ArrayList<>(maxElements);
        successorsMax(current).parallelStream().map(x -> new StateWrap(x, parallelRoutine(x, depth))).sequential().forEach(successors::add);
        StateWrap max = successors.stream().max(StateWrap::compareTo).get();
        TrisState temp = successors.stream().filter(x -> x.compareTo(max) == 0).
                max((x,y) -> Double.compare(x.state.heuristicValue, y.state.heuristicValue)).get().state;

        pool.allocations.set(0);
        pool.requests.set(0);
        pool.refresh();
        current.reset(temp);
        return current;
    }


    protected double evalMin(TrisState state, double alpha, double beta, int depth)
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


    protected double evalMax(TrisState state, double alpha, double beta, int depth)
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
        } catch (Interruption e)
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
                if (temp.state[i][j] == 0)
                {
                    temp.state[i][j] = -1;
                    temp.revalue();
                    successors.add(temp);
                    temp= pool.getCopy(current);

                }
            }
        successors.sort(TrisState::comparatorMin);
        while (successors.size()> maxElements)
            pool.dispose(successors.remove(successors.size()-1));
        return successors;
    }

    protected ArrayList<TrisState> successorsMax(TrisState current)
    {

        ArrayList<TrisState> successors = new ArrayList<>();
        TrisState temp = pool.getCopy(current);
        for(int i = 0; i< TrisState.size; i++)
            for (int j = 0; j < TrisState.size; j++)
            {
                if (temp.state[i][j] == 0)
                {
                    temp.state[i][j] = 1;
                    temp.revalue();
                    successors.add(temp);
                    temp= pool.getCopy(current);

                }
            }

        successors.sort(TrisState::comparatorMax);
        while (successors.size()> maxElements)
            pool.dispose(successors.remove(successors.size()-1));
        return successors;
    }

}
