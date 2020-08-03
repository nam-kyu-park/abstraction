package kr.co.sptek.abstraction.module.stream;

public class DPair<K, V> extends Object{
    private K key;
    private V value;

    public DPair(K key, V value) {
        this.key= key;
        this.value = value;
    }
    public void setValue(V value) { this.value = value; }
    public V getValue() { return this.value; }

    public void setKey(K value) { this.key= value; }
    public K getKey() { return this.key; }

    public String toString(){
        return "DPair: " +
                "[key:" + key.getClass().getName() + " - " + key + "] " +
                "[value:" + value.getClass().getName() + " - " + value;
    }
}
