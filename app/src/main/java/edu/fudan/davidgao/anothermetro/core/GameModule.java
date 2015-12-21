package edu.fudan.davidgao.anothermetro.core;

interface GameModule {
    void init(GameLogic logic) throws AlgorithmException;
    void start() throws AlgorithmException;
}
