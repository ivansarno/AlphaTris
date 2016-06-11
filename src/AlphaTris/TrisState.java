package AlphaTris;

import com.google.common.collect.MinMaxPriorityQueue;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ivan on 18/05/2016.
 *
 */
public class TrisState
{
    public final byte[][] state;
    protected final int serie;
    protected final int size;
    protected double result;
    public static long generated = 0;
    protected double hval;

    public TrisState(byte[][] state, int serie, int size)
    {
        this.state = state;
        this.serie = serie;
        this.size = size;
        generated ++;
        hval = Double.NaN;
        result = 0;
    }

    public TrisState(int serie, int size)
    {
        this.serie = serie;
        this.size = size;
        state = new byte[size][size];
        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
                state[i][j] = 0;
        }
        result = 0;
        generated++;
        hval = Double.NaN;
    }


    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(size*size);
        for(int i=0; i<size; i++)
        {
            for(int j=0; j<size; j++)
                if(state[i][j] == 0)
                    builder.append("_ ");
                else  if(state[i][j] == 1)
                    builder.append("O ");
                else builder.append("X ");
            builder.append('\n');
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof TrisState)) return false;

        TrisState trisState = (TrisState) o;
        return Arrays.deepEquals(state, trisState.state);

    }

    @Override
    public int hashCode()
    {
        return Arrays.deepHashCode(state);
    }


    public double eval()
    {
        return result;
    }


    public boolean isTerminal()
    {
        int i, j;
        boolean noMoves = true;
        for(i=0; i < size; i++)
        {
            for(j=0; j<size;j++)
            {
                if(checkSequence(i,j))
                {
                    if(state[i][j] == -1)
                        result = minVal();
                    else result = maxVal();
                    return true;
                }
                if(state[i][j] == 0)
                    noMoves = false;
            }

        }
        return noMoves;
    }



    public double heuristic()
    {
        if(Double.isNaN(hval))
        {
            if(this.isTerminal())
                hval = result;
            else hval = checkColonne() + checkRighe() + checkDiagonaleDX() + checkDiagonaleSX();
        }
        return hval;
    }


    public double maxVal()
    {
        return Math.pow(size,4)* size * 10;
    }

    public double minVal()
    {
        return -maxVal();
    }



    protected boolean checkSequence(int i, int j)
    {
        if(state[i][j] == 0)
            return false;
        if(i+serie-1 < size)
        {
            int h = i + 1;
            int s = 1;
            while(state[i][j] == state[h][j])
            {
                h++;
                s++;
                if(s == serie)
                    return true;
            }
        }

        if(j+serie-1 < size)
        {
            int h = j + 1;
            int s = 1;
            while(state[i][j] == state[i][h])
            {
                h++;
                s++;
                if(s == serie)
                    return true;
            }
        }

        if(i+serie-1 < size && j+serie-1 < size)
        {
            int h = i + 1;
            int y = j + 1;
            int s = 1;
            while(state[i][j] == state[h][y])
            {
                h++;
                s++;
                y++;
                if(s == serie)
                    return true;
            }
        }

        if(i+serie-1 < size && j-serie+1 >= 0)
        {
            int h = i + 1;
            int s = 1;
            int y = j - 1;
            while(state[i][j] == state[h][y])
            {
                h++;
                s++;
                y--;
                if(s == serie)
                    return true;
            }
        }
        return false;
    }



    protected static byte[][] arrayCopy(byte[][] a)
    {
        int size = a.length;
        byte[][] n = new byte[size][];
        for(int i=0; i<size; i++)
            n[i] = Arrays.copyOf(a[i], size);
        return n;
    }

    protected static double f(int val)
    {
        if(val>0)
            return Math.pow(val, 2);
        return -Math.pow(val, 4);
    }

    protected double checkColonne()
    {
        int current, acc, zeroPrima, zeroDopo;
        double val = 0;

        for (int j = 0; j < size; j++)
        {
            zeroPrima = 0;
            zeroDopo = 0;
            current = 0;
            acc = 0;
            for (int i = 0; i < size; i++)
            {
                if (current == 0 && state[i][j] == 0)
                {
                    current = state[i][j];
                    acc += current;
                    zeroPrima++;
                    continue;
                }
                if (current == 0 && state[i][j] != 0)
                {
                    current = state[i][j];
                    acc += current;
                    continue;
                }
                if (0 == state[i][j])
                {
                    zeroDopo++;
                    continue;
                }
                if (current == state[i][j])
                {
                    acc += current;
                    continue;
                }
                if (current != state[i][j])
                {
                    if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                        val += f(acc);
                    current = state[i][j];
                    acc = current;
                    zeroPrima = zeroDopo;
                    zeroDopo = 0;
                }
            }
            if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                val += f(acc);
        }


        return val;
    }

    protected double checkRighe()
    {
        int current, acc, zeroPrima, zeroDopo;
        double val = 0;

        for (int i = 0; i < size; i++)
        {
            zeroPrima = 0;
            zeroDopo = 0;
            current = 0;
            acc = 0;
            for (int j = 0; j < size; j++)
            {
                if (current == 0 && state[i][j] == 0)
                {
                    current = state[i][j];
                    acc += current;
                    zeroPrima++;
                    continue;
                }
                if (current == 0 && state[i][j] != 0)
                {
                    current = state[i][j];
                    acc += current;
                    continue;
                }
                if (0 == state[i][j])
                {
                    zeroDopo++;
                    continue;
                }
                if (current == state[i][j])
                {
                    acc += current;
                    continue;
                }
                if (current != state[i][j])
                {
                    if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                        val += f(acc);
                    current = state[i][j];
                    acc = current;
                    zeroPrima = zeroDopo;
                    zeroDopo = 0;
                }
            }
            if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                val += f(acc);
        }


        return val;
    }

    protected double checkDiagonaleSX()
    {
        int current, acc, zeroPrima, zeroDopo;
        double val = 0;
        //System.out.println();
        for (int i = size-1; i >= serie-1; i--)
        {
            zeroPrima = 0;
            zeroDopo = 0;
            current = 0;
            acc = 0;
            int j,h;
            for (j = 0, h = i; h >= 0; j++, h--)
            {
                //System.out.println(state[h][j]);
                if (current == 0 && state[h][j] == 0)
                {
                    current = state[h][j];
                    acc += current;
                    zeroPrima++;
                    continue;
                }
                if (current == 0 && state[h][j] != 0)
                {
                    current = state[h][j];
                    acc += current;
                    continue;
                }
                if (0 == state[h][j])
                {
                    zeroDopo++;
                    continue;
                }
                if (current == state[h][j])
                {
                    acc += current;
                    continue;
                }
                if (current != state[h][j])
                {
                    if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                        val += f(acc);
                    current = state[h][j];
                    acc = current;
                    zeroPrima = zeroDopo;
                    zeroDopo = 0;
                }
            }
            //System.out.println();
            if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                val += f(acc);
        }
        //System.out.println();
        for (int i = 1; i <= size-serie; i++)
        {
            zeroPrima = 0;
            zeroDopo = 0;
            current = 0;
            acc = 0;
            int j,h;
            for (j = i, h = size-1; j < size && h >= 0; j++, h--)
            {
                //System.out.println(state[h][j]);
                if (current == 0 && state[h][j] == 0)
                {
                    current = state[h][j];
                    acc += current;
                    zeroPrima++;
                    continue;
                }
                if (current == 0 && state[h][j] != 0)
                {
                    current = state[h][j];
                    acc += current;
                    continue;
                }
                if (0 == state[h][j])
                {
                    zeroDopo++;
                    continue;
                }
                if (current == state[h][j])
                {
                    acc += current;
                    continue;
                }
                if (current != state[h][j])
                {
                    if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                        val += f(acc);
                    current = state[h][j];
                    acc = current;
                    zeroPrima = zeroDopo;
                    zeroDopo = 0;
                }
            }
            //System.out.println();
            if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                val += f(acc);
        }

        //System.out.println();
        return val;
    }

    protected double checkDiagonaleDX()
    {
        int current, acc, zeroPrima, zeroDopo;
        double val = 0;
        //System.out.println();
        for (int i = 0; i <= size-serie; i++)
        {
            zeroPrima = 0;
            zeroDopo = 0;
            current = 0;
            acc = 0;
            int j,h;
            for (j = 0, h = i; j < size && h< size; j++, h++)
            {
                //System.out.println(state[h][j]);
                if (current == 0 && state[h][j] == 0)
                {
                    current = state[h][j];
                    acc += current;
                    zeroPrima++;
                    continue;
                }
                if (current == 0 && state[h][j] != 0)
                {
                    current = state[h][j];
                    acc += current;
                    continue;
                }
                if (0 == state[h][j])
                {
                    zeroDopo++;
                    continue;
                }
                if (current == state[h][j])
                {
                    acc += current;
                    continue;
                }
                if (current != state[h][j])
                {
                    if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                        val += f(acc);
                    current = state[h][j];
                    acc = current;
                    zeroPrima = zeroDopo;
                    zeroDopo = 0;
                }
            }
            //System.out.println();
            if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                val += f(acc);
        }
        //System.out.println();
        for (int i = 1; i <= size-serie; i++)
        {
            zeroPrima = 0;
            zeroDopo = 0;
            current = 0;
            acc = 0;
            int j,h;
            for (j = i, h = 0; j < size && h< size; j++, h++)
            {
                //System.out.println(state[h][j]);
                if (current == 0 && state[h][j] == 0)
                {
                    current = state[h][j];
                    acc += current;
                    zeroPrima++;
                    continue;
                }
                if (current == 0 && state[h][j] != 0)
                {
                    current = state[h][j];
                    acc += current;
                    continue;
                }
                if (0 == state[h][j])
                {
                    zeroDopo++;
                    continue;
                }
                if (current == state[h][j])
                {
                    acc += current;
                    continue;
                }
                if (current != state[h][j])
                {
                    if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                        val += f(acc);
                    current = state[h][j];
                    acc = current;
                    zeroPrima = zeroDopo;
                    zeroDopo = 0;
                }
            }
            //System.out.println();
            if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                val += f(acc);
        }

        //System.out.println();
        return val;
    }

    public ArrayList<TrisState> successorsMax()
    {
        MinMaxPriorityQueue<TrisState> queue = MinMaxPriorityQueue.orderedBy(TrisState::comparatorMax).maximumSize(10).create();
        ArrayList<TrisState> successors = new ArrayList<>();
        TrisState current = new TrisState(arrayCopy(state), serie, size);
        for(int i=0; i< size; i++)
            for (int j = 0; j < size; j++)
            {
                if (state[i][j] == 0)
                {
                    current.state[i][j] = 1;
                    if(queue.size() < 10)
                    {
                        queue.add(current);
                        current = new TrisState(arrayCopy(state), serie, size);
                        continue;
                    }
                    if(comparatorMax(current, queue.peekLast()) == 1)
                        current.state[i][j] = 0;
                    else
                    {
                        queue.add(current);
                        current = new TrisState(arrayCopy(state), serie, size);
                    }

                }
            }
        successors.addAll(queue);
        return successors;
    }


    public  ArrayList<TrisState> successorsMin()
    {
        MinMaxPriorityQueue<TrisState> queue = MinMaxPriorityQueue.orderedBy(TrisState::comparatorMin).maximumSize(10).create();
        ArrayList<TrisState> successors = new ArrayList<>();
        TrisState current = new TrisState(arrayCopy(state), serie, size);
        for(int i=0; i< size; i++)
            for (int j = 0; j < size; j++)
            {
                if (state[i][j] == 0)
                {
                    current.state[i][j] = -1;
                    if(queue.size() < 10)
                    {
                        queue.add(current);
                        current = new TrisState(arrayCopy(state), serie, size);
                        continue;
                    }
                    if(comparatorMin(current, queue.peekLast()) == 1)
                        current.state[i][j] = 0;
                    else
                    {
                        queue.add(current);
                        current = new TrisState(arrayCopy(state), serie, size);
                    }

                }
            }
        successors.addAll(queue);
        return successors;
    }

    protected static int comparatorMin(TrisState a, TrisState b)
    {
        return Double.compare(a.heuristic(), b.heuristic());
    }
    protected static int comparatorMax(TrisState a, TrisState b)
    {
        return Double.compare(b.heuristic(), a.heuristic());
    }

}