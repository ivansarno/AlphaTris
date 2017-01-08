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

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

class Engine
{
    private boolean termination; //threads termination flag
    private final ConcurrentHashMap<TrisState, Double> explored;
    private final TrisPool pool;
    final int maxElements;
    final int maxDepth;

    Engine(int maxElements, int depth)
    {
        explored = new ConcurrentHashMap<>();
        this.maxElements = maxElements;
        this.maxDepth = depth;

        //preallocates of nodes in the pool
        int allocations = TrisState.size > 20 ? 100000 : 150000;
        pool = new TrisPool(allocations);
        for (int i = 0; i < allocations; i++)
        {
            TrisState s = new TrisState();
            pool.all.add(s);
        }
    }

    //compute next move
    TrisState nextState(TrisState current)
    {
        if(current.isTerminal)
            return current;


        termination = false;

        //generates successors, calls the rating procedure, then chooses the maximum
        ArrayList<TrisState> successors = successorsMax(current);
        TrisState temp = successors.parallelStream().map(x -> new StateWrap(x, parallelRoutine(x, maxDepth)))
                .max(StateWrap::compareTo).get().state;


        current.reset(temp);
        return current;
    }

    //clears the table of explored states and makes all states available in the allocated pool
    void refresh()
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
            pool.dispose(state); //this state will not be used more, add it at the pool
            return value;
        }


        if(depth == 0)
        {
            explored.put(state, state.heuristicValue);
            return state.heuristicValue;
        }


        double current = Double.POSITIVE_INFINITY;
        ArrayList<TrisState> successors = successorsMin(state); //generates successors 
        {
            for (TrisState s: successors)
            {
                current = Math.min(evalMax(s, alpha, beta, depth-1), current);
                if(current <= alpha)
                    break;
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
            //compute value of the state
            double val = evalMin(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth - 1);

            //this thread found goal state, interrumpts other threads
            if (val == TrisState.maxValue)
                termination = true;
            return val;
        } catch (Interruption e) //other thread foud goal state, return minimum value
        {
            return Double.NEGATIVE_INFINITY;
        }
    }




    private ArrayList<TrisState> successorsMin(TrisState current)
    {
        //The priority queue uses the comparatorfor Max because I want a reversed order
        //the worst element in head of the queue, to compare and replace
        PriorityQueue<TrisState> queue = new PriorityQueue<>(maxElements, TrisState::comparatorMax);
        TrisState temp = pool.getCopy(current);
        for(int i = 0; i< TrisState.size; i++)
            for (int j = 0; j < TrisState.size; j++)
            {
                if (current.state[i][j] == 0)//found a void cell
                {
                    temp.state[i][j] = -1;//sign the move for the user
                    temp.revalue(); //compute the nuw value
                    if(queue.size() < maxElements)
                    {
                        //not exceeded the number of successors
                        //add this to the queue
                        queue.add(temp);
                        temp = pool.getCopy(current);
                        continue;
                    }
                    if(TrisState.comparatorMin(temp, queue.peek()) == 1)
                    {
                        //successor is worse than those in the queue
                        //reset it
                        temp.state[i][j] = 0;
                        temp.revalue(current);
                    }
                    else
                    {
                        //add this to the queue
                        queue.add(temp);
                        //estract the worst and reset it
                        temp = queue.poll();
                        temp.reset(current);
                    }

                }
            }
        ArrayList<TrisState> successors = new ArrayList<>(queue);
        successors.sort(TrisState::comparatorMin); //orders the successors
        return successors;
    }

    private ArrayList<TrisState> successorsMax(TrisState current)
    {
        PriorityQueue<TrisState> queue = new PriorityQueue<>(maxElements, TrisState::comparatorMin);
        TrisState temp = pool.getCopy(current);

        for(int i = 0; i< TrisState.size; i++)
            for (int j = 0; j < TrisState.size; j++)
            {
                if (current.state[i][j] == 0)
                {
                    temp.state[i][j] = 1;//sign the move for the program
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
                        temp.revalue(current);
                    }
                    else
                    {
                        queue.add(temp);
                        temp = queue.poll();
                        temp.reset(current);
                    }

                }
            }
        ArrayList<TrisState> successors = new ArrayList<>(queue);
        successors.sort(TrisState::comparatorMax);
        return successors;
    }
}
