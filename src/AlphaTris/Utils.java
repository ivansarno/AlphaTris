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

//Tuple state * value to use with API Stream
class StateWrap implements Comparable<StateWrap>
{

    public StateWrap(TrisState state, double value)
    {
        this.value = value;
        this.state = state;
    }



    final TrisState state;
    private final double value;




    public int compareTo(StateWrap o)
    {
        int result = Double.compare(this.value, o.value);
        if(result != 0)
            return result;
        return Double.compare(this.state.heuristicValue, o.state.heuristicValue);
    }
}

class Interruption extends RuntimeException
{

}