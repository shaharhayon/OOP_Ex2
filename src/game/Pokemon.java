package game;

import api.*;

public class Pokemon {
    private edge_data _edge;
    private double _value;
    private int _type;
    private DWGraph_DS.Position _pos;

    public Pokemon(edge_data _edge, double _value, int _type, DWGraph_DS.Position _pos) {
        this._edge = _edge;
        this._value = _value;
        this._type = _type;
        this._pos = _pos;
    }

    public edge_data get_edge() {
        return _edge;
    }

    public void set_edge(edge_data _edge) {
        this._edge = _edge;
    }

    public double get_value() {
        return _value;
    }

    public void set_value(double _value) {
        this._value = _value;
    }

    public int get_type() {
        return _type;
    }

    public void set_type(int _type) {
        this._type = _type;
    }

    public DWGraph_DS.Position get_pos() {
        return _pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pokemon)) return false;

        Pokemon pokemon = (Pokemon) o;

        if (Double.compare(pokemon._value, _value) != 0) return false;
        if (_type != pokemon._type) return false;
        if (!_edge.equals(pokemon._edge)) return false;
        return _pos.equals(pokemon._pos);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = _edge.hashCode();
        temp = Double.doubleToLongBits(_value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + _type;
        result = 31 * result + _pos.hashCode();
        return result;
    }

    public void set_pos(DWGraph_DS.Position _pos) {
        this._pos = _pos;
    }
}
