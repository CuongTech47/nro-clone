package com.ngocrong.backend.lib;

/**
 * Lớp `KeyValue` đại diện cho một cặp khóa-giá trị chung chung với các phần tử bổ sung.
 *
 * @param <K> kiểu dữ liệu của khóa
 * @param <V> kiểu dữ liệu của giá trị
 */
public class KeyValue<K, V> {

    private K key;  // Khóa của cặp giá trị
    private V value;  // Giá trị của cặp khóa
    private Object[] elements;  // Các phần tử bổ sung có thể kèm theo

    /**
     * Constructor khởi tạo đối tượng KeyValue với khóa, giá trị và các phần tử bổ sung.
     *
     * @param key Khóa của cặp giá trị
     * @param value Giá trị của cặp khóa
     * @param elements Các phần tử bổ sung
     */
    public KeyValue(K key, V value, Object... elements) {
        this.key = key;
        this.value = value;
        this.elements = elements;
    }

    // Getter và Setter cho các thuộc tính

    /**
     * Lấy khóa của đối tượng KeyValue.
     *
     * @return khóa của đối tượng
     */
    public K getKey() {
        return key;
    }

    /**
     * Thiết lập khóa của đối tượng KeyValue.
     *
     * @param key khóa mới của đối tượng
     */
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * Lấy giá trị của đối tượng KeyValue.
     *
     * @return giá trị của đối tượng
     */
    public V getValue() {
        return value;
    }

    /**
     * Thiết lập giá trị của đối tượng KeyValue.
     *
     * @param value giá trị mới của đối tượng
     */
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Lấy các phần tử bổ sung của đối tượng KeyValue.
     *
     * @return các phần tử bổ sung
     */
    public Object[] getElements() {
        return elements;
    }

    /**
     * Thiết lập các phần tử bổ sung của đối tượng KeyValue.
     *
     * @param elements các phần tử bổ sung mới
     */
    public void setElements(Object[] elements) {
        this.elements = elements;
    }
}
