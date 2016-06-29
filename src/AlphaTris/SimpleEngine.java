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

        explored.clear();;
        return current;
    }


    protected double parallelMin(TrisState state, double alpha, double beta, int depth)
    {
        if(termination)
            throw new Interruption();

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
            throw new Interruption();

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
        } catch (Interruption e)
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
                if (temp.state[i][j] == 0)
                {
                    temp.state[i][j] = -1;
                    temp.revalue();
                    successors.add(temp);
                    temp= new TrisState(current);

                }
            }
        successors.sort(TrisState::comparatorMin);
        while (successors.size()> maxElements)
            successors.remove(successors.size()-1);
        return successors;
    }

    protected ArrayList<TrisState> successorsMax(TrisState current)
    {

        ArrayList<TrisState> successors = new ArrayList<>();
        TrisState temp = new TrisState(current);
        for(int i = 0; i< TrisState.size; i++)
            for (int j = 0; j < TrisState.size; j++)
            {
                if (temp.state[i][j] == 0)
                {
                    temp.state[i][j] = 1;
                    temp.revalue();
                    successors.add(temp);
                    temp= new TrisState(current);

                }
            }

        successors.sort(TrisState::comparatorMax);
        while (successors.size()> maxElements)
            successors.remove(successors.size()-1);
        return successors;
    }

}




