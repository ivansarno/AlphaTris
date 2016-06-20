package AlphaTris;

import java.util.Arrays;

/**
 * Created by ivan on 18/05/2016.
 *
 */
public class TrisState
{
    public final byte[][] state;
    protected static int serie;
    protected static int size;
    protected double value;
    protected double heuristicValue;
    protected static double maxValue;
    protected static double minValue;


    public TrisState(byte[][] state)
    {
        this.state = state;
        heuristicValue = Double.NaN;
        value = 0;
    }

    public TrisState()
    {
        state = new byte[size][size];
        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
                state[i][j] = 0;
        }
        value = 0;
        heuristicValue = Double.NaN;
    }

    public TrisState(TrisState source)
    {
        this.state = arrayCopy(source.state);
        heuristicValue = Double.NaN;
        value = 0;
    }

    public static void init(int serie, int size)
    {
        TrisState.serie = serie;
        TrisState.size = size;
        maxValue = Math.pow(size,4)* size * 10;
        minValue = -maxValue;
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
        return value;
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
                        value = minValue;
                    else value = maxValue;
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
        if(Double.isNaN(heuristicValue))
        {
            if(this.isTerminal())
                heuristicValue = value;
            else heuristicValue = columnsValue() + rowsValue() + diagonalsDXValue() + diagonalsSXValue();
        }
        return heuristicValue;
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

    protected static double weight(int val)
    {
        if(val>0)
            return Math.pow(val, 2);
        return -Math.pow(val, 4);
    }

    protected double columnsValue()
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
                        val += weight(acc);
                    current = state[i][j];
                    acc = current;
                    zeroPrima = zeroDopo;
                    zeroDopo = 0;
                }
            }
            if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                val += weight(acc);
        }


        return val;
    }

    protected double rowsValue()
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
                        val += weight(acc);
                    current = state[i][j];
                    acc = current;
                    zeroPrima = zeroDopo;
                    zeroDopo = 0;
                }
            }
            if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                val += weight(acc);
        }


        return val;
    }

    protected double diagonalsSXValue()
    {
        int current, acc, zeroPrima, zeroDopo;
        double val = 0;

        for (int i = size-1; i >= serie-1; i--)
        {
            zeroPrima = 0;
            zeroDopo = 0;
            current = 0;
            acc = 0;
            int j,h;
            for (j = 0, h = i; h >= 0; j++, h--)
            {

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
                        val += weight(acc);
                    current = state[h][j];
                    acc = current;
                    zeroPrima = zeroDopo;
                    zeroDopo = 0;
                }
            }

            if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                val += weight(acc);
        }

        for (int i = 1; i <= size-serie; i++)
        {
            zeroPrima = 0;
            zeroDopo = 0;
            current = 0;
            acc = 0;
            int j,h;
            for (j = i, h = size-1; j < size && h >= 0; j++, h--)
            {

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
                        val += weight(acc);
                    current = state[h][j];
                    acc = current;
                    zeroPrima = zeroDopo;
                    zeroDopo = 0;
                }
            }

            if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                val += weight(acc);
        }


        return val;
    }

    protected double diagonalsDXValue()
    {
        int current, acc, zeroPrima, zeroDopo;
        double val = 0;

        for (int i = 0; i <= size-serie; i++)
        {
            zeroPrima = 0;
            zeroDopo = 0;
            current = 0;
            acc = 0;
            int j,h;
            for (j = 0, h = i; j < size && h< size; j++, h++)
            {

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
                        val += weight(acc);
                    current = state[h][j];
                    acc = current;
                    zeroPrima = zeroDopo;
                    zeroDopo = 0;
                }
            }

            if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                val += weight(acc);
        }

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
                        val += weight(acc);
                    current = state[h][j];
                    acc = current;
                    zeroPrima = zeroDopo;
                    zeroDopo = 0;
                }
            }

            if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                val += weight(acc);
        }


        return val;
    }


    protected static int comparatorMin(TrisState a, TrisState b)
    {
        return Double.compare(a.heuristic(), b.heuristic());
    }
    protected static int comparatorMax(TrisState a, TrisState b)
    {
        return Double.compare(b.heuristic(), a.heuristic());
    }

    protected static void arrayOverwrite(byte[][] destination, byte[][] source)
    {
        for(int i = 0; i < source.length; i++)
            for(int j = 0; j < source.length; j++)
                destination[i][j] = source[i][j];
    }
    protected void reset()
    {
        setZero(state);
        value = 0.0;
        heuristicValue = Double.NaN;
    }

    static void setZero(byte[][] a)
    {
        for(int i=0; i< a.length; i++)
            for(int j=0; j<a.length; j++)
            {
                a[i][j] = 0;
            }
    }
    protected void reset(TrisState source)
    {
        arrayOverwrite(this.state, source.state);
        value = 0.0;
        heuristicValue = Double.NaN;
    }

}



