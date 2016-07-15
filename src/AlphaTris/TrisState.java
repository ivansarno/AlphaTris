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
    }

    TrisState(TrisState source)
    {
        this.state = arrayCopy(source.state);
        isTerminal = source.isTerminal;
        value = source.value;
        heuristicValue = source.heuristicValue;
    }

    //inizializza i campi statici
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
        StringBuilder builder = new StringBuilder(size*(size+2));
        builder.append("  ");
        if(size > 9)
            for(int i=0; i<size; i++)
                if(i>=10)
                    builder.append(" " + i/10);
                else  builder.append("  ");

        builder.append("\n  ");
        for(int i=0; i<size; i++)
            builder.append(" " + i%10);

        builder.append("\n");
        for(int i=0; i<size; i++)
        {
            if(i<10)
                builder.append(i + "  ");
            else builder.append(i + " ");

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




    //verifica se uno stato è terminale e setta anche value
    private boolean terminationTest()
    {
        int i, j;
        boolean noMoves = true;
        for(i=0; i < size; i++)
        {
            for(j=0; j<size;j++)
            {
                if(state[i][j] != 0)
                    if(checkSequence(i,j))
                    {
                        if(state[i][j] == -1)
                            value = minValue;
                        else value = maxValue;
                        return true;
                    }
                else noMoves = false;
            }

        }
        return noMoves;
    }



    private double heuristicEvaluation()
    {
        return columnsValue() + rowsValue() + diagonalsDXValue() + diagonalsSXValue();
    }


    //cerca sequenze vincenti/perdenti
    private boolean checkSequence(int i, int j)
    {
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


    //assegna  una sequenza il suo valore effettvo
    private static double weight(int sequenceValue, int length)
    {
        if(sequenceValue>0)
            return Math.pow(sequenceValue, 2)*length;
        return -Math.pow(-sequenceValue, 3.6)*length;
    }

    //calcola la valutazione euristica sulle colonne
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
                    zeroPrima += zeroDopo;
                    zeroDopo = 0;
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

    //calcola la valutazione euristica sulle righe
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
                    zeroPrima += zeroDopo;
                    zeroDopo = 0;
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

    //calcola la valutazione euristica sulle diagonali sinistre
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
                    zeroPrima += zeroDopo;
                    zeroDopo = 0;
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
                    zeroPrima += zeroDopo;
                    zeroDopo = 0;
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

    //calcola la valutazione euristica sulle diagonali destre
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
                    zeroPrima += zeroDopo;
                    zeroDopo = 0;
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
                    zeroPrima += zeroDopo;
                    zeroDopo = 0;
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

    //riporta uno stato alla configurazione di default
    void reset()
    {
        setZero(state);
        value = 0.0;
        isTerminal = false;
        heuristicValue = 0;
    }

    private static void setZero(byte[][] a)
    {
        for (byte[] anA : a)
            Arrays.fill(anA, (byte) 0);
    }

    //copia la matrice e i valori di un altro stato
    void reset(TrisState source)
    {
        arrayOverwrite(this.state, source.state);
        value = source.value;
        isTerminal = source.isTerminal;
        heuristicValue = source.heuristicValue;
    }

    //ricalcola i valori dello stato
    void revalue()
    {
        value = 0.0;
        isTerminal = terminationTest();
        if(isTerminal)
            heuristicValue = value;
        else heuristicValue = heuristicEvaluation();
    }

    //setta i valori di uno stato con quelli di un altro
    void revalue(TrisState source)
    {
        value = source.value;
        isTerminal = source.isTerminal;
        heuristicValue = source.heuristicValue;
    }

}



