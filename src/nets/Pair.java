package nets;

public class Pair<M,N> {

    public M getObject1() {
        return object1;
    }

    public void setObject1(M object1) {
        this.object1 = object1;
    }

    public N getObject2() {
        return object2;
    }

    public void setObject2(N object2) {
        this.object2 = object2;
    }

    private M object1;
    private N object2;

    public Pair(M object1, N object2){
        this.object1 = object1;
        this.object2 = object2;
    }

}
