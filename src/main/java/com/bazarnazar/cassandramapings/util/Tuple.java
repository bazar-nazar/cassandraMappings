package com.bazarnazar.cassandramapings.util;

/**
 * Created by Bazar on 26.05.16.
 */
public class Tuple<T, U> {

    public T _1;
    public U _2;

    public Tuple(T _1, U _2) {
        this._1 = _1;
        this._2 = _2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Tuple))
            return false;

        Tuple tuple = (Tuple) o;

        if (_1 != null ? !_1.equals(tuple._1) : tuple._1 != null)
            return false;
        if (_2 != null ? !_2.equals(tuple._2) : tuple._2 != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _1 != null ? _1.hashCode() : 0;
        result = 31 * result + (_2 != null ? _2.hashCode() : 0);
        return result;
    }
}
