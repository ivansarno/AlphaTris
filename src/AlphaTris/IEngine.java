package AlphaTris;

/**
 * Created by ivan on 15/06/2016.
 *
 */
public interface IEngine
{
     TrisState parallelNextState(TrisState current, int depth);
}
