package rotherator.ui;

import rotherator.Simulation;

import java.util.Map;

public interface RecomputeListener {
    public void recomputed(Map<String, Map<String, Simulation>> scenarios);
}
