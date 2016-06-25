package AlphaTris;

import java.util.Arrays;

/**
 * Created by ivan on 18/05/2016.
 *
 */
class TrisState
{
    final byte[][] state;
    private static int serie;
    static int size;
    double value;
    double heuristicValue;
    static double maxValue;
    static double minValue;
    boolean isTerminal;



    TrisState()
    {
        state = new byte[size][size];
        setZero(state);
    }

    TrisState(TrisState source)
    {
        this.state = arrayCopy(source.state);
        isTerminal = terminationTest();//setta anche value
        if(isTerminal)
            heuristicValue = value;
        else heuristicValue = heuristicEvaluation();
    }

    static void init(int serie, int size)
    {
        TrisState.serie = serie;
        TrisState.size = size;
        maxValue = Math.pow(size,6) * 10;
        minValue = -maxValue;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(size*size);
        builder.append(" ");
        for(int i=0; i<size; i++)
            builder.append(" " + i);
        builder.append("\n");
        for(int i=0; i<size; i++)
        {
            builder.append(i + " ");
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




    //setta anche value
    private boolean terminationTest()
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



    private double heuristicEvaluation()
    {
        return columnsValue() + rowsValue() + diagonalsDXValue() + diagonalsSXValue();
    }



    private boolean checkSequence(int i, int j)
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



    private static byte[][] arrayCopy(byte[][] a)
    {
        int size = a.length;
        byte[][] n = new byte[size][];
        for(int i=0; i<size; i++)
            n[i] = Arrays.copyOf(a[i], size);
        return n;
    }

    private static double weight1(int val, int length)
    {
        if(val>0)
            return Math.pow(val, 2);//*length * (serie/size);
        return -Math.pow(val, 4);//*length * (serie/size);
    }

    private static double weight2(int val, int length)
    {
        if(val>0)
            return Math.pow(val, 2);
        return Math.pow(val, 3);
    }

    private static double weight3(int val, int length)
    {
        if(val>0)
            return Math.pow(val, 2)*length;
        return Math.pow(val, 3)*length;
    }

    private static double weight4(int val, int length)
    {
        if(val>0)
            return Math.pow(val, 2)*length;
        return -Math.pow(val, 4)*length;
    }
    private static double weight5(int val, int length)
    {
        if(val>0)
            return Math.pow(val, 2);
        return -Math.pow(val, 4)*length/size;
    }

    private static double weight(int val, int length)
    {
        return weight5(val, length);
    }

    private double columnsValue()
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
                        val += weight(acc, zeroDopo+zeroPrima);
                    current = state[i][j];
                    acc = current;
                    zeroPrima = zeroDopo;
                    zeroDopo = 0;
                }
            }
            if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                val += weight(acc, zeroDopo+zeroPrima);
        }


        return val;
    }

    private double rowsValue()
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
                        val += weight(acc, zeroDopo+zeroPrima);
                    current = state[i][j];
                    acc = current;
                    zeroPrima = zeroDopo;
                    zeroDopo = 0;
                }
            }
            if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                val += weight(acc, zeroDopo+zeroPrima);
        }


        return val;
    }

    private double diagonalsSXValue()
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
                        val += weight(acc, zeroDopo+zeroPrima);
                    current = state[h][j];
                    acc = current;
                    zeroPrima = zeroDopo;
                    zeroDopo = 0;
                }
            }

            if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                val += weight(acc, zeroDopo+zeroPrima);
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
                        val += weight(acc, zeroDopo+zeroPrima);
                    current = state[h][j];
                    acc = current;
                    zeroPrima = zeroDopo;
                    zeroDopo = 0;
                }
            }

            if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                val += weight(acc, zeroDopo+zeroPrima);
        }


        return val;
    }

    private double diagonalsDXValue()
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
                        val += weight(acc, zeroDopo+zeroPrima);
                    current = state[h][j];
                    acc = current;
                    zeroPrima = zeroDopo;
                    zeroDopo = 0;
                }
            }

            if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                val += weight(acc, zeroDopo+zeroPrima);
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
                        val += weight(acc, zeroDopo+zeroPrima);
                    current = state[h][j];
                    acc = current;
                    zeroPrima = zeroDopo;
                    zeroDopo = 0;
                }
            }

            if (Math.abs(acc) + zeroPrima + zeroDopo >= serie)
                val += weight(acc, zeroDopo+zeroPrima);
        }


        return val;
    }


    static int comparatorMin(TrisState a, TrisState b)
    {
        return Double.compare(a.heuristicValue, b.heuristicValue);
    }
    static int comparatorMax(TrisState a, TrisState b)
    {
        return Double.compare(b.heuristicValue, a.heuristicValue);
    }

    private static void arrayOverwrite(byte[][] destination, byte[][] source)
    {
        for(int i = 0; i < source.length; i++)
            System.arraycopy(source[i], 0, destination[i], 0, source.length);
    }
    void reset()
    {
        setZero(state);
        value = 0.0;
        isTerminal = false;
        heuristicValue = 0;
    }

    static void setZero(byte[][] a)
    {
        for(int i=0; i< a.length; i++)
            for(int j=0; j<a.length; j++)
            {
                a[i][j] = 0;
            }
    }
    void reset(TrisState source)
    {
        arrayOverwrite(this.state, source.state);
        value = source.value;
        isTerminal = source.isTerminal;
        heuristicValue = source.heuristicValue;
    }

    void revalue()
    {
        value = 0.0;
        isTerminal = terminationTest();
        if(isTerminal)
            heuristicValue = value;
        else heuristicValue = heuristicEvaluation();
    }

    void softReset(TrisState source)
    {
        value = source.value;
        isTerminal = source.isTerminal;
        heuristicValue = source.heuristicValue;
    }

}



