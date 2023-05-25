package {{packageName}}.support;

public class Pair<F, S> {

  public final F first;
  public final S second;

  private Pair(F first, S second) {
    this.first = first;
    this.second = second;
  }

  public static <F, S> Pair<F, S> of(F first, S second) {
    return new Pair<>(first, second);
  }

  @Override
  public String toString() {
    return "Pair{" +
           "first=" + first +
           ", second=" + second +
           '}';
  }
}
