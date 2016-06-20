package AlphaTris;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ivan on 11/06/2016.
 *
 */
public class SimpleEngine implements IEngine
{
    protected double upper;
    protected double lower;
    protected boolean termination;
    protected ConcurrentHashMap<TrisState, Double> explored;

    public SimpleEngine()
    {
        explored = new ConcurrentHashMap<>();
    }

    public TrisState parallelNextState(TrisState current, int depth)
    {
        if(current.isTerminal())
            return current;


        upper = current.maxVal();
        lower = current.minVal();
        termination = false;

        current = successorsMax(current).parallelStream().map(x -> new StateWrap(x, parallelRoutine(x, depth)))
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

        if(state.isTerminal())
            return state.eval();


        if(depth == 0)
            return state.heuristic();


        double current = Double.POSITIVE_INFINITY;
        for (TrisState s: successorsMin(state))
        {

            current = Math.min(parallelMax(s, alpha, beta, depth-1), current);
            if(current <= alpha)
                return  current;
            beta = Math.min(current, beta);


            if(beta == lower)
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

        if(state.isTerminal())
            return state.eval();


        if(depth == 0)
        {
            return state.heuristic();
        }


        double current = Double.NEGATIVE_INFINITY;
        for (TrisState s : successorsMax(state))
        {
            current = Math.max(parallelMin(s, alpha, beta, depth-1), current);
            if(current >= beta)
                return current;
            alpha = Math.max(alpha, current);


            if(alpha == upper)
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
            if (val == upper)
                termination = true;
            return val;
        } catch (ABT e)
        {
            return Double.NEGATIVE_INFINITY;
        }
    }




    protected ArrayList<TrisState> successorsMin(TrisState state)
    {
        ArrayList<TrisState> temp = state.successorsMin();
        temp.sort(TrisState::comparatorMin);
        return temp;
    }

    protected ArrayList<TrisState> successorsMax(TrisState state)
    {
        ArrayList<TrisState> temp = state.successorsMax();
        temp.sort(TrisState::comparatorMax);
        return temp;
    }

}




