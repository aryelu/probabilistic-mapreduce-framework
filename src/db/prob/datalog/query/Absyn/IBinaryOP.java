package db.prob.datalog.query.Absyn;

/**
 * Created with IntelliJ IDEA.
 * User: arye
 * Date: 30/06/13
 * Time: 08:10
 * To change this template use File | Settings | File Templates.
 */
public interface IBinaryOP<T> {

    public T get_right();
    public T get_left();
}
