package AlphaTris;

import java.util.Scanner;


/**
 * Created by ivan on 11/06/2016.
 *
 */
public class TrisInterface
{
    public static void main(String[] args)
    {
        System.out.println("Inserire 2 numeri, la dimensione della griglia e la lunghezza della serie");
        int serie, size;
        Scanner in = new Scanner(System.in);
        size = in.nextInt();
        serie = in.nextInt();
        TrisState.init(serie, size);
        System.out.println("Tu sei X");
        game(size, serie);

    }

    static void game(int size, int serie)
    {
        TrisState t = new TrisState();
        System.out.println(t);
        IEngine engine = getEngine(size, serie, t);
        Scanner in = new Scanner(System.in);
        int x,y;
        while (!t.isTerminal())
        {
            System.out.println("Inserire 2 numeri, la riga e la colonna della casella da segnare");
            x = in.nextInt();
            y = in.nextInt();
            t.state[x][y] = -1;
            long time = System.currentTimeMillis();
            t = engine.nextState(t);
            time = System.currentTimeMillis()- time;
            System.out.println();
            /*System.out.println("tempo elaborazione mossa: " + time + "ms");
            System.out.println("profondità esplorazione: " + depth);
            System.out.println("nodi generati: " + TrisState.generated.get());
            System.out.println();*/
            System.out.println(t);
        }
        if(t.eval() == 0)
            System.out.println("Pareggio!");
        else if(t.eval() > 0)
            System.out.println("Hai perso :(");
        else if(t.eval() < 0)
            System.out.println("Hai Vinto :)");

    }

    static IEngine getEngine(int size, int serie, TrisState t)
    {
        int maxElements;
        int depth;
        if(size <= 10)
        {
            maxElements = 10;
            depth = 6;
        }
        else if(size > 10 && size < 25)
        {
            maxElements = 12;
            depth = 4;
        }
        else
        {
            maxElements = 20;
            depth = 2;
        }
        return new SoftPoolEngine(maxElements, depth);
    }

}
