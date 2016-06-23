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
        Scanner input = new Scanner(System.in);
        size = input.nextInt();
        serie = input.nextInt();
        TrisState.init(serie, size);
        IEngine engine = getEngine(size);
        game(engine);

    }

    static void game(IEngine engine)
    {
        System.out.println("Tu sei X");
        TrisState state = new TrisState();
        System.out.println(state);
        Scanner input = new Scanner(System.in);
        int x,y;
        while (!state.isTerminal)
        {
            System.out.println("Inserire 2 numeri, la riga e la colonna della casella da segnare");
            x = input.nextInt();
            y = input.nextInt();
            state.state[x][y] = -1;
            long time = System.currentTimeMillis();
            state = engine.nextState(state);
            time = System.currentTimeMillis()- time;
            System.out.println();
            System.out.println("tempo elaborazione mossa: " + time + "ms");
            /*System.out.println("profondità esplorazione: " + maxDepth);
            System.out.println("nodi generati: " + TrisState.generated.get());
            System.out.println();*/
            System.out.println(state);
        }
        if(state.value == 0)
            System.out.println("Pareggio!");
        else if(state.value > 0)
            System.out.println("Hai Perso :(");
        else if(state.value < 0)
            System.out.println("Hai Vinto :)");

    }

    static IEngine getEngine(int size)
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
        return new SimpleEngine(maxElements, depth);
    }

}
