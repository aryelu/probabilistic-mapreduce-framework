package db.prob.datalog.query;

public interface Visitable
{
  public void accept(Visitor v);
}
