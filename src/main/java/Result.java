public class Result {
    Long tmLife;
    Object value;


    public Result(Long tmLife, Object value) {
        this.tmLife = tmLife;
        this.value = value;
    }
/*
    public void setTmLife(Long tmLife) {
        this.tmLife = tmLife;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Long getTmLife() { return this.tmLife; }

    public Object getValue() { return this.value; }

 */

    public boolean isExpired() { return this.tmLife != 0L && System.currentTimeMillis() > this.tmLife; }

}
