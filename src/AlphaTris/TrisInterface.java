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

import java.util.Scanner;

class TrisInterface
{
    public static void main(String[] args)
    {
        System.out.println("Inserire 2 numeri, la dimensione della griglia e la lunghezza della serie");
        int serie, size;
        Scanner input = new Scanner(System.in);
        size = input.nextInt();
        serie = input.nextInt();
        TrisState.init(serie, size);//initialize static members of the state
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
            engine.refresh(); //prepares a new iteration while waiting for input
            x = input.nextInt();
            y = input.nextInt();
            state.state[x][y] = -1; //sign user moves
            state.revalue(); //assign the new value

            long time = System.currentTimeMillis();
            state = engine.nextState(state); //compute move
            time = System.currentTimeMillis()- time;

            System.out.println();
            System.out.println("time to move: " + time + "ms");
            System.out.println("allocated memory: " + (runtime.totalMemory()>>20) + "MB");
            System.out.println("depth exploration: " + engine.maxDepth);
            System.out.println("successors explored per state: " + engine.maxElements);
            System.out.println();
            System.out.println(state);
        }
        //stampa esito
        if(state.value == 0)
            System.out.println("Draw!");
        else if(state.value > 0)
            System.out.println("You Loose :(");
        else if(state.value < 0)
            System.out.println("You Win :)");

    }

    //setup the engine 
    private static Engine getEngine()
    {
        if(TrisState.size > 10)
            return new Engine(15, 4);
        return new Engine(10, 6);
    }


}
