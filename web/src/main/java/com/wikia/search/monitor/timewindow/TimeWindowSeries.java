package com.wikia.search.monitor.timewindow;

import java.util.ArrayList;
import java.util.List;

public class TimeWindowSeries<T> {
    private final List<T> buffer;
    private int size = 0;
    private int pos = 0;
    private int capacity;

    public TimeWindowSeries(int capacity) {
        buffer = new ArrayList<T>(capacity);
        this.capacity = capacity;
    }

    public void insert(T element) {
        buffer.add( (pos+size) % capacity, element );
        if ( size == capacity ) {
            pos++;
        } else {
            size++;
        }
    }

    public List<T> snapshot() {
        List<T> listView = new ArrayList<>( buffer.subList(pos, Math.min(pos+size, capacity)) );
        if ( pos+size > capacity ) {
            listView.addAll(buffer.subList(0, pos + size - capacity));
        }
        return listView;
    }
}
