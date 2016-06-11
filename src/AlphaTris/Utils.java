package AlphaTris;

import java.util.Comparator;

/**
 * Created by ivan on 11/06/2016.
 */
class StateWrap implements Comparable<StateWrap>
{

        public StateWrap(TrisState state, double value)
    {
        this.value = value;
        this.state = state;
    }



    final TrisState state;
    final double value;



    public int compareTo(StateWrap o)
    {
        return Double.compare(this.value, o.value);
    }
}

class ABT extends RuntimeException
{

}
