package AlphaTris;

import java.util.Scanner;


/**
 * Created by ivan on 11/06/2016.
 *
 */
class TrisInterface
{
    public static void main(String[] args)
    {
        System.out.println("Inserire 2 numeri, la dimensione della griglia e la lunghezza della serie");
        int serie, size;
        Scanner input = new Scanner(System.in);
        size = input.nextInt();
        serie = input.nextInt();
        TrisState.init(serie, size);
        game();

    }

    private static void game()
    {
        System.out.println("\nTu sei X");
        TrisState state = new TrisState();
        System.out.println(state);
        Engine engine = getEngine();
        Scanner input = new Scanner(System.in);
        int x,y;
        Runtime runtime = Runtime.getRuntime();
        while (!state.isTerminal)
        {
            System.out.println("Inserire 2 numeri, la riga e la colonna della casella da segnare");
            engine.refresh();
            x = input.nextInt();
            y = input.nextInt();
            state.state[x][y] = -1;
            state.revalue();
            long time = System.currentTimeMillis();
            state = engine.nextState(state);
            time = System.currentTimeMillis()- time;
            System.out.println();
            System.out.println("tempo elaborazione mossa: " + time + "ms");
            System.out.println("memoria totale allocata: " + (runtime.totalMemory()>>20) + "MB");
            System.out.println("profondità esplorazione: " + engine.maxDepth);
            System.out.println("Successori esplorati per nodo: " + engine.maxElements);
            System.out.println();
            System.out.println(state);
        }
        if(state.value == 0)
            System.out.println("Pareggio!");
        else if(state.value > 0)
            System.out.println("Hai Perso :(");
        else if(state.value < 0)
            System.out.println("Hai Vinto :)");

    }

    private static Engine getEngine()
    {
        int maxElements;
        int depth;
        if(TrisState.size <= 10)
        {
            maxElements = 25;
            depth = 8;
        }
        else if(TrisState.size > 10 && TrisState.size <= 25)
        {
            maxElements = 30;
            depth = 5;
        }
        else
        {
            maxElements = 50;
            depth = 3;
        }
        return new Engine(maxElements, depth);
    }


}
